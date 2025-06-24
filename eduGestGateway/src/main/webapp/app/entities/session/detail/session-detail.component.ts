import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { ISession } from '../session.model';

@Component({
  selector: 'jhi-session-detail',
  templateUrl: './session-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class SessionDetailComponent {
  session = input<ISession | null>(null);

  previousState(): void {
    window.history.back();
  }
}
