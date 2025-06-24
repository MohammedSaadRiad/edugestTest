import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { ISchoolClass } from '../school-class.model';

@Component({
  selector: 'jhi-school-class-detail',
  templateUrl: './school-class-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class SchoolClassDetailComponent {
  schoolClass = input<ISchoolClass | null>(null);

  previousState(): void {
    window.history.back();
  }
}
