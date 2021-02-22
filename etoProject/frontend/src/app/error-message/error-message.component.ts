import {ChangeDetectorRef, Component, Injectable, Input, OnInit} from '@angular/core';
import {EndpointComponent} from '../controller/endpoint/endpoint.component';
import {BehaviorSubject} from 'rxjs';

@Component({
  selector: 'app-error-message',
  templateUrl: './error-message.component.html',
  styleUrls: ['./error-message.component.css']
})
@Injectable(
  {
    providedIn: 'root',
  }
)
export class ErrorMessageComponent implements OnInit {

  @Input() message:string = "";
  @Input() solution:string  = "";
  constructor(private cdrf: ChangeDetectorRef) { }

  ngOnInit(): void {
  }

  showErrorMessage() {
    this.cdrf.detectChanges()
    document.getElementById("errorMessage").style.display = 'block';
  }

  closeErrorMessage() {
    document.getElementById("errorMessage").style.display = 'none';
  }

}
