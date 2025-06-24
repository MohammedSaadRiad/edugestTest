import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import SchoolClassResolve from './route/school-class-routing-resolve.service';

const schoolClassRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/school-class.component').then(m => m.SchoolClassComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/school-class-detail.component').then(m => m.SchoolClassDetailComponent),
    resolve: {
      schoolClass: SchoolClassResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/school-class-update.component').then(m => m.SchoolClassUpdateComponent),
    resolve: {
      schoolClass: SchoolClassResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/school-class-update.component').then(m => m.SchoolClassUpdateComponent),
    resolve: {
      schoolClass: SchoolClassResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default schoolClassRoute;
