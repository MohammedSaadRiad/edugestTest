import { IStudent } from 'app/entities/eduGestMicroservice/student/student.model';
import { ISubject } from 'app/entities/eduGestMicroservice/subject/subject.model';

export interface IGrades {
  id: number;
  grade?: number | null;
  student?: IStudent | null;
  subject?: ISubject | null;
}

export type NewGrades = Omit<IGrades, 'id'> & { id: null };
