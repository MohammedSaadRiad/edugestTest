import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IParent } from '../parent.model';
import { ParentService } from '../service/parent.service';

@Component({
  templateUrl: './parent-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ParentDeleteDialogComponent {
  parent?: IParent;

  protected parentService = inject(ParentService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.parentService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
