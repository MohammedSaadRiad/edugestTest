export interface IRoom {
  id: number;
  name?: string | null;
  capacity?: number | null;
}

export type NewRoom = Omit<IRoom, 'id'> & { id: null };
