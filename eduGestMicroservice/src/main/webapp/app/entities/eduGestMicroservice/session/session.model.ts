import { IRoom } from 'app/entities/eduGestMicroservice/room/room.model';
import { ISubject } from 'app/entities/eduGestMicroservice/subject/subject.model';
import { ITeacher } from 'app/entities/eduGestMicroservice/teacher/teacher.model';
import { ISchoolClass } from 'app/entities/eduGestMicroservice/school-class/school-class.model';
import { ITimetable } from 'app/entities/eduGestMicroservice/timetable/timetable.model';

export interface ISession {
  id: number;
  day?: string | null;
  startTime?: string | null;
  endTime?: string | null;
  semester?: number | null;
  room?: IRoom | null;
  subject?: ISubject | null;
  teacher?: ITeacher | null;
  schoolClass?: ISchoolClass | null;
  timetable?: ITimetable | null;
}

export type NewSession = Omit<ISession, 'id'> & { id: null };
