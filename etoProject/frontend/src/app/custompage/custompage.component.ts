import { Component, OnInit } from '@angular/core';
import {EndpointComponent} from '../controller/endpoint/endpoint.component';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-custompage',
  templateUrl: './custompage.component.html',
  styleUrls: ['./custompage.component.css']
})
export class CustompageComponent implements OnInit {

  format: string;
  subscribe: Subscription
  constructor(private Endpoint: EndpointComponent) {
  }

  ngOnInit(): void {
    this.subscribe = this.Endpoint._formatSubs.subscribe(format => this.format = format);
  }


}
