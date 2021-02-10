import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustompageComponent } from './custompage.component';

describe('CustompageComponent', () => {
  let component: CustompageComponent;
  let fixture: ComponentFixture<CustompageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CustompageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CustompageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
