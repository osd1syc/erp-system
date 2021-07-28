import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ErpGatewaySharedModule } from 'app/shared/shared.module';
import { RouterModule, Routes } from '@angular/router';
import { AboutModule } from 'app/bespoke/about/about.module';

const routes: Routes = [
  {
    path: 'about/finance-erp',
    loadChildren: () => import('./about/about.module').then(m => m.AboutModule),
  },
];

@NgModule({
  imports: [CommonModule, ErpGatewaySharedModule, RouterModule.forChild(routes), AboutModule],
  exports: [AboutModule],
})
export class BespokeModule {}
