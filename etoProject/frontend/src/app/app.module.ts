import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { FooterComponent } from './footer/footer/footer.component';
import { NavbarComponent } from './navbar/navbar/navbar.component';
import { EndpointComponent } from './controller/endpoint/endpoint.component';
import { HomepageComponent } from './home/homepage/homepage.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import {RouterModule} from '@angular/router';
import {AppRoutingModule} from './app-routing';
import {HttpClientModule} from '@angular/common/http';
import {FormsModule} from '@angular/forms';
import { LoadingComponent } from './loading/loading.component';
import { CustompageComponent } from './custompage/custompage.component';
import { FilepageComponent } from './custompage/filepage/filepage.component';
import { TablepageComponent } from './custompage/tablepage/tablepage.component';

@NgModule({
  declarations: [
    AppComponent,
    FooterComponent,
    NavbarComponent,
    HomepageComponent,
    LoadingComponent,
    CustompageComponent,
    FilepageComponent,
    TablepageComponent
  ],
    imports: [
        BrowserModule,
        NgbModule,
        RouterModule,
        AppRoutingModule,
        HttpClientModule,
        FormsModule,
    ],
  providers: [EndpointComponent],
  bootstrap: [AppComponent]
})
export class AppModule { }
