import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IAbsence, NewAbsence } from '../absence.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAbsence for edit and NewAbsenceFormGroupInput for create.
 */
type AbsenceFormGroupInput = IAbsence | PartialWithRequiredKeyOf<NewAbsence>;

type AbsenceFormDefaults = Pick<NewAbsence, 'id'>;

type AbsenceFormGroupContent = {
  id: FormControl<IAbsence['id'] | NewAbsence['id']>;
  date: FormControl<IAbsence['date']>;
  justification: FormControl<IAbsence['justification']>;
  note: FormControl<IAbsence['note']>;
  student: FormControl<IAbsence['student']>;
  session: FormControl<IAbsence['session']>;
};

export type AbsenceFormGroup = FormGroup<AbsenceFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AbsenceFormService {
  createAbsenceFormGroup(absence: AbsenceFormGroupInput = { id: null }): AbsenceFormGroup {
    const absenceRawValue = {
      ...this.getFormDefaults(),
      ...absence,
    };
    return new FormGroup<AbsenceFormGroupContent>({
      id: new FormControl(
        { value: absenceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      date: new FormControl(absenceRawValue.date),
      justification: new FormControl(absenceRawValue.justification),
      note: new FormControl(absenceRawValue.note),
      student: new FormControl(absenceRawValue.student),
      session: new FormControl(absenceRawValue.session),
    });
  }

  getAbsence(form: AbsenceFormGroup): IAbsence | NewAbsence {
    return form.getRawValue() as IAbsence | NewAbsence;
  }

  resetForm(form: AbsenceFormGroup, absence: AbsenceFormGroupInput): void {
    const absenceRawValue = { ...this.getFormDefaults(), ...absence };
    form.reset(
      {
        ...absenceRawValue,
        id: { value: absenceRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): AbsenceFormDefaults {
    return {
      id: null,
    };
  }
}
