export interface ITimetable {
  id: number;
  semestre?: number | null;
}

export type NewTimetable = Omit<ITimetable, 'id'> & { id: null };
