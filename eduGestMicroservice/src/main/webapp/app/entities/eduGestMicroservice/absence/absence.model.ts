import dayjs from 'dayjs/esm';
import { IStudent } from 'app/entities/eduGestMicroservice/student/student.model';
import { ISession } from 'app/entities/eduGestMicroservice/session/session.model';

export interface IAbsence {
  id: number;
  date?: dayjs.Dayjs | null;
  justification?: string | null;
  note?: string | null;
  student?: IStudent | null;
  session?: ISession | null;
}

export type NewAbsence = Omit<IAbsence, 'id'> & { id: null };
