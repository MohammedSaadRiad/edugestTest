import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IGrades } from '../grades.model';

@Component({
  selector: 'jhi-grades-detail',
  templateUrl: './grades-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class GradesDetailComponent {
  grades = input<IGrades | null>(null);

  previousState(): void {
    window.history.back();
  }
}
