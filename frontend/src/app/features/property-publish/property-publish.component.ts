import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-property-publish',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf],
  template: `
    <div class="max-w-xl mx-auto p-6 bg-white rounded-lg shadow-md mt-10">
      <h2 class="text-2xl font-bold mb-4">Publicar Inmueble</h2>
      
      <form [formGroup]="propertyForm" (ngSubmit)="onSubmit()" class="space-y-4">
        <div>
          <label class="block text-sm font-medium">Título</label>
          <input type="text" formControlName="title" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm">
        </div>

        <div>
          <label class="block text-sm font-medium">Precio</label>
          <input type="number" formControlName="price" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm">
        </div>

        <div>
          <label class="block text-sm font-medium">Tipo</label>
          <select formControlName="type" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm">
            <option value="RENT">Alquiler</option>
            <option value="SALE">Venta</option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-medium">Imagen del inmueble</label>
          <input type="file" (change)="onFileSelected($event)" class="mt-1 block w-full">
        </div>

        <button type="submit" [disabled]="propertyForm.invalid || isUploading()" 
                class="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 disabled:opacity-50">
          {{ isUploading() ? 'Subiendo...' : 'Publicar Inmueble' }}
        </button>
      </form>
    </div>
  `
})
export class PropertyPublishComponent {
  private fb = inject(FormBuilder);
  private http = inject(HttpClient);

  propertyForm = this.fb.group({
    title: ['', Validators.required],
    price: [null, [Validators.required, Validators.min(0)]],
    type: ['RENT', Validators.required]
  });

  selectedFile = signal<File | null>(null);
  isUploading = signal(false);

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile.set(file);
    }
  }

  onSubmit() {
    if (this.propertyForm.valid && this.selectedFile()) {
      this.isUploading.set(true);
      const file = this.selectedFile() as File;

      // 1. Mock: Pedimos permiso al Backend
      this.http.get<{uploadUrl: string}>(`/api/v1/media/presigned-url?extension=jpg&contentType=${file.type}`)
        .subscribe({
          next: (res) => {
            // 2. Mock: Simular subida directa a AWS S3 (haremos un pequeño timeout en vez de envío HTTP real para la prueba UI)
            setTimeout(() => {
              // 3. Informamos al Backend de la creación del Inmueble
              const payload = {
                ...this.propertyForm.value,
                mediaUrl: res.uploadUrl.split('?')[0]
              };
              
              this.http.post('/api/v1/properties', payload).subscribe({
                 next: () => {
                    this.isUploading.set(false);
                    alert('¡Inmueble publicado con elegancia y éxito!');
                    this.propertyForm.reset();
                    this.selectedFile.set(null);
                 },
                 error: () => this.isUploading.set(false)
              });
            }, 1000); // Simulamos 1 segundo de subida a la red de S3
          },
          error: () => this.isUploading.set(false)
        });
    }
  }
}
