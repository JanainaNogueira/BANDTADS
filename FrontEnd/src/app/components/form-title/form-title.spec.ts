import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormTitle } from './form-title';

describe('FormTitle', () => {
  let component: FormTitle;
  let fixture: ComponentFixture<FormTitle>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormTitle]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FormTitle);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
