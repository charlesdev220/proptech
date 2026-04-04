import { ChangeDetectionStrategy, Component, inject, output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PerfilService, MediaDTO } from '../../../../api';

@Component({
  selector: 'app-document-uploader',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './document-uploader.html',
  styleUrls: ['./document-uploader.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DocumentUploaderComponent {
  private readonly perfilService = inject(PerfilService);

  readonly uploadSuccess = output<MediaDTO>();

  documentType: 'DNI' | 'PAYSLIP' = 'DNI';
  selectedFile = signal<File | null>(null);
  uploading = signal(false);
  error = signal<string | null>(null);

  onFileSelected(event: Event) {
    this.error.set(null);
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      
      // Validación estricta 5MB
      if (file.size > 5 * 1024 * 1024) {
        this.error.set('El archivo supera el límite de 5MB seguro.');
        this.selectedFile.set(null);
        return;
      }
      this.selectedFile.set(file);
    }
  }

  uploadDocument() {
    const file = this.selectedFile();
    if (!file) return;

    this.uploading.set(true);
    this.error.set(null);

    // Debido a OpenAPI Generator form-data handling, enviamos las partes
    this.perfilService.profileDocumentsPost(
      this.documentType as any,
      file, 
      'body' // observe
    ).subscribe({
      next: (response: MediaDTO) => {
        this.uploading.set(false);
        this.selectedFile.set(null);
        this.uploadSuccess.emit(response);
      },
      error: (err: any) => {
        console.error(err);
        this.uploading.set(false);
        this.error.set('Fallo al subir archivo. Verifica el backend o tu conexión.');
      }
    });
  }
}
