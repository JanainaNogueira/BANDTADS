import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagersList } from './managers-list';

describe('ManagersList', () => {
  let component: ManagersList;
  let fixture: ComponentFixture<ManagersList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManagersList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManagersList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
