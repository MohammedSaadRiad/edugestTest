import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { ITimetable } from '../timetable.model';

@Component({
  selector: 'jhi-timetable-detail',
  templateUrl: './timetable-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class TimetableDetailComponent {
  timetable = input<ITimetable | null>(null);

  previousState(): void {
    window.history.back();
  }
}
