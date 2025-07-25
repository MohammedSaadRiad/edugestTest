import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ISubject, NewSubject } from '../subject.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISubject for edit and NewSubjectFormGroupInput for create.
 */
type SubjectFormGroupInput = ISubject | PartialWithRequiredKeyOf<NewSubject>;

type SubjectFormDefaults = Pick<NewSubject, 'id' | 'schoolClasses' | 'teachers'>;

type SubjectFormGroupContent = {
  id: FormControl<ISubject['id'] | NewSubject['id']>;
  name: FormControl<ISubject['name']>;
  code: FormControl<ISubject['code']>;
  description: FormControl<ISubject['description']>;
  schoolClasses: FormControl<ISubject['schoolClasses']>;
  teachers: FormControl<ISubject['teachers']>;
};

export type SubjectFormGroup = FormGroup<SubjectFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SubjectFormService {
  createSubjectFormGroup(subject: SubjectFormGroupInput = { id: null }): SubjectFormGroup {
    const subjectRawValue = {
      ...this.getFormDefaults(),
      ...subject,
    };
    return new FormGroup<SubjectFormGroupContent>({
      id: new FormControl(
        { value: subjectRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(subjectRawValue.name, {
        validators: [Validators.required],
      }),
      code: new FormControl(subjectRawValue.code, {
        validators: [Validators.required],
      }),
      description: new FormControl(subjectRawValue.description),
      schoolClasses: new FormControl(subjectRawValue.schoolClasses ?? []),
      teachers: new FormControl(subjectRawValue.teachers ?? []),
    });
  }

  getSubject(form: SubjectFormGroup): ISubject | NewSubject {
    return form.getRawValue() as ISubject | NewSubject;
  }

  resetForm(form: SubjectFormGroup, subject: SubjectFormGroupInput): void {
    const subjectRawValue = { ...this.getFormDefaults(), ...subject };
    form.reset(
      {
        ...subjectRawValue,
        id: { value: subjectRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SubjectFormDefaults {
    return {
      id: null,
      schoolClasses: [],
      teachers: [],
    };
  }
}
