import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IGrades } from '../grades.model';
import { GradesService } from '../service/grades.service';

@Component({
  templateUrl: './grades-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class GradesDeleteDialogComponent {
  grades?: IGrades;

  protected gradesService = inject(GradesService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.gradesService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
