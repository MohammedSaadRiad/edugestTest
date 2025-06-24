import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IGrades } from '../grades.model';
import { GradesService } from '../service/grades.service';

const gradesResolve = (route: ActivatedRouteSnapshot): Observable<null | IGrades> => {
  const id = route.params.id;
  if (id) {
    return inject(GradesService)
      .find(id)
      .pipe(
        mergeMap((grades: HttpResponse<IGrades>) => {
          if (grades.body) {
            return of(grades.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default gradesResolve;
