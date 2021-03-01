import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {EndpointComponent} from '../../controller/endpoint/endpoint.component';
import {Router} from '@angular/router';
import {HttpErrorResponse} from '@angular/common/http';
import {ErrorMessageComponent} from '../../error-message/error-message.component';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css'],
})
export class HomepageComponent implements OnInit {

  constructor(private Endpoint: EndpointComponent, private _router: Router, private errorMessage:
  ErrorMessageComponent) { }

  fileUpload: File = null;
  rowHeader: string = null;
  columnHeader: string = null;
  loading = false;
  type: string = null;
  format: string;
  message: string = "";
  solution: string = "";
  fileProvided = false;
  subs: Subscription;

  ngOnInit(): void {
    this.subs = this.Endpoint._formatSubs.subscribe(format => this.format = format)
  }

  // tslint:disable-next-line:typedef
  openForm(type: string) {

    if (type === 'FileFormat') {
      document.getElementById('FileFormatModal').style.display = 'block';
    } else {
      document.getElementById('TableFormatModal').style.display = 'block';
    }
  }

  // tslint:disable-next-line:typedef
  handleFileInput(file: FileList) {
    this.fileUpload = file.item(0);
    this.fileProvided = true;
  }

  // tslint:disable-next-line:typedef
  closeForm(type: string) {
    this.fileProvided = false;
    if (type === 'FileFormat') {
      document.getElementById('FileFormatModal').style.display = 'none';
    } else {
      document.getElementById('TableFormatModal').style.display = 'none';
    }
  }

  // tslint:disable-next-line:typedef
  changeType(type: string) {
    if (type === 'custom') {
      document.getElementById('rowHeader').style.display = 'none';
      document.getElementById('columnHeader').style.display = 'none';
    } else {
      document.getElementById('rowHeader').style.display = 'block';
      document.getElementById('columnHeader').style.display = 'block';
    }
  }

  // tslint:disable-next-line:typedef
  async convertFile(format: string, type: string) {
    this.loading = true;
    this.fileProvided = false;
    if (type === 'automated') {

      await this.Endpoint.createAuto(this.fileUpload, format, this.columnHeader, this.rowHeader)
        .catch((err: HttpErrorResponse) => {
          if(err.error instanceof Error) {
            this.message = err.error.message;
            this.closeForm(format)
            this.errorMessage.showErrorMessage()
            this.rowHeader = null;
            this.columnHeader = null;
            return;
          } else {
            this.message = err.message;
            this.solution = err.error.solution;
            this.closeForm(format)
            this.errorMessage.showErrorMessage()
            this.rowHeader = null;
            this.columnHeader = null;
            return;
          }
        });
      this.Endpoint.changeFormat(format);
      this.loading = false;
      this.closeForm(format);
      this.rowHeader = null;
      this.columnHeader = null;
      this._router.navigate(['/repository']);
    } else {
      await this.Endpoint.initializeCustom(this.fileUpload, format)
        .catch((err: HttpErrorResponse) => {
        if(err.error instanceof Error) {
          this.message = err.error.message;
          this.closeForm(format)
          this.errorMessage.showErrorMessage()
          return;
        } else {
          this.message = err.message;
          this.solution = err.error.solution;
          this.closeForm(format)
          this.errorMessage.showErrorMessage()
          return;
        }
      });
      this.Endpoint.changeFormat(format);
      this.loading = false;
      this.closeForm(format);
      this._router.navigate(['/custompage']);
    }

  }

}
