import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import GradesResolve from './route/grades-routing-resolve.service';

const gradesRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/grades.component').then(m => m.GradesComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/grades-detail.component').then(m => m.GradesDetailComponent),
    resolve: {
      grades: GradesResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/grades-update.component').then(m => m.GradesUpdateComponent),
    resolve: {
      grades: GradesResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/grades-update.component').then(m => m.GradesUpdateComponent),
    resolve: {
      grades: GradesResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default gradesRoute;
