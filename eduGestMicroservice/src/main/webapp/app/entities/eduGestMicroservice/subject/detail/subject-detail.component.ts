import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { ISubject } from '../subject.model';

@Component({
  selector: 'jhi-subject-detail',
  templateUrl: './subject-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class SubjectDetailComponent {
  subject = input<ISubject | null>(null);

  previousState(): void {
    window.history.back();
  }
}
