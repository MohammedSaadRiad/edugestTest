import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ParentResolve from './route/parent-routing-resolve.service';

const parentRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/parent.component').then(m => m.ParentComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/parent-detail.component').then(m => m.ParentDetailComponent),
    resolve: {
      parent: ParentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/parent-update.component').then(m => m.ParentUpdateComponent),
    resolve: {
      parent: ParentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/parent-update.component').then(m => m.ParentUpdateComponent),
    resolve: {
      parent: ParentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default parentRoute;
