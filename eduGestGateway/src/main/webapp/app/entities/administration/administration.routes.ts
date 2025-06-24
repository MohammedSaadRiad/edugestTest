import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import AdministrationResolve from './route/administration-routing-resolve.service';

const administrationRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/administration.component').then(m => m.AdministrationComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/administration-detail.component').then(m => m.AdministrationDetailComponent),
    resolve: {
      administration: AdministrationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/administration-update.component').then(m => m.AdministrationUpdateComponent),
    resolve: {
      administration: AdministrationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/administration-update.component').then(m => m.AdministrationUpdateComponent),
    resolve: {
      administration: AdministrationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default administrationRoute;
