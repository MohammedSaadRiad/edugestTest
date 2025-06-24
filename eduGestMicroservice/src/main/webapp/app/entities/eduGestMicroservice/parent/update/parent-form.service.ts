import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IParent, NewParent } from '../parent.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IParent for edit and NewParentFormGroupInput for create.
 */
type ParentFormGroupInput = IParent | PartialWithRequiredKeyOf<NewParent>;

type ParentFormDefaults = Pick<NewParent, 'id' | 'students'>;

type ParentFormGroupContent = {
  id: FormControl<IParent['id'] | NewParent['id']>;
  identifier: FormControl<IParent['identifier']>;
  birthDate: FormControl<IParent['birthDate']>;
  gender: FormControl<IParent['gender']>;
  phoneNumber: FormControl<IParent['phoneNumber']>;
  address: FormControl<IParent['address']>;
  note: FormControl<IParent['note']>;
  students: FormControl<IParent['students']>;
};

export type ParentFormGroup = FormGroup<ParentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ParentFormService {
  createParentFormGroup(parent: ParentFormGroupInput = { id: null }): ParentFormGroup {
    const parentRawValue = {
      ...this.getFormDefaults(),
      ...parent,
    };
    return new FormGroup<ParentFormGroupContent>({
      id: new FormControl(
        { value: parentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      identifier: new FormControl(parentRawValue.identifier, {
        validators: [Validators.required],
      }),
      birthDate: new FormControl(parentRawValue.birthDate),
      gender: new FormControl(parentRawValue.gender),
      phoneNumber: new FormControl(parentRawValue.phoneNumber),
      address: new FormControl(parentRawValue.address),
      note: new FormControl(parentRawValue.note),
      students: new FormControl(parentRawValue.students ?? []),
    });
  }

  getParent(form: ParentFormGroup): IParent | NewParent {
    return form.getRawValue() as IParent | NewParent;
  }

  resetForm(form: ParentFormGroup, parent: ParentFormGroupInput): void {
    const parentRawValue = { ...this.getFormDefaults(), ...parent };
    form.reset(
      {
        ...parentRawValue,
        id: { value: parentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ParentFormDefaults {
    return {
      id: null,
      students: [],
    };
  }
}
