import { Component, OnInit } from '@angular/core';
import {EndpointComponent} from '../../controller/endpoint/endpoint.component';
import {plainToClass} from 'class-transformer';
import {Table, WorkbookEndpoint, WorksheetTable} from '../../entity/workbook-endpoint';
import {ChangeDetectorRef} from '@angular/core';
import {CheckboxItem} from '../../entity/checkbox-item';

@Component({
  selector: 'app-tablepage',
  templateUrl: './tablepage.component.html',
  styleUrls: ['./tablepage.component.css']
})
export class TablepageComponent implements OnInit {

  response: any;
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
  constructor(private endpoint: EndpointComponent, private cdrf: ChangeDetectorRef) { }

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
    })
  }

  openForm(worksheetName: string, modalId: string) {
    document.getElementById(modalId).style.display = 'block';
    this.worksheetsNameTemp =  worksheetName;
  }
  closeForm(modalId: string) {
    document.getElementById(modalId).style.display = 'none';
  }
  saveForm(worksheetName: string) {
    for(let item of this.worksheetTemp) {
      if(item.worksheetName === worksheetName) {
        console.log(item.worksheetName)
        console.log(worksheetName)
        item.rowHeader = this.rowHeaderTemp;
        item.columnHeader = this.columnHeaderTemp;
        break;
      }
    }
    console.log(this.worksheetTemp)
    this.columnHeaderTemp = "";
    this.rowHeaderTemp = "";
    this.worksheetsNameTemp = "";
    document.getElementById('CustomModal').style.display = 'none';
  }

  async initializeColumnRowHeader() {
    let err = false;
    await this.endpoint.initializeRowAndColumnHeader(this.worksheetTemp).catch(err => err = true);
    if(err) {

    } else {
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
      })
      this.initializeColumnRow = true;
    }
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
    console.log(worksheetName)
    this.tableTemp = [];
    for(let item of this.worksheetTemp) {
      if(item.worksheetName === worksheetName) {
        this.tableTemp = item.table;
        console.log(this.tableTemp)
      }
    }
  }

  saveTable(worksheetName: string) {
    console.log(this.worksheetTemp)
    for(let item of this.worksheetTemp) {
      if(item.worksheetName === worksheetName) {
        let table =  new Table();
        table.name = this.tableNameTemp;
        let columns: CheckboxItem[] = []
        for(let column of this.tempColumn.get(worksheetName)) {
          let temp =  new CheckboxItem();
          temp.value = column.value;
          temp.isChecked = true;
          columns.push(temp);
        }
        table.columns = columns;

        let rows: CheckboxItem[] = []
        for(let row of this.tempRow.get(worksheetName)) {
          let temp =  new CheckboxItem();
          temp.value =  row.value;
          temp.isChecked = true;
          rows.push(temp);
        }
        table.rows = rows;
        item.table.push(table);
        break;
      }
    }
    this.tableNameTemp = "";
    this.closeForm('CustomModalAddTable');
  }


  modalCustomFunction(tableName: string, type: string, worksheetName: string) {
    console.log(tableName)
    if (type === "columns") {
      console.log(worksheetName)
      let tables = this.worksheetTemp.find(x => x.worksheetName === worksheetName).table
      let table =  tables.find(x => x.name === tableName).columns
      this.modalCustom = table;
      console.log(this.modalCustom)
    } else if (type === "rows") {

      let tables = this.worksheetTemp.find(x => x.worksheetName === worksheetName).table
      let table =  tables.find(x => x.name === tableName).rows
      this.modalCustom = table;
      console.log(this.modalCustom)
    }
    this.tableNameTemp = "";
    document.getElementById('CustomModalRowColumn').style.display = 'block';
  }

  saveColumnRow(type: string, worksheetName: string, tableName: string) {
    /*
    let temp: string[] = [];
    for(let item of this.modalCustom) {
      if(item.isChecked){
        temp.push(item.value);
      }
    }
    if(type === 'column'){
      for(let item of this.worksheetTemp) {
        if(item.worksheetName === worksheetName) {
          for(let table of item.table) {
            if(table.name === tableName) {
              table.columns = temp;
              break;
            }
          }
          break;
        }
      }
    } else {
      for(let item of this.worksheetTemp) {
        if(item.worksheetName === worksheetName) {
          for(let table of item.table) {
            if(table.name === tableName) {
              table.rows = temp;
              break;
            }
          }
          break;
        }
      }
    }
    this.modalCustom = [];

     */
    this.closeForm('CustomModalRowColumn')
  }

  RemoveTable(tableName: string, worksheetName: string) {
    for(let worksheet of this.worksheetTemp) {
      if(worksheet.worksheetName === worksheetName) {
        console.log("find")
        console.log(worksheet.table)
        worksheet.table = worksheet.table.filter(x => x.name != tableName);
        console.log(tableName)
        console.log(worksheet.table)
        break;
      }
    }
    this.getTable(worksheetName)
  }

  Convert() {
    console.log(this.worksheetTemp)
  }

}
