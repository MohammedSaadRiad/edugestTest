import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISession } from '../session.model';
import { SessionService } from '../service/session.service';

const sessionResolve = (route: ActivatedRouteSnapshot): Observable<null | ISession> => {
  const id = route.params.id;
  if (id) {
    return inject(SessionService)
      .find(id)
      .pipe(
        mergeMap((session: HttpResponse<ISession>) => {
          if (session.body) {
            return of(session.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default sessionResolve;
