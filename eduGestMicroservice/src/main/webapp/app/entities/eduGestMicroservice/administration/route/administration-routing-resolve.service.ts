import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAdministration } from '../administration.model';
import { AdministrationService } from '../service/administration.service';

const administrationResolve = (route: ActivatedRouteSnapshot): Observable<null | IAdministration> => {
  const id = route.params.id;
  if (id) {
    return inject(AdministrationService)
      .find(id)
      .pipe(
        mergeMap((administration: HttpResponse<IAdministration>) => {
          if (administration.body) {
            return of(administration.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default administrationResolve;
