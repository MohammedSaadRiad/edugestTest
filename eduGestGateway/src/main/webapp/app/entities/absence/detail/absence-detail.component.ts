import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatePipe } from 'app/shared/date';
import { IAbsence } from '../absence.model';

@Component({
  selector: 'jhi-absence-detail',
  templateUrl: './absence-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatePipe],
})
export class AbsenceDetailComponent {
  absence = input<IAbsence | null>(null);

  previousState(): void {
    window.history.back();
  }
}
