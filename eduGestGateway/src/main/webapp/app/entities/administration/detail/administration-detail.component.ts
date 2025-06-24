import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatePipe } from 'app/shared/date';
import { IAdministration } from '../administration.model';

@Component({
  selector: 'jhi-administration-detail',
  templateUrl: './administration-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatePipe],
})
export class AdministrationDetailComponent {
  administration = input<IAdministration | null>(null);

  previousState(): void {
    window.history.back();
  }
}
