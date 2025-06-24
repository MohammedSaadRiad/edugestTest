import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAbsence } from '../absence.model';
import { AbsenceService } from '../service/absence.service';

const absenceResolve = (route: ActivatedRouteSnapshot): Observable<null | IAbsence> => {
  const id = route.params.id;
  if (id) {
    return inject(AbsenceService)
      .find(id)
      .pipe(
        mergeMap((absence: HttpResponse<IAbsence>) => {
          if (absence.body) {
            return of(absence.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default absenceResolve;
