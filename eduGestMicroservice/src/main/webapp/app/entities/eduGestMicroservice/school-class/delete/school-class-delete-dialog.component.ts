import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ISchoolClass } from '../school-class.model';
import { SchoolClassService } from '../service/school-class.service';

@Component({
  templateUrl: './school-class-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class SchoolClassDeleteDialogComponent {
  schoolClass?: ISchoolClass;

  protected schoolClassService = inject(SchoolClassService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.schoolClassService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
