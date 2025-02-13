import { User, InsertUser, Ride, InsertRide } from "@shared/schema";

export interface IStorage {
  getUser(id: number): Promise<User | undefined>;
  getUserByEmail(email: string): Promise<User | undefined>;
  createUser(user: InsertUser): Promise<User>;
  getRides(): Promise<Ride[]>;
  getRide(id: number): Promise<Ride | undefined>;
  createRide(userId: number, ride: InsertRide): Promise<Ride>;
}

export class MemStorage implements IStorage {
  private users: Map<number, User>;
  private rides: Map<number, Ride>;
  private currentUserId: number;
  private currentRideId: number;

  constructor() {
    this.users = new Map();
    this.rides = new Map();
    this.currentUserId = 1;
    this.currentRideId = 1;
  }

  async getUser(id: number): Promise<User | undefined> {
    return this.users.get(id);
  }

  async getUserByEmail(email: string): Promise<User | undefined> {
    return Array.from(this.users.values()).find(
      (user) => user.email === email
    );
  }

  async createUser(insertUser: InsertUser): Promise<User> {
    const id = this.currentUserId++;
    const user: User = { ...insertUser, id };
    this.users.set(id, user);
    return user;
  }

  async getRides(): Promise<Ride[]> {
    return Array.from(this.rides.values());
  }

  async getRide(id: number): Promise<Ride | undefined> {
    return this.rides.get(id);
  }

  async createRide(userId: number, insertRide: InsertRide): Promise<Ride> {
    const id = this.currentRideId++;
    const ride: Ride = { ...insertRide, id, userId };
    this.rides.set(id, ride);
    return ride;
  }
}

export const storage = new MemStorage();
