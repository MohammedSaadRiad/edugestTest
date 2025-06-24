import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISchoolClass, NewSchoolClass } from '../school-class.model';

export type PartialUpdateSchoolClass = Partial<ISchoolClass> & Pick<ISchoolClass, 'id'>;

export type EntityResponseType = HttpResponse<ISchoolClass>;
export type EntityArrayResponseType = HttpResponse<ISchoolClass[]>;

@Injectable({ providedIn: 'root' })
export class SchoolClassService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/school-classes', 'edugestmicroservice');

  create(schoolClass: NewSchoolClass): Observable<EntityResponseType> {
    return this.http.post<ISchoolClass>(this.resourceUrl, schoolClass, { observe: 'response' });
  }

  update(schoolClass: ISchoolClass): Observable<EntityResponseType> {
    return this.http.put<ISchoolClass>(`${this.resourceUrl}/${this.getSchoolClassIdentifier(schoolClass)}`, schoolClass, {
      observe: 'response',
    });
  }

  partialUpdate(schoolClass: PartialUpdateSchoolClass): Observable<EntityResponseType> {
    return this.http.patch<ISchoolClass>(`${this.resourceUrl}/${this.getSchoolClassIdentifier(schoolClass)}`, schoolClass, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISchoolClass>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISchoolClass[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSchoolClassIdentifier(schoolClass: Pick<ISchoolClass, 'id'>): number {
    return schoolClass.id;
  }

  compareSchoolClass(o1: Pick<ISchoolClass, 'id'> | null, o2: Pick<ISchoolClass, 'id'> | null): boolean {
    return o1 && o2 ? this.getSchoolClassIdentifier(o1) === this.getSchoolClassIdentifier(o2) : o1 === o2;
  }

  addSchoolClassToCollectionIfMissing<Type extends Pick<ISchoolClass, 'id'>>(
    schoolClassCollection: Type[],
    ...schoolClassesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const schoolClasses: Type[] = schoolClassesToCheck.filter(isPresent);
    if (schoolClasses.length > 0) {
      const schoolClassCollectionIdentifiers = schoolClassCollection.map(schoolClassItem => this.getSchoolClassIdentifier(schoolClassItem));
      const schoolClassesToAdd = schoolClasses.filter(schoolClassItem => {
        const schoolClassIdentifier = this.getSchoolClassIdentifier(schoolClassItem);
        if (schoolClassCollectionIdentifiers.includes(schoolClassIdentifier)) {
          return false;
        }
        schoolClassCollectionIdentifiers.push(schoolClassIdentifier);
        return true;
      });
      return [...schoolClassesToAdd, ...schoolClassCollection];
    }
    return schoolClassCollection;
  }
}
