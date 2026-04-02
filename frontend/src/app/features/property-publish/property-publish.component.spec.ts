import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { PropertyPublishComponent } from './property-publish.component';

describe('PropertyPublishComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, HttpClientTestingModule, PropertyPublishComponent]
    }).compileComponents();
  });

  it('should create the component', () => {
    const fixture = TestBed.createComponent(PropertyPublishComponent as any);
    const app = fixture.componentInstance as PropertyPublishComponent;
    expect(app).toBeTruthy();
  });

  it('form should be invalid when empty', () => {
    const fixture = TestBed.createComponent(PropertyPublishComponent as any);
    const app = fixture.componentInstance as PropertyPublishComponent;
    expect(app.propertyForm.invalid).toBeTrue();
  });
});
