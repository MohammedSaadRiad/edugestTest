import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ISession, NewSession } from '../session.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISession for edit and NewSessionFormGroupInput for create.
 */
type SessionFormGroupInput = ISession | PartialWithRequiredKeyOf<NewSession>;

type SessionFormDefaults = Pick<NewSession, 'id'>;

type SessionFormGroupContent = {
  id: FormControl<ISession['id'] | NewSession['id']>;
  day: FormControl<ISession['day']>;
  startTime: FormControl<ISession['startTime']>;
  endTime: FormControl<ISession['endTime']>;
  semester: FormControl<ISession['semester']>;
  room: FormControl<ISession['room']>;
  subject: FormControl<ISession['subject']>;
  teacher: FormControl<ISession['teacher']>;
  schoolClass: FormControl<ISession['schoolClass']>;
  timetable: FormControl<ISession['timetable']>;
};

export type SessionFormGroup = FormGroup<SessionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SessionFormService {
  createSessionFormGroup(session: SessionFormGroupInput = { id: null }): SessionFormGroup {
    const sessionRawValue = {
      ...this.getFormDefaults(),
      ...session,
    };
    return new FormGroup<SessionFormGroupContent>({
      id: new FormControl(
        { value: sessionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      day: new FormControl(sessionRawValue.day),
      startTime: new FormControl(sessionRawValue.startTime),
      endTime: new FormControl(sessionRawValue.endTime),
      semester: new FormControl(sessionRawValue.semester),
      room: new FormControl(sessionRawValue.room),
      subject: new FormControl(sessionRawValue.subject),
      teacher: new FormControl(sessionRawValue.teacher),
      schoolClass: new FormControl(sessionRawValue.schoolClass),
      timetable: new FormControl(sessionRawValue.timetable),
    });
  }

  getSession(form: SessionFormGroup): ISession | NewSession {
    return form.getRawValue() as ISession | NewSession;
  }

  resetForm(form: SessionFormGroup, session: SessionFormGroupInput): void {
    const sessionRawValue = { ...this.getFormDefaults(), ...session };
    form.reset(
      {
        ...sessionRawValue,
        id: { value: sessionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SessionFormDefaults {
    return {
      id: null,
    };
  }
}
