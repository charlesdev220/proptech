export * from './autenticacin.service';
import { AutenticacinService } from './autenticacin.service';
export * from './inmuebles.service';
import { InmueblesService } from './inmuebles.service';
export * from './medios.service';
import { MediosService } from './medios.service';
export * from './perfil.service';
import { PerfilService } from './perfil.service';
export const APIS = [AutenticacinService, InmueblesService, MediosService, PerfilService];
