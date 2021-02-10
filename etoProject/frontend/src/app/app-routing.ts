import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {HomepageComponent} from './home/homepage/homepage.component';
import {CustompageComponent} from './custompage/custompage.component';


const routes: Routes = [
  {path: 'home', component: HomepageComponent},
  {path: '', redirectTo: 'home', pathMatch: 'full'},
  {path: 'custompage', component: CustompageComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule { }

