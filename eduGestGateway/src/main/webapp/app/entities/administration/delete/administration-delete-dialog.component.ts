import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IAdministration } from '../administration.model';
import { AdministrationService } from '../service/administration.service';

@Component({
  templateUrl: './administration-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class AdministrationDeleteDialogComponent {
  administration?: IAdministration;

  protected administrationService = inject(AdministrationService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.administrationService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
