import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IGrades, NewGrades } from '../grades.model';

export type PartialUpdateGrades = Partial<IGrades> & Pick<IGrades, 'id'>;

export type EntityResponseType = HttpResponse<IGrades>;
export type EntityArrayResponseType = HttpResponse<IGrades[]>;

@Injectable({ providedIn: 'root' })
export class GradesService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/grades');

  create(grades: NewGrades): Observable<EntityResponseType> {
    return this.http.post<IGrades>(this.resourceUrl, grades, { observe: 'response' });
  }

  update(grades: IGrades): Observable<EntityResponseType> {
    return this.http.put<IGrades>(`${this.resourceUrl}/${this.getGradesIdentifier(grades)}`, grades, { observe: 'response' });
  }

  partialUpdate(grades: PartialUpdateGrades): Observable<EntityResponseType> {
    return this.http.patch<IGrades>(`${this.resourceUrl}/${this.getGradesIdentifier(grades)}`, grades, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IGrades>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IGrades[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getGradesIdentifier(grades: Pick<IGrades, 'id'>): number {
    return grades.id;
  }

  compareGrades(o1: Pick<IGrades, 'id'> | null, o2: Pick<IGrades, 'id'> | null): boolean {
    return o1 && o2 ? this.getGradesIdentifier(o1) === this.getGradesIdentifier(o2) : o1 === o2;
  }

  addGradesToCollectionIfMissing<Type extends Pick<IGrades, 'id'>>(
    gradesCollection: Type[],
    ...gradesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const grades: Type[] = gradesToCheck.filter(isPresent);
    if (grades.length > 0) {
      const gradesCollectionIdentifiers = gradesCollection.map(gradesItem => this.getGradesIdentifier(gradesItem));
      const gradesToAdd = grades.filter(gradesItem => {
        const gradesIdentifier = this.getGradesIdentifier(gradesItem);
        if (gradesCollectionIdentifiers.includes(gradesIdentifier)) {
          return false;
        }
        gradesCollectionIdentifiers.push(gradesIdentifier);
        return true;
      });
      return [...gradesToAdd, ...gradesCollection];
    }
    return gradesCollection;
  }
}
