import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IAbsence } from '../absence.model';
import { AbsenceService } from '../service/absence.service';

@Component({
  templateUrl: './absence-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class AbsenceDeleteDialogComponent {
  absence?: IAbsence;

  protected absenceService = inject(AbsenceService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.absenceService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
