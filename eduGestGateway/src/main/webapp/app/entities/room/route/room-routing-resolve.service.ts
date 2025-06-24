import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRoom } from '../room.model';
import { RoomService } from '../service/room.service';

const roomResolve = (route: ActivatedRouteSnapshot): Observable<null | IRoom> => {
  const id = route.params.id;
  if (id) {
    return inject(RoomService)
      .find(id)
      .pipe(
        mergeMap((room: HttpResponse<IRoom>) => {
          if (room.body) {
            return of(room.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default roomResolve;
