import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RepopageComponent } from './repopage.component';

describe('RepopageComponent', () => {
  let component: RepopageComponent;
  let fixture: ComponentFixture<RepopageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RepopageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RepopageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
