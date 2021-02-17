import { Component, OnInit } from '@angular/core';
import {EndpointComponent} from '../controller/endpoint/endpoint.component';
import {Subscription} from 'rxjs';
import {Router} from '@angular/router';

@Component({
  selector: 'app-repopage',
  templateUrl: './repopage.component.html',
  styleUrls: ['./repopage.component.css']
})
export class RepopageComponent implements OnInit {

  response: any;
  format: string;
  subscribe: Subscription
  repoName: string ="";

  constructor(private endpoint: EndpointComponent, private router:Router) { }

  async ngOnInit(): Promise<void> {
    this.subscribe = this.endpoint._formatSubs.subscribe(format => this.format = format);
    let resp = await this.endpoint.getAllRepositories(this.format);
    resp.subscribe(data =>
      this.response = data);
  }

  changeRepoType(repoType: string) {
    if(repoType === 'add') {
      document.getElementById('createBody').style.display = 'none';
      document.getElementById('createBody').classList.remove('active');
      document.getElementById('addBody').style.display = 'block';
      document.getElementById('addBody').classList.add('active');
      console.log(document.getElementById('addBody').classList.toString())
    } else {
      document.getElementById('createBody').style.display = 'block';
      document.getElementById('createBody').classList.add('active');
      document.getElementById('addBody').style.display = 'none';
      document.getElementById('addBody').classList.remove('active');
      console.log(document.getElementById('addBody').classList.toString())
    }
  }

  createRepoAndAddGraph() {
    this.endpoint.createRepoAndAddGraph(this.format, this.repoName);
    this.router.navigate(['/home'])
  }

  addGraphIntoRepo() {
    this.endpoint.addGraphIntoRepo(this.format, this.repoName);
    this.router.navigate(['/home'])
  }

}
