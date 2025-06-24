import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import RoomResolve from './route/room-routing-resolve.service';

const roomRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/room.component').then(m => m.RoomComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/room-detail.component').then(m => m.RoomDetailComponent),
    resolve: {
      room: RoomResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/room-update.component').then(m => m.RoomUpdateComponent),
    resolve: {
      room: RoomResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/room-update.component').then(m => m.RoomUpdateComponent),
    resolve: {
      room: RoomResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default roomRoute;
