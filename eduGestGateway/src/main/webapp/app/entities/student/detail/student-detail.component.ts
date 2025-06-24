import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatePipe } from 'app/shared/date';
import { IStudent } from '../student.model';

@Component({
  selector: 'jhi-student-detail',
  templateUrl: './student-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatePipe],
})
export class StudentDetailComponent {
  student = input<IStudent | null>(null);

  previousState(): void {
    window.history.back();
  }
}
