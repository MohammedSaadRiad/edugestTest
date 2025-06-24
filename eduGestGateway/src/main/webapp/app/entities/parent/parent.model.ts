import dayjs from 'dayjs/esm';
import { IStudent } from 'app/entities/student/student.model';
import { Genders } from 'app/entities/enumerations/genders.model';

export interface IParent {
  id: number;
  identifier?: string | null;
  birthDate?: dayjs.Dayjs | null;
  gender?: keyof typeof Genders | null;
  phoneNumber?: string | null;
  address?: string | null;
  note?: string | null;
  students?: IStudent[] | null;
}

export type NewParent = Omit<IParent, 'id'> & { id: null };
