import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISchoolClass } from '../school-class.model';
import { SchoolClassService } from '../service/school-class.service';

const schoolClassResolve = (route: ActivatedRouteSnapshot): Observable<null | ISchoolClass> => {
  const id = route.params.id;
  if (id) {
    return inject(SchoolClassService)
      .find(id)
      .pipe(
        mergeMap((schoolClass: HttpResponse<ISchoolClass>) => {
          if (schoolClass.body) {
            return of(schoolClass.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default schoolClassResolve;
