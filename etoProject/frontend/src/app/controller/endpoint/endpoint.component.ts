import {Injectable} from '@angular/core';
import {HttpClient, HttpRequest} from '@angular/common/http';
import {BehaviorSubject} from 'rxjs';
import {WorksheetTable} from '../../entity/workbook-endpoint';
@Injectable(
  {
    providedIn: 'root',
  }
)
export class EndpointComponent {

  private baseURL = 'http://localhost:8080/';
  // tslint:disable-next-line:variable-name
  private _format = new BehaviorSubject("TableFormat")
  _formatSubs = this._format.asObservable();
  constructor(private http: HttpClient) { }

  // tslint:disable-next-line:typedef
  public async createAuto(file: File, format: string, rowHeader: string, columnHeader: string) {

    const formData: FormData = new FormData();

    formData.append('file', file);
    formData.append('format', format);
    if (rowHeader !== null) {
      formData.append('rowHeader', rowHeader);
      formData.append('columnHeader', columnHeader);
    }

    return await this.http.post(this.baseURL + 'automated', formData).toPromise();

  }

  // tslint:disable-next-line:typedef
  public async initializeCustom(file: File, format: string) {
    const formData: FormData = new FormData();
    formData.append('file', file);
    formData.append('format', format);
    return await this.http.post(this.baseURL + 'custom/initialize', formData).toPromise();
  }

  public async initializeRowAndColumnHeader(worksheets: Array<WorksheetTable>) {
    console.log(JSON.stringify(worksheets))
    const headers = { 'content-type': 'application/json'}
    return await this.http.post(this.baseURL + 'custom/initialize/crheader', JSON.stringify(worksheets), {'headers':headers}).toPromise();
  }

  // tslint:disable-next-line:typedef
  public async createCustom(restriction){

  }

  // tslint:disable-next-line:typedef
  public getInitializeWorkbook(format: string) {
    return this.http.get(this.baseURL + format + '/workbook')

  }


  changeFormat(format: string) {
    this._format.next(format);
  }
}
