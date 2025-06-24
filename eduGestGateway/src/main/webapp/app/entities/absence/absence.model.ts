import dayjs from 'dayjs/esm';
import { IStudent } from 'app/entities/student/student.model';
import { ISession } from 'app/entities/session/session.model';

export interface IAbsence {
  id: number;
  date?: dayjs.Dayjs | null;
  justification?: string | null;
  note?: string | null;
  student?: IStudent | null;
  session?: ISession | null;
}

export type NewAbsence = Omit<IAbsence, 'id'> & { id: null };
