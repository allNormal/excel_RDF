import { Component, OnInit } from '@angular/core';
import {EndpointComponent} from '../../controller/endpoint/endpoint.component';
import {WorkbookEndpoint, Worksheet} from '../../entity/workbook-endpoint';
import {plainToClass} from 'class-transformer';
import {CheckboxItem} from '../../entity/checkbox-item';
import {ChangeDetectorRef} from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-filepage',
  templateUrl: './filepage.component.html',
  styleUrls: ['./filepage.component.css']
})
export class FilepageComponent implements OnInit {

  response: any
  changeCustom: string;
  modalCustom: CheckboxItem[] = [];
  type: string;
  worksheetTemp: Array<Worksheet> = [];
  constructor(private endpoint: EndpointComponent, private cdrf:ChangeDetectorRef,
              private _router: Router) { }

  async ngOnInit(): Promise<void> {
    let resp = await this.endpoint.getInitializeWorkbook('file');
    resp.subscribe(data => {
      this.response = plainToClass(WorkbookEndpoint, data);
      this.endpoint.workbookName = this.response[0].workbookName;
      for(let i = 0; i<this.response[0].worksheets.length; i++) {
        let temp =  new Worksheet();
        temp.worksheetName = this.response[0].worksheets[i].worksheetName;
        temp.active = true;
        let columns: Array<CheckboxItem> = [];
        for(let column of this.response[0].worksheets[i].columns) {
          let column1 =  new CheckboxItem();
          column1.value = column
          column1.isChecked = true;
          columns.push(column1)
          //this.modalCustom.push(column1);
        }
        temp.columns = columns;
        //this.tempColumn.set(this.response[0].worksheets[i].worksheetName, this.modalCustom);
        //this.modalCustom = [];
        let rows: Array<CheckboxItem> = [];
        for(let row of this.response[0].worksheets[i].rows) {
          let row1 =  new CheckboxItem();
          row1.value = row
          row1.isChecked = true;
          rows.push(row1);
          //this.modalCustom.push(row1);
        }
        temp.rows = rows;
        this.worksheetTemp.push(temp);
        //this.tempRow.set(this.response[0].worksheets[i].worksheetName, this.modalCustom);
        //this.modalCustom = [];
        //this.tempRow.set(this.response[0].worksheets[i].worksheetName, this.response[0].worksheets[i].rows);
      }
      console.log(this.worksheetTemp)
    });
  }

  checkActive(worksheetName: string) {
    return this.worksheetTemp.find(x => x.worksheetName == worksheetName).active;
  }

  modalCustomFunction(type: string, worksheetName: string) {
    console.log(worksheetName)
    this.type = type;
    if (type === "columns") {
      this.modalCustom = this.worksheetTemp.find(x => x.worksheetName === worksheetName).columns;
    } else if (type === "rows") {
      this.modalCustom = this.worksheetTemp.find(x => x.worksheetName === worksheetName).rows;
    }
    console.log(this.modalCustom);
    document.getElementById('CustomModal').style.display = 'block';
  }

  closeForm() {
    document.getElementById('CustomModal').style.display = 'none';
    this.modalCustom = [];
  }

  saveForm(worksheetName: string) {
    this.closeForm();
    /*
    let temp: Array<CheckboxItem> = [];
    if(this.type === 'columns') {
      for(let i = 0 ; i<this.modalCustom.length; i++) {
        if(this.modalCustom[i].isChecked) {
          temp.push(this.modalCustom[i].value)
        }
      }
      this.worksheetTemp.find(x => x.worksheetName = worksheetName).columns = temp;
    } else if(this.type === 'rows') {
      for(let i = 0 ; i<this.modalCustom.length; i++) {
        if(this.modalCustom[i].isChecked) {
          temp.push(this.modalCustom[i].value)
        }
      }
      for(let item of this.worksheetTemp) {
        if(item.worksheetName === worksheetName) {
          item.rows = temp;
        }
      }
    }
    console.log(this.worksheetTemp)
    this.modalCustom = []

     */
  }

  changeStatus(status: string, worksheetName: string) {
    if(status === "disabled") {
      this.worksheetTemp.find(x => x.worksheetName == worksheetName).active = true;
    } else {
      this.worksheetTemp.find(x => x.worksheetName == worksheetName).active = false;
    }
    this.cdrf.detectChanges();
  }

  async convert() {
    for(let worksheets of this.worksheetTemp) {
      worksheets.columns = worksheets.columns.filter(x => x.isChecked);
      worksheets.rows = worksheets.rows.filter(x => x.isChecked);
    }
    await this.endpoint.createCustom(this.worksheetTemp, 'file');
    this._router.navigate(['/repository']);
  }

}
