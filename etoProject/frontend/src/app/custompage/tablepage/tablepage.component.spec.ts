import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TablepageComponent } from './tablepage.component';

describe('TablepageComponent', () => {
  let component: TablepageComponent;
  let fixture: ComponentFixture<TablepageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TablepageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TablepageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
