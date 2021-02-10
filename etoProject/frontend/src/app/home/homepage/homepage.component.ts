import { Component, OnInit } from '@angular/core';
import {Subscription} from 'rxjs';
import {EndpointComponent} from '../../controller/endpoint/endpoint.component';
import {Router} from '@angular/router';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css'],
})
export class HomepageComponent implements OnInit {

  constructor(private Endpoint: EndpointComponent, private _router: Router) { }

  fileUpload: File = null;
  rowHeader: string = null;
  columnHeader: string = null;
  hideSuccessMessage = false;
  loader = false;
  type: string = null;
  format: string;
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
  }

  // tslint:disable-next-line:typedef
  closeForm(type: string) {
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
    this.loader = true;
    if (type === 'automated') {

      await this.Endpoint.createAuto(this.fileUpload, format, this.columnHeader, this.rowHeader);
      this.loader = false;
      this.closeForm(format);
      this.hideSuccessMessage = true;

      this.rowHeader = null;
      this.columnHeader = null;
    } else {
      await this.Endpoint.initializeCustom(this.fileUpload, format);
      this.Endpoint.changeFormat(format);
      console.log(this.format)
      this.loader = false;
      this.closeForm(format);
      this._router.navigate(['/custompage']);
    }

    this.FadeOutLink();
  }

  // tslint:disable-next-line:typedef
  async convertFileCustom(format: string) {
    await this.Endpoint.createCustom(this.fileUpload);
  }

  // tslint:disable-next-line:typedef
  FadeOutLink() {
    setTimeout( () => {
      this.hideSuccessMessage = false;
    }, 2000);
  }

}
