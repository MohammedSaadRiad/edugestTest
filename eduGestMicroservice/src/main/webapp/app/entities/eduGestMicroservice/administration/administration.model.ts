import dayjs from 'dayjs/esm';
import { Genders } from 'app/entities/enumerations/genders.model';

export interface IAdministration {
  id: number;
  identifier?: string | null;
  birthDate?: dayjs.Dayjs | null;
  gender?: keyof typeof Genders | null;
  phoneNumber?: string | null;
  address?: string | null;
  type?: string | null;
  note?: string | null;
}

export type NewAdministration = Omit<IAdministration, 'id'> & { id: null };
