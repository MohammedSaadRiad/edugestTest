import { ISchoolClass } from 'app/entities/school-class/school-class.model';
import { ITeacher } from 'app/entities/teacher/teacher.model';

export interface ISubject {
  id: number;
  name?: string | null;
  code?: string | null;
  description?: string | null;
  schoolClasses?: ISchoolClass[] | null;
  teachers?: ITeacher[] | null;
}

export type NewSubject = Omit<ISubject, 'id'> & { id: null };
