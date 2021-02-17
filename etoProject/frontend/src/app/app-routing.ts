import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {HomepageComponent} from './home/homepage/homepage.component';
import {CustompageComponent} from './custompage/custompage.component';
import {RepopageComponent} from './repopage/repopage.component';


const routes: Routes = [
  {path: 'home', component: HomepageComponent},
  {path: '', redirectTo: 'home', pathMatch: 'full'},
  {path: 'custompage', component: CustompageComponent},
  {path: 'repository', component: RepopageComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule { }

