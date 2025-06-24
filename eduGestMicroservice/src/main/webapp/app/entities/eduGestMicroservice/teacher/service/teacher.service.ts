import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITeacher, NewTeacher } from '../teacher.model';

export type PartialUpdateTeacher = Partial<ITeacher> & Pick<ITeacher, 'id'>;

type RestOf<T extends ITeacher | NewTeacher> = Omit<T, 'birthDate'> & {
  birthDate?: string | null;
};

export type RestTeacher = RestOf<ITeacher>;

export type NewRestTeacher = RestOf<NewTeacher>;

export type PartialUpdateRestTeacher = RestOf<PartialUpdateTeacher>;

export type EntityResponseType = HttpResponse<ITeacher>;
export type EntityArrayResponseType = HttpResponse<ITeacher[]>;

@Injectable({ providedIn: 'root' })
export class TeacherService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/teachers', 'edugestmicroservice');

  create(teacher: NewTeacher): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(teacher);
    return this.http
      .post<RestTeacher>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(teacher: ITeacher): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(teacher);
    return this.http
      .put<RestTeacher>(`${this.resourceUrl}/${this.getTeacherIdentifier(teacher)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(teacher: PartialUpdateTeacher): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(teacher);
    return this.http
      .patch<RestTeacher>(`${this.resourceUrl}/${this.getTeacherIdentifier(teacher)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTeacher>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTeacher[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTeacherIdentifier(teacher: Pick<ITeacher, 'id'>): number {
    return teacher.id;
  }

  compareTeacher(o1: Pick<ITeacher, 'id'> | null, o2: Pick<ITeacher, 'id'> | null): boolean {
    return o1 && o2 ? this.getTeacherIdentifier(o1) === this.getTeacherIdentifier(o2) : o1 === o2;
  }

  addTeacherToCollectionIfMissing<Type extends Pick<ITeacher, 'id'>>(
    teacherCollection: Type[],
    ...teachersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const teachers: Type[] = teachersToCheck.filter(isPresent);
    if (teachers.length > 0) {
      const teacherCollectionIdentifiers = teacherCollection.map(teacherItem => this.getTeacherIdentifier(teacherItem));
      const teachersToAdd = teachers.filter(teacherItem => {
        const teacherIdentifier = this.getTeacherIdentifier(teacherItem);
        if (teacherCollectionIdentifiers.includes(teacherIdentifier)) {
          return false;
        }
        teacherCollectionIdentifiers.push(teacherIdentifier);
        return true;
      });
      return [...teachersToAdd, ...teacherCollection];
    }
    return teacherCollection;
  }

  protected convertDateFromClient<T extends ITeacher | NewTeacher | PartialUpdateTeacher>(teacher: T): RestOf<T> {
    return {
      ...teacher,
      birthDate: teacher.birthDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restTeacher: RestTeacher): ITeacher {
    return {
      ...restTeacher,
      birthDate: restTeacher.birthDate ? dayjs(restTeacher.birthDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTeacher>): HttpResponse<ITeacher> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestTeacher[]>): HttpResponse<ITeacher[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
