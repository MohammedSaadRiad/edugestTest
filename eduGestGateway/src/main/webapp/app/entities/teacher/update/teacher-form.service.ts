import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITeacher, NewTeacher } from '../teacher.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITeacher for edit and NewTeacherFormGroupInput for create.
 */
type TeacherFormGroupInput = ITeacher | PartialWithRequiredKeyOf<NewTeacher>;

type TeacherFormDefaults = Pick<NewTeacher, 'id' | 'subjects' | 'schoolClasses'>;

type TeacherFormGroupContent = {
  id: FormControl<ITeacher['id'] | NewTeacher['id']>;
  identifier: FormControl<ITeacher['identifier']>;
  birthDate: FormControl<ITeacher['birthDate']>;
  qualification: FormControl<ITeacher['qualification']>;
  gender: FormControl<ITeacher['gender']>;
  experience: FormControl<ITeacher['experience']>;
  phoneNumber: FormControl<ITeacher['phoneNumber']>;
  address: FormControl<ITeacher['address']>;
  type: FormControl<ITeacher['type']>;
  note: FormControl<ITeacher['note']>;
  subjects: FormControl<ITeacher['subjects']>;
  schoolClasses: FormControl<ITeacher['schoolClasses']>;
};

export type TeacherFormGroup = FormGroup<TeacherFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TeacherFormService {
  createTeacherFormGroup(teacher: TeacherFormGroupInput = { id: null }): TeacherFormGroup {
    const teacherRawValue = {
      ...this.getFormDefaults(),
      ...teacher,
    };
    return new FormGroup<TeacherFormGroupContent>({
      id: new FormControl(
        { value: teacherRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      identifier: new FormControl(teacherRawValue.identifier, {
        validators: [Validators.required],
      }),
      birthDate: new FormControl(teacherRawValue.birthDate),
      qualification: new FormControl(teacherRawValue.qualification),
      gender: new FormControl(teacherRawValue.gender),
      experience: new FormControl(teacherRawValue.experience),
      phoneNumber: new FormControl(teacherRawValue.phoneNumber),
      address: new FormControl(teacherRawValue.address),
      type: new FormControl(teacherRawValue.type),
      note: new FormControl(teacherRawValue.note),
      subjects: new FormControl(teacherRawValue.subjects ?? []),
      schoolClasses: new FormControl(teacherRawValue.schoolClasses ?? []),
    });
  }

  getTeacher(form: TeacherFormGroup): ITeacher | NewTeacher {
    return form.getRawValue() as ITeacher | NewTeacher;
  }

  resetForm(form: TeacherFormGroup, teacher: TeacherFormGroupInput): void {
    const teacherRawValue = { ...this.getFormDefaults(), ...teacher };
    form.reset(
      {
        ...teacherRawValue,
        id: { value: teacherRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TeacherFormDefaults {
    return {
      id: null,
      subjects: [],
      schoolClasses: [],
    };
  }
}
