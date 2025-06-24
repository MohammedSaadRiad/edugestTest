import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ISchoolClass, NewSchoolClass } from '../school-class.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISchoolClass for edit and NewSchoolClassFormGroupInput for create.
 */
type SchoolClassFormGroupInput = ISchoolClass | PartialWithRequiredKeyOf<NewSchoolClass>;

type SchoolClassFormDefaults = Pick<NewSchoolClass, 'id' | 'teachers' | 'subjects'>;

type SchoolClassFormGroupContent = {
  id: FormControl<ISchoolClass['id'] | NewSchoolClass['id']>;
  name: FormControl<ISchoolClass['name']>;
  year: FormControl<ISchoolClass['year']>;
  timetable: FormControl<ISchoolClass['timetable']>;
  teachers: FormControl<ISchoolClass['teachers']>;
  subjects: FormControl<ISchoolClass['subjects']>;
};

export type SchoolClassFormGroup = FormGroup<SchoolClassFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SchoolClassFormService {
  createSchoolClassFormGroup(schoolClass: SchoolClassFormGroupInput = { id: null }): SchoolClassFormGroup {
    const schoolClassRawValue = {
      ...this.getFormDefaults(),
      ...schoolClass,
    };
    return new FormGroup<SchoolClassFormGroupContent>({
      id: new FormControl(
        { value: schoolClassRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(schoolClassRawValue.name, {
        validators: [Validators.required],
      }),
      year: new FormControl(schoolClassRawValue.year),
      timetable: new FormControl(schoolClassRawValue.timetable),
      teachers: new FormControl(schoolClassRawValue.teachers ?? []),
      subjects: new FormControl(schoolClassRawValue.subjects ?? []),
    });
  }

  getSchoolClass(form: SchoolClassFormGroup): ISchoolClass | NewSchoolClass {
    return form.getRawValue() as ISchoolClass | NewSchoolClass;
  }

  resetForm(form: SchoolClassFormGroup, schoolClass: SchoolClassFormGroupInput): void {
    const schoolClassRawValue = { ...this.getFormDefaults(), ...schoolClass };
    form.reset(
      {
        ...schoolClassRawValue,
        id: { value: schoolClassRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SchoolClassFormDefaults {
    return {
      id: null,
      teachers: [],
      subjects: [],
    };
  }
}
