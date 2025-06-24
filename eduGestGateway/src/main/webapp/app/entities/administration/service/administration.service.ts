import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAdministration, NewAdministration } from '../administration.model';

export type PartialUpdateAdministration = Partial<IAdministration> & Pick<IAdministration, 'id'>;

type RestOf<T extends IAdministration | NewAdministration> = Omit<T, 'birthDate'> & {
  birthDate?: string | null;
};

export type RestAdministration = RestOf<IAdministration>;

export type NewRestAdministration = RestOf<NewAdministration>;

export type PartialUpdateRestAdministration = RestOf<PartialUpdateAdministration>;

export type EntityResponseType = HttpResponse<IAdministration>;
export type EntityArrayResponseType = HttpResponse<IAdministration[]>;

@Injectable({ providedIn: 'root' })
export class AdministrationService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/administrations');

  create(administration: NewAdministration): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(administration);
    return this.http
      .post<RestAdministration>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(administration: IAdministration): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(administration);
    return this.http
      .put<RestAdministration>(`${this.resourceUrl}/${this.getAdministrationIdentifier(administration)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(administration: PartialUpdateAdministration): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(administration);
    return this.http
      .patch<RestAdministration>(`${this.resourceUrl}/${this.getAdministrationIdentifier(administration)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestAdministration>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestAdministration[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getAdministrationIdentifier(administration: Pick<IAdministration, 'id'>): number {
    return administration.id;
  }

  compareAdministration(o1: Pick<IAdministration, 'id'> | null, o2: Pick<IAdministration, 'id'> | null): boolean {
    return o1 && o2 ? this.getAdministrationIdentifier(o1) === this.getAdministrationIdentifier(o2) : o1 === o2;
  }

  addAdministrationToCollectionIfMissing<Type extends Pick<IAdministration, 'id'>>(
    administrationCollection: Type[],
    ...administrationsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const administrations: Type[] = administrationsToCheck.filter(isPresent);
    if (administrations.length > 0) {
      const administrationCollectionIdentifiers = administrationCollection.map(administrationItem =>
        this.getAdministrationIdentifier(administrationItem),
      );
      const administrationsToAdd = administrations.filter(administrationItem => {
        const administrationIdentifier = this.getAdministrationIdentifier(administrationItem);
        if (administrationCollectionIdentifiers.includes(administrationIdentifier)) {
          return false;
        }
        administrationCollectionIdentifiers.push(administrationIdentifier);
        return true;
      });
      return [...administrationsToAdd, ...administrationCollection];
    }
    return administrationCollection;
  }

  protected convertDateFromClient<T extends IAdministration | NewAdministration | PartialUpdateAdministration>(
    administration: T,
  ): RestOf<T> {
    return {
      ...administration,
      birthDate: administration.birthDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restAdministration: RestAdministration): IAdministration {
    return {
      ...restAdministration,
      birthDate: restAdministration.birthDate ? dayjs(restAdministration.birthDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestAdministration>): HttpResponse<IAdministration> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestAdministration[]>): HttpResponse<IAdministration[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
