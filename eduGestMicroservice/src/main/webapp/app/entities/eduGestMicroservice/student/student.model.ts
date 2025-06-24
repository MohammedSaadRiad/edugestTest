import dayjs from 'dayjs/esm';
import { ISchoolClass } from 'app/entities/eduGestMicroservice/school-class/school-class.model';
import { IParent } from 'app/entities/eduGestMicroservice/parent/parent.model';
import { Genders } from 'app/entities/enumerations/genders.model';

export interface IStudent {
  id: number;
  identifier?: string | null;
  birthDate?: dayjs.Dayjs | null;
  gender?: keyof typeof Genders | null;
  nationality?: string | null;
  phoneNumber?: string | null;
  address?: string | null;
  note?: string | null;
  schoolClass?: ISchoolClass | null;
  parents?: IParent[] | null;
}

export type NewStudent = Omit<IStudent, 'id'> & { id: null };
