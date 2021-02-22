import { Component, OnInit } from '@angular/core';
import {EndpointComponent} from '../../controller/endpoint/endpoint.component';
import {plainToClass} from 'class-transformer';
import {Table, WorkbookEndpoint, WorksheetTable} from '../../entity/workbook-endpoint';
import {ChangeDetectorRef} from '@angular/core';
import {CheckboxItem} from '../../entity/checkbox-item';
import {Router} from '@angular/router';
import {ErrorMessageComponent} from '../../error-message/error-message.component';
import {HttpErrorResponse} from '@angular/common/http';

@Component({
  selector: 'app-tablepage',
  templateUrl: './tablepage.component.html',
  styleUrls: ['./tablepage.component.css']
})
export class TablepageComponent implements OnInit {

  response: any;
  loading = false;
  worksheetTemp: Array<WorksheetTable> = [];
  worksheetsNameTemp: string =  "";
  rowHeaderTemp: string = "";
  columnHeaderTemp: string = "";
  initializeColumnRow: boolean =  false;
  tempColumn = new Map<string, CheckboxItem[]>();
  tempRow = new Map<string, CheckboxItem[]>();
  tableTemp: Table[] = [];
  tableNameTemp: string = "";
  modalCustom: CheckboxItem[] = [];
  type: string = "";
  message: string = "";
  solution: string = "";

  constructor(private endpoint: EndpointComponent, private cdrf: ChangeDetectorRef,
              private _router: Router, private errorMessage:ErrorMessageComponent) { }

  async ngOnInit(): Promise<void> {

    let resp = await this.endpoint.getInitializeWorkbook('table');
    resp.subscribe(data => {
      this.response = plainToClass(WorkbookEndpoint, data);
      for (let i = 0; i < this.response[0].worksheets.length; i++) {
        let temp = new WorksheetTable();
        temp.worksheetName = this.response[0].worksheets[i].worksheetName;
        temp.columnHeader = "0";
        temp.rowHeader = "0";
        this.worksheetTemp.push(temp);
      }
    },
      error => {
        this.message = error.message;
        this.solution = error.solution;
        this.errorMessage.showErrorMessage();
      })
  }

  openForm(worksheetName: string, modalId: string) {
    document.getElementById(modalId).style.display = 'block';
    this.worksheetsNameTemp =  worksheetName;
  }
  closeForm(modalId: string) {
    document.getElementById(modalId).style.display = 'none';
  }
  saveForm(worksheetName: string, rowHeaderFromAnotherWs: string) {
    for(let item of this.worksheetTemp) {
      if(item.worksheetName === worksheetName) {
        if(rowHeaderFromAnotherWs === '') {
          item.rowHeader = this.rowHeaderTemp;
        } else {
          item.columnsRowsFrom = rowHeaderFromAnotherWs;
          item.rowHeader = '-1';
        }
        if(this.columnHeaderTemp === '') {
          item.columnHeader = '0';
        } else {
          item.columnHeader = this.columnHeaderTemp;
        }
        break;
      }
    }
    this.columnHeaderTemp = "";
    this.rowHeaderTemp = "";
    this.worksheetsNameTemp = "";
    document.getElementById('rowHeaderInput').style.display = 'block';
    document.getElementById('CustomModal').style.display = 'none';
  }

  getWorksheetsWithoutTheActualOne() {
    return this.worksheetTemp.filter(x => x.worksheetName != this.worksheetsNameTemp);
  }

  dissapearingRowInput(worksheetName: string) {
    if(worksheetName === ''){
      document.getElementById('rowHeaderInput').style.display = 'block';
    } else {
      document.getElementById('rowHeaderInput').style.display = 'none';
    }
  }

  async initializeColumnRowHeader() {
    this.loading = true;
    await this.endpoint.initializeRowAndColumnHeader(this.worksheetTemp);
      let resp = await this.endpoint.getInitializeWorkbook('table');
      resp.subscribe(data => {
        this.response = plainToClass(WorkbookEndpoint, data);
        for (let i = 0; i < this.response[0].worksheets.length; i++) {

          for(let column of this.response[0].worksheets[i].columns) {
            let column1 =  new CheckboxItem();
            column1.value = column
            column1.isChecked = true;
            this.modalCustom.push(column1);
          }
          this.tempColumn.set(this.response[0].worksheets[i].worksheetName, this.modalCustom);
          this.modalCustom = [];

          for(let row of this.response[0].worksheets[i].rows) {
            let row1 =  new CheckboxItem();
            row1.value = row
            row1.isChecked = true;
            this.modalCustom.push(row1);
          }
          this.tempRow.set(this.response[0].worksheets[i].worksheetName, this.modalCustom);
          this.modalCustom = [];
        }
      },
        error => {
        this.message = error.message;
        this.solution = error.solution;
        this.errorMessage.showErrorMessage();
        })
    this.initializeColumnRow = true;
      this.loading = false;
  }

  changeStatus(status: string, worksheetName: string) {
    if(status === "disabled") {
      this.worksheetTemp.find(x => x.worksheetName == worksheetName).active = true;
    } else {
      this.worksheetTemp.find(x => x.worksheetName == worksheetName).active = false;
    }
    console.log(this.worksheetTemp.find(x => x.worksheetName == worksheetName).active)
    this.cdrf.detectChanges();
  }

  checkActive(worksheetName: string) {
    return this.worksheetTemp.find(x => x.worksheetName == worksheetName).active;
  }

  getTable(worksheetName: string) {
    this.tableTemp = [];
    for(let item of this.worksheetTemp) {
      if(item.worksheetName === worksheetName) {
        this.tableTemp = item.table;
      }
    }
  }

  saveTable(worksheetName: string) {
    for(let item of this.worksheetTemp) {
      if(item.worksheetName === worksheetName) {
        let table =  new Table();
        table.name = this.tableNameTemp;
        let columns: CheckboxItem[] = []
        if(this.tempColumn.get(worksheetName) !== undefined) {
          for(let column of this.tempColumn.get(worksheetName)) {
            let temp =  new CheckboxItem();
            temp.value = column.value;
            temp.isChecked = true;
            columns.push(temp);
          }
          table.columns = columns;
        }
        let rows: CheckboxItem[] = []
        if(this.tempRow.get(worksheetName) !== undefined) {
          for(let row of this.tempRow.get(worksheetName)) {
            let temp =  new CheckboxItem();
            temp.value =  row.value;
            temp.isChecked = true;
            rows.push(temp);
          }
          table.rows = rows;
        }
        item.table.push(table);
        break;
      }
    }
    this.tableNameTemp = "";
    this.closeForm('CustomModalAddTable');
  }


  modalCustomFunction(tableName: string, type: string, worksheetName: string) {
    if (type === "columns") {
      let tables = this.worksheetTemp.find(x => x.worksheetName === worksheetName).table
      let table =  tables.find(x => x.name === tableName).columns
      this.modalCustom = table;
    } else if (type === "rows") {

      let tables = this.worksheetTemp.find(x => x.worksheetName === worksheetName).table
      let table =  tables.find(x => x.name === tableName).rows
      this.modalCustom = table;
    }
    this.tableNameTemp = "";
    document.getElementById('CustomModalRowColumn').style.display = 'block';
  }

  saveColumnRow(type: string, worksheetName: string, tableName: string) {
    this.closeForm('CustomModalRowColumn')
  }

  RemoveTable(tableName: string, worksheetName: string) {
    for(let worksheet of this.worksheetTemp) {
      if(worksheet.worksheetName === worksheetName) {
        worksheet.table = worksheet.table.filter(x => x.name != tableName);
        break;
      }
    }
    this.getTable(worksheetName)
  }

  async Convert() {
    this.loading = true;
    for(let worksheet of this.worksheetTemp) {
      for(let table of worksheet.table) {
        table.columns = table.columns.filter(column => column.isChecked);
        table.rows =  table.rows.filter(row => row.isChecked);
      }
    }

    await this.endpoint.createCustom(this.worksheetTemp, 'table')
      .catch((err:HttpErrorResponse) => {
        if(err instanceof Error) {
          this.message = err.message;
          this.errorMessage.showErrorMessage();
          return;
        } else {
          this.message = err.error.message;
          this.solution = err.error.solution;
          this.errorMessage.showErrorMessage();
          return;
        }
      });
    this._router.navigate(['/repository']);
  }

}
