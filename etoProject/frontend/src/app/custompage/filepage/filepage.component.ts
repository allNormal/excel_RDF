import { Component, OnInit } from '@angular/core';
import {EndpointComponent} from '../../controller/endpoint/endpoint.component';
import {WorkbookEndpoint, Worksheet} from '../../entity/workbook-endpoint';
import {plainToClass} from 'class-transformer';
import {CheckboxItem} from '../../entity/checkbox-item';
import {ChangeDetectorRef} from '@angular/core';

@Component({
  selector: 'app-filepage',
  templateUrl: './filepage.component.html',
  styleUrls: ['./filepage.component.css']
})
export class FilepageComponent implements OnInit {

  response: any
  changeCustom: string;
  modalCustom: CheckboxItem[] = [];
  tempColumn = new Map<string, CheckboxItem[]>();
  tempRow = new Map<string, CheckboxItem[]>();
  type: string;
  worksheetTemp: Array<Worksheet> = [];
  constructor(private endpoint: EndpointComponent, private cdrf:ChangeDetectorRef) { }

  async ngOnInit(): Promise<void> {
    let resp = await this.endpoint.getInitializeWorkbook('file');
    resp.subscribe(data => {
      this.response = plainToClass(WorkbookEndpoint, data);
      for(let i = 0; i<this.response[0].worksheets.length; i++) {
        let temp =  new Worksheet();
        temp.worksheetName = this.response[0].worksheets[i].worksheetName;
        temp.active = true;
        this.worksheetTemp.push(temp);
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
        //this.tempRow.set(this.response[0].worksheets[i].worksheetName, this.response[0].worksheets[i].rows);
      }
      console.log(this.response[0].worksheets)
      console.log(this.response[0].worksheets[0].worksheetName)
      console.log(this.tempColumn);
    });
  }

  checkActive(worksheetName: string) {
    console.log(this.worksheetTemp.find(x => x.worksheetName == worksheetName).active)
    return this.worksheetTemp.find(x => x.worksheetName == worksheetName).active;
  }

  modalCustomFunction(type: string, worksheetName: string) {
    this.type = type;
    if (type === "columns") {
      console.log(worksheetName)
      this.modalCustom = this.tempColumn.get(worksheetName);
    } else if (type === "rows") {

      this.modalCustom = this.tempRow.get(worksheetName);
    }
    document.getElementById('CustomModal').style.display = 'block';
  }

  closeForm() {
    document.getElementById('CustomModal').style.display = 'none';
    this.modalCustom = [];
  }

  saveForm(worksheetName: string) {
    document.getElementById('CustomModal').style.display = 'none';
    let temp: string[] = [];
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
  }

  changeStatus(status: string, worksheetName: string) {
    console.log(worksheetName)
    console.log(this.worksheetTemp)
    if(status === "disabled") {
      this.worksheetTemp.find(x => x.worksheetName == worksheetName).active = true;
    } else {
      this.worksheetTemp.find(x => x.worksheetName == worksheetName).active = false;
    }
    console.log(this.worksheetTemp.find(x => x.worksheetName == worksheetName).active)
    this.cdrf.detectChanges();
  }

}
