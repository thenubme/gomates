import { pgTable, text, serial, integer, timestamp, doublePrecision } from "drizzle-orm/pg-core";
import { createInsertSchema } from "drizzle-zod";
import { z } from "zod";

export const users = pgTable("users", {
  id: serial("id").primaryKey(),
  email: text("email").notNull().unique(),
  password: text("password").notNull(),
  name: text("name").notNull()
});

export const rides = pgTable("rides", {
  id: serial("id").primaryKey(),
  userId: integer("user_id").notNull(),
  origin: text("origin").notNull(),
  destination: text("destination").notNull(),
  originLat: doublePrecision("origin_lat").notNull(),
  originLng: doublePrecision("origin_lng").notNull(),
  destinationLat: doublePrecision("destination_lat").notNull(),
  destinationLng: doublePrecision("destination_lng").notNull(),
  fare: integer("fare").notNull(),
  seats: integer("seats").notNull(),
  departureTime: timestamp("departure_time").notNull()
});

export const insertUserSchema = createInsertSchema(users).pick({
  email: true,
  password: true,
  name: true
});

export const insertRideSchema = createInsertSchema(rides).pick({
  origin: true,
  destination: true,
  originLat: true,
  originLng: true,
  destinationLat: true,
  destinationLng: true,
  fare: true,
  seats: true,
  departureTime: true
});

export type InsertUser = z.infer<typeof insertUserSchema>;
export type User = typeof users.$inferSelect;
export type InsertRide = z.infer<typeof insertRideSchema>;
export type Ride = typeof rides.$inferSelect;
