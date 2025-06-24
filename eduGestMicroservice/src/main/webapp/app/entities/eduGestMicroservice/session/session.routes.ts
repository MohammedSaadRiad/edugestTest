import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import SessionResolve from './route/session-routing-resolve.service';

const sessionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/session.component').then(m => m.SessionComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/session-detail.component').then(m => m.SessionDetailComponent),
    resolve: {
      session: SessionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/session-update.component').then(m => m.SessionUpdateComponent),
    resolve: {
      session: SessionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/session-update.component').then(m => m.SessionUpdateComponent),
    resolve: {
      session: SessionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default sessionRoute;
