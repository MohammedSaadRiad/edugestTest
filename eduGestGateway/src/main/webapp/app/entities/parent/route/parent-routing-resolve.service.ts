import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IParent } from '../parent.model';
import { ParentService } from '../service/parent.service';

const parentResolve = (route: ActivatedRouteSnapshot): Observable<null | IParent> => {
  const id = route.params.id;
  if (id) {
    return inject(ParentService)
      .find(id)
      .pipe(
        mergeMap((parent: HttpResponse<IParent>) => {
          if (parent.body) {
            return of(parent.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default parentResolve;
