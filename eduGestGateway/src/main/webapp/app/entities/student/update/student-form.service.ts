import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IStudent, NewStudent } from '../student.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IStudent for edit and NewStudentFormGroupInput for create.
 */
type StudentFormGroupInput = IStudent | PartialWithRequiredKeyOf<NewStudent>;

type StudentFormDefaults = Pick<NewStudent, 'id' | 'parents'>;

type StudentFormGroupContent = {
  id: FormControl<IStudent['id'] | NewStudent['id']>;
  identifier: FormControl<IStudent['identifier']>;
  birthDate: FormControl<IStudent['birthDate']>;
  gender: FormControl<IStudent['gender']>;
  nationality: FormControl<IStudent['nationality']>;
  phoneNumber: FormControl<IStudent['phoneNumber']>;
  address: FormControl<IStudent['address']>;
  note: FormControl<IStudent['note']>;
  schoolClass: FormControl<IStudent['schoolClass']>;
  parents: FormControl<IStudent['parents']>;
};

export type StudentFormGroup = FormGroup<StudentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class StudentFormService {
  createStudentFormGroup(student: StudentFormGroupInput = { id: null }): StudentFormGroup {
    const studentRawValue = {
      ...this.getFormDefaults(),
      ...student,
    };
    return new FormGroup<StudentFormGroupContent>({
      id: new FormControl(
        { value: studentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      identifier: new FormControl(studentRawValue.identifier, {
        validators: [Validators.required],
      }),
      birthDate: new FormControl(studentRawValue.birthDate),
      gender: new FormControl(studentRawValue.gender),
      nationality: new FormControl(studentRawValue.nationality),
      phoneNumber: new FormControl(studentRawValue.phoneNumber),
      address: new FormControl(studentRawValue.address),
      note: new FormControl(studentRawValue.note),
      schoolClass: new FormControl(studentRawValue.schoolClass),
      parents: new FormControl(studentRawValue.parents ?? []),
    });
  }

  getStudent(form: StudentFormGroup): IStudent | NewStudent {
    return form.getRawValue() as IStudent | NewStudent;
  }

  resetForm(form: StudentFormGroup, student: StudentFormGroupInput): void {
    const studentRawValue = { ...this.getFormDefaults(), ...student };
    form.reset(
      {
        ...studentRawValue,
        id: { value: studentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): StudentFormDefaults {
    return {
      id: null,
      parents: [],
    };
  }
}
