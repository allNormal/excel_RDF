import { Component, OnInit } from '@angular/core';
import {EndpointComponent} from '../../controller/endpoint/endpoint.component';
import {WorkbookEndpoint, Worksheet} from '../../entity/workbook-endpoint';
import {plainToClass} from 'class-transformer';
import {CheckboxItem} from '../../entity/checkbox-item';
import {ChangeDetectorRef} from '@angular/core';
import {Router} from '@angular/router';
import {ErrorMessageComponent} from '../../error-message/error-message.component';
import {HttpErrorResponse} from '@angular/common/http';

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
  message: string = "";
  solution: string = "";
  loading = false;
  constructor(private endpoint: EndpointComponent, private cdrf:ChangeDetectorRef,
              private _router: Router, private errorMessage: ErrorMessageComponent) { }

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
        }
        temp.columns = columns;
        let rows: Array<CheckboxItem> = [];
        for(let row of this.response[0].worksheets[i].rows) {
          let row1 =  new CheckboxItem();
          row1.value = row
          row1.isChecked = true;
          rows.push(row1);
        }
        temp.rows = rows;
        this.worksheetTemp.push(temp);
      }
    },
      error => {
      this.message = error.message;
      this.solution = error.solution;
      this.errorMessage.showErrorMessage();
      });
  }

  checkActive(worksheetName: string) {
    return this.worksheetTemp.find(x => x.worksheetName == worksheetName).active;
  }

  modalCustomFunction(type: string, worksheetName: string) {
    this.type = type;
    if (type === "columns") {
      this.modalCustom = this.worksheetTemp.find(x => x.worksheetName === worksheetName).columns;
    } else if (type === "rows") {
      this.modalCustom = this.worksheetTemp.find(x => x.worksheetName === worksheetName).rows;
    }
    document.getElementById('CustomModal').style.display = 'block';
  }

  closeForm() {
    document.getElementById('CustomModal').style.display = 'none';
    this.modalCustom = [];
  }

  saveForm(worksheetName: string) {
    this.closeForm();
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
    this.loading = true;
    for(let worksheets of this.worksheetTemp) {
      worksheets.columns = worksheets.columns.filter(x => x.isChecked);
      worksheets.rows = worksheets.rows.filter(x => x.isChecked);
    }
    await this.endpoint.createCustom(this.worksheetTemp, 'file')
      .catch((err:HttpErrorResponse) => {
        if(err instanceof Error) {
          this.message = err.message;
          this.errorMessage.showErrorMessage();
          return;
        } else {
          this.message = err.message;
          this.solution = err.error.solution;
          this.errorMessage.showErrorMessage();
          return;
        }
      });
    this._router.navigate(['/repository']);
  }

}
