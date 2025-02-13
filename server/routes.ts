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
      const parsed = insertUserSchema.parse(req.body);
      const hashedPassword = await bcrypt.hash(parsed.password, 10);

      const user = await storage.createUser({
        ...parsed,
        password: hashedPassword
      });

      // Don't send password back to client
      const { password, ...userWithoutPassword } = user;
      res.json({ user: userWithoutPassword });
    } catch (error: any) {
      res.status(400).json({ message: error.message });
    }
  });

  app.post("/api/auth/login", async (req, res) => {
    try {
      const { email, password } = req.body;
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