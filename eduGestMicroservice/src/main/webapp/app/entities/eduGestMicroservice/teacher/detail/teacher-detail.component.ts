import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatePipe } from 'app/shared/date';
import { ITeacher } from '../teacher.model';

@Component({
  selector: 'jhi-teacher-detail',
  templateUrl: './teacher-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatePipe],
})
export class TeacherDetailComponent {
  teacher = input<ITeacher | null>(null);

  previousState(): void {
    window.history.back();
  }
}
