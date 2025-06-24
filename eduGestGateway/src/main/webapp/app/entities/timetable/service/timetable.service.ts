import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITimetable, NewTimetable } from '../timetable.model';

export type PartialUpdateTimetable = Partial<ITimetable> & Pick<ITimetable, 'id'>;

export type EntityResponseType = HttpResponse<ITimetable>;
export type EntityArrayResponseType = HttpResponse<ITimetable[]>;

@Injectable({ providedIn: 'root' })
export class TimetableService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/timetables');

  create(timetable: NewTimetable): Observable<EntityResponseType> {
    return this.http.post<ITimetable>(this.resourceUrl, timetable, { observe: 'response' });
  }

  update(timetable: ITimetable): Observable<EntityResponseType> {
    return this.http.put<ITimetable>(`${this.resourceUrl}/${this.getTimetableIdentifier(timetable)}`, timetable, { observe: 'response' });
  }

  partialUpdate(timetable: PartialUpdateTimetable): Observable<EntityResponseType> {
    return this.http.patch<ITimetable>(`${this.resourceUrl}/${this.getTimetableIdentifier(timetable)}`, timetable, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITimetable>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITimetable[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTimetableIdentifier(timetable: Pick<ITimetable, 'id'>): number {
    return timetable.id;
  }

  compareTimetable(o1: Pick<ITimetable, 'id'> | null, o2: Pick<ITimetable, 'id'> | null): boolean {
    return o1 && o2 ? this.getTimetableIdentifier(o1) === this.getTimetableIdentifier(o2) : o1 === o2;
  }

  addTimetableToCollectionIfMissing<Type extends Pick<ITimetable, 'id'>>(
    timetableCollection: Type[],
    ...timetablesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const timetables: Type[] = timetablesToCheck.filter(isPresent);
    if (timetables.length > 0) {
      const timetableCollectionIdentifiers = timetableCollection.map(timetableItem => this.getTimetableIdentifier(timetableItem));
      const timetablesToAdd = timetables.filter(timetableItem => {
        const timetableIdentifier = this.getTimetableIdentifier(timetableItem);
        if (timetableCollectionIdentifiers.includes(timetableIdentifier)) {
          return false;
        }
        timetableCollectionIdentifiers.push(timetableIdentifier);
        return true;
      });
      return [...timetablesToAdd, ...timetableCollection];
    }
    return timetableCollection;
  }
}
