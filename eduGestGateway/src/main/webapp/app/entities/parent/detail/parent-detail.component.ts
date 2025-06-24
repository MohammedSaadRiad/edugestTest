import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatePipe } from 'app/shared/date';
import { IParent } from '../parent.model';

@Component({
  selector: 'jhi-parent-detail',
  templateUrl: './parent-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatePipe],
})
export class ParentDetailComponent {
  parent = input<IParent | null>(null);

  previousState(): void {
    window.history.back();
  }
}
