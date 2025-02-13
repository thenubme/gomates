import type { Express } from "express";
import { createServer, type Server } from "http";
import path from "path";
import { storage } from "./storage";
import { insertUserSchema } from "@shared/schema";
import bcrypt from "bcryptjs";

export function registerRoutes(app: Express): Server {
  // Authentication routes
  app.post("/api/auth/register", async (req, res) => {
    try {
      const { email, password, name } = req.body;

      // Validate input
      const validatedData = insertUserSchema.parse({
        email,
        password,
        name: name || email.split('@')[0] // Use part of email as name if not provided
      });

      // Check if user already exists
      const existingUser = await storage.getUserByEmail(validatedData.email);
      if (existingUser) {
        return res.status(400).json({ message: "Email already registered" });
      }

      const hashedPassword = await bcrypt.hash(validatedData.password, 10);

      const user = await storage.createUser({
        ...validatedData,
        password: hashedPassword
      });

      // Don't send password back to client
      const { password: _, ...userWithoutPassword } = user;
      res.json({ user: userWithoutPassword });
    } catch (error: any) {
      console.error('Registration error:', error);
      res.status(400).json({ 
        message: error.message || "Error during registration",
        errors: error.errors // Include validation errors if any
      });
    }
  });

  app.post("/api/auth/login", async (req, res) => {
    try {
      const { email, password } = req.body;

      // Validate input
      if (!email || !password) {
        return res.status(400).json({ message: "Email and password are required" });
      }

      const user = await storage.getUserByEmail(email);
      if (!user) {
        return res.status(401).json({ message: "Invalid email or password" });
      }

      const isValid = await bcrypt.compare(password, user.password);
      if (!isValid) {
        return res.status(401).json({ message: "Invalid email or password" });
      }

      // Don't send password back to client
      const { password: _, ...userWithoutPassword } = user;
      res.json({ user: userWithoutPassword });
    } catch (error: any) {
      console.error('Login error:', error);
      res.status(400).json({ message: error.message });
    }
  });

  // Rides routes
  app.get("/api/rides", async (_req, res) => {
    try {
      const rides = await storage.getRides();
      res.json(rides);
    } catch (error: any) {
      res.status(500).json({ message: error.message });
    }
  });

  app.post("/api/rides", async (req, res) => {
    try {
      const ride = await storage.createRide(req.body);
      res.json(ride);
    } catch (error: any) {
      res.status(400).json({ message: error.message });
    }
  });

  // Download checkpoint zip file
  app.get("/download/mysql-checkpoint", (req, res) => {
    const filePath = path.join(process.cwd(), "mysql_integration_checkpoint.zip");
    res.download(filePath, "mysql_integration_checkpoint.zip");
  });

  const httpServer = createServer(app);
  return httpServer;
}