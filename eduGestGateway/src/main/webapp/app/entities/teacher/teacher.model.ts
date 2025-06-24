import dayjs from 'dayjs/esm';
import { ISubject } from 'app/entities/subject/subject.model';
import { ISchoolClass } from 'app/entities/school-class/school-class.model';
import { Genders } from 'app/entities/enumerations/genders.model';

export interface ITeacher {
  id: number;
  identifier?: string | null;
  birthDate?: dayjs.Dayjs | null;
  qualification?: string | null;
  gender?: keyof typeof Genders | null;
  experience?: number | null;
  phoneNumber?: string | null;
  address?: string | null;
  type?: string | null;
  note?: string | null;
  subjects?: ISubject[] | null;
  schoolClasses?: ISchoolClass[] | null;
}

export type NewTeacher = Omit<ITeacher, 'id'> & { id: null };
