import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { Genders } from 'app/entities/enumerations/genders.model';
import { IAdministration } from '../administration.model';
import { AdministrationService } from '../service/administration.service';
import { AdministrationFormGroup, AdministrationFormService } from './administration-form.service';

@Component({
  selector: 'jhi-administration-update',
  templateUrl: './administration-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class AdministrationUpdateComponent implements OnInit {
  isSaving = false;
  administration: IAdministration | null = null;
  gendersValues = Object.keys(Genders);

  protected administrationService = inject(AdministrationService);
  protected administrationFormService = inject(AdministrationFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AdministrationFormGroup = this.administrationFormService.createAdministrationFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ administration }) => {
      this.administration = administration;
      if (administration) {
        this.updateForm(administration);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const administration = this.administrationFormService.getAdministration(this.editForm);
    if (administration.id !== null) {
      this.subscribeToSaveResponse(this.administrationService.update(administration));
    } else {
      this.subscribeToSaveResponse(this.administrationService.create(administration));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAdministration>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(administration: IAdministration): void {
    this.administration = administration;
    this.administrationFormService.resetForm(this.editForm, administration);
  }
}
