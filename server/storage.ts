import { users, rides, type User, type InsertUser, type Ride, type InsertRide } from "@shared/schema";

export interface IStorage {
  getUser(id: number): Promise<User | undefined>;
  getUserByEmail(email: string): Promise<User | undefined>;
  createUser(user: InsertUser): Promise<User>;
  getRides(): Promise<Ride[]>;
  createRide(ride: InsertRide & { userId: number; departureTime: Date }): Promise<Ride>;
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
      (user) => user.email === email,
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

  async createRide(ride: InsertRide & { userId: number; departureTime: Date }): Promise<Ride> {
    const id = this.currentRideId++;
    const newRide: Ride = { ...ride, id };
    this.rides.set(id, newRide);
    return newRide;
  }
}

export const storage = new MemStorage();