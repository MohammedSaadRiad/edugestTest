import { IRoom } from 'app/entities/room/room.model';
import { ISubject } from 'app/entities/subject/subject.model';
import { ITeacher } from 'app/entities/teacher/teacher.model';
import { ISchoolClass } from 'app/entities/school-class/school-class.model';
import { ITimetable } from 'app/entities/timetable/timetable.model';

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
