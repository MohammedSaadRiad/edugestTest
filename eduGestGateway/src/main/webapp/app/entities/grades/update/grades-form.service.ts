import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IGrades, NewGrades } from '../grades.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IGrades for edit and NewGradesFormGroupInput for create.
 */
type GradesFormGroupInput = IGrades | PartialWithRequiredKeyOf<NewGrades>;

type GradesFormDefaults = Pick<NewGrades, 'id'>;

type GradesFormGroupContent = {
  id: FormControl<IGrades['id'] | NewGrades['id']>;
  grade: FormControl<IGrades['grade']>;
  student: FormControl<IGrades['student']>;
  subject: FormControl<IGrades['subject']>;
};

export type GradesFormGroup = FormGroup<GradesFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class GradesFormService {
  createGradesFormGroup(grades: GradesFormGroupInput = { id: null }): GradesFormGroup {
    const gradesRawValue = {
      ...this.getFormDefaults(),
      ...grades,
    };
    return new FormGroup<GradesFormGroupContent>({
      id: new FormControl(
        { value: gradesRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      grade: new FormControl(gradesRawValue.grade),
      student: new FormControl(gradesRawValue.student),
      subject: new FormControl(gradesRawValue.subject),
    });
  }

  getGrades(form: GradesFormGroup): IGrades | NewGrades {
    return form.getRawValue() as IGrades | NewGrades;
  }

  resetForm(form: GradesFormGroup, grades: GradesFormGroupInput): void {
    const gradesRawValue = { ...this.getFormDefaults(), ...grades };
    form.reset(
      {
        ...gradesRawValue,
        id: { value: gradesRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): GradesFormDefaults {
    return {
      id: null,
    };
  }
}
