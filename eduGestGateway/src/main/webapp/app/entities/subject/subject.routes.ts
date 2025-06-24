import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import SubjectResolve from './route/subject-routing-resolve.service';

const subjectRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/subject.component').then(m => m.SubjectComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/subject-detail.component').then(m => m.SubjectDetailComponent),
    resolve: {
      subject: SubjectResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/subject-update.component').then(m => m.SubjectUpdateComponent),
    resolve: {
      subject: SubjectResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/subject-update.component').then(m => m.SubjectUpdateComponent),
    resolve: {
      subject: SubjectResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default subjectRoute;
