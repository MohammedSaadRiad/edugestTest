import { ITimetable } from 'app/entities/eduGestMicroservice/timetable/timetable.model';
import { ITeacher } from 'app/entities/eduGestMicroservice/teacher/teacher.model';
import { ISubject } from 'app/entities/eduGestMicroservice/subject/subject.model';

export interface ISchoolClass {
  id: number;
  name?: string | null;
  year?: number | null;
  timetable?: ITimetable | null;
  teachers?: ITeacher[] | null;
  subjects?: ISubject[] | null;
}

export type NewSchoolClass = Omit<ISchoolClass, 'id'> & { id: null };
