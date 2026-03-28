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
          <label class="block text-sm font-medium">Imágenes o Vídeo (Máx 5, Vídeos < 15MB)</label>
          <input type="file" multiple (change)="onFileSelected($event)" accept="image/*,video/mp4" class="mt-1 block w-full">
          <p class="text-red-500 text-xs mt-1" *ngIf="errorMessage()">{{ errorMessage() }}</p>
        </div>

        <button type="submit" [disabled]="propertyForm.invalid || isUploading() || selectedFiles().length === 0" 
                class="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 disabled:opacity-50">
          {{ isUploading() ? 'Procesando y Subiendo...' : 'Publicar Inmueble' }}
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

  selectedFiles = signal<File[]>([]);
  isUploading = signal(false);
  errorMessage = signal<string | null>(null);

  onFileSelected(event: any) {
    this.errorMessage.set(null);
    const files: File[] = Array.from(event.target.files);
    
    if (files.length > 5) {
      this.errorMessage.set('Máximo 5 archivos permitidos.');
      return;
    }

    for (let f of files) {
      if (f.type.startsWith('video/') && f.size > 15 * 1024 * 1024) {
        this.errorMessage.set('El vídeo excede los 15 MB.');
        return;
      }
    }
    
    this.selectedFiles.set(files);
  }

  async compressImage(file: File): Promise<Blob> {
    if (!file.type.startsWith('image/')) return file;

    return new Promise((resolve) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = (event: any) => {
        const img = new Image();
        img.src = event.target.result;
        img.onload = () => {
          const canvas = document.createElement('canvas');
          const maxDim = 1280;
          let width = img.width;
          let height = img.height;

          if (width > height && width > maxDim) {
            height *= maxDim / width;
            width = maxDim;
          } else if (height > maxDim) {
            width *= maxDim / height;
            height = maxDim;
          }

          canvas.width = width;
          canvas.height = height;
          const ctx = canvas.getContext('2d');
          ctx?.drawImage(img, 0, 0, width, height);
          
          canvas.toBlob((blob) => {
            resolve(blob || file);
          }, 'image/jpeg', 0.70);
        };
      };
    });
  }

  async onSubmit() {
    if (this.propertyForm.valid && this.selectedFiles().length > 0) {
      this.isUploading.set(true);
      
      const mediaUrls: string[] = [];

      try {
        // En un entorno de producción, esto debería usar Promese.all o secuencial estricto.
        for (let file of this.selectedFiles()) {
          const compressedBlob = await this.compressImage(file);
          const formData = new FormData();
          formData.append('file', compressedBlob, file.name);

          const res = await this.http.post<{url: string}>('/api/v1/media/upload', formData).toPromise();
          if (res?.url) mediaUrls.push(res.url);
        }

        const payload = {
          ...this.propertyForm.value,
          mediaUrls: mediaUrls
        };
        
        await this.http.post('/api/v1/properties', payload).toPromise();
        
        this.isUploading.set(false);
        alert('¡Inmueble publicado con medios comprimidos localmente!');
        this.propertyForm.reset();
        this.selectedFiles.set([]);
      } catch (err) {
        this.isUploading.set(false);
        this.errorMessage.set('Error al subir los archivos.');
      }
    }
  }
}
