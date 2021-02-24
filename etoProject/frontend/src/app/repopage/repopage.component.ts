import {ChangeDetectorRef, Component, NgZone, OnInit} from '@angular/core';
import {EndpointComponent} from '../controller/endpoint/endpoint.component';
import {Subscription} from 'rxjs';
import {Router} from '@angular/router';
import {ErrorMessageComponent} from '../error-message/error-message.component';
import {HttpErrorResponse} from '@angular/common/http';

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
  message: string = "";
  solution: string = "";
  loading = true;
  constructor(private endpoint: EndpointComponent, private router:Router, private errorMessage: ErrorMessageComponent) { }

  async ngOnInit(): Promise<void> {

    this.subscribe = this.endpoint._formatSubs.subscribe(format => this.format = format);
    let resp = await this.endpoint.getAllRepositories(this.format);
    resp.subscribe(data => {
        this.response = data;
        this.loading = false;
    },
      error => {
        this.message = "failure in connecting to graphDB";
        this.solution = "please start graphDB to use this functions";
        this.loading = false;
        this.errorMessage.showErrorMessage();
        //this.errorMessage.changeMessage(error.message);
        //this.errorMessage.changeSolution("please start graph db");
      });
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
    this.endpoint.addGraphIntoRepo(this.format, this.repoName)
      .catch((err:HttpErrorResponse) => {
        if(err instanceof Error) {
          this.message = "error in adding graph" + err.error;
          this.errorMessage.showErrorMessage();
          return;
        } else {
          this.message = "error in adding graph " + err.error;
          this.solution = err.error.solution;
          this.errorMessage.showErrorMessage();
          return;
        }
      });
    this.router.navigate(['/home'])
  }

}
