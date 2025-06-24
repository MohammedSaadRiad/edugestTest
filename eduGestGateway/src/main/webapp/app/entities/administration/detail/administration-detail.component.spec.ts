import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { AdministrationDetailComponent } from './administration-detail.component';

describe('Administration Management Detail Component', () => {
  let comp: AdministrationDetailComponent;
  let fixture: ComponentFixture<AdministrationDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdministrationDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./administration-detail.component').then(m => m.AdministrationDetailComponent),
              resolve: { administration: () => of({ id: 12826 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(AdministrationDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AdministrationDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load administration on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', AdministrationDetailComponent);

      // THEN
      expect(instance.administration()).toEqual(expect.objectContaining({ id: 12826 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
