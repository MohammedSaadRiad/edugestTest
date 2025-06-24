import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITimetable, NewTimetable } from '../timetable.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITimetable for edit and NewTimetableFormGroupInput for create.
 */
type TimetableFormGroupInput = ITimetable | PartialWithRequiredKeyOf<NewTimetable>;

type TimetableFormDefaults = Pick<NewTimetable, 'id'>;

type TimetableFormGroupContent = {
  id: FormControl<ITimetable['id'] | NewTimetable['id']>;
  semestre: FormControl<ITimetable['semestre']>;
};

export type TimetableFormGroup = FormGroup<TimetableFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TimetableFormService {
  createTimetableFormGroup(timetable: TimetableFormGroupInput = { id: null }): TimetableFormGroup {
    const timetableRawValue = {
      ...this.getFormDefaults(),
      ...timetable,
    };
    return new FormGroup<TimetableFormGroupContent>({
      id: new FormControl(
        { value: timetableRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      semestre: new FormControl(timetableRawValue.semestre),
    });
  }

  getTimetable(form: TimetableFormGroup): ITimetable | NewTimetable {
    return form.getRawValue() as ITimetable | NewTimetable;
  }

  resetForm(form: TimetableFormGroup, timetable: TimetableFormGroupInput): void {
    const timetableRawValue = { ...this.getFormDefaults(), ...timetable };
    form.reset(
      {
        ...timetableRawValue,
        id: { value: timetableRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TimetableFormDefaults {
    return {
      id: null,
    };
  }
}
