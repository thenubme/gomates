import express from 'express';
import bcrypt from 'bcryptjs';
import pool from './db.js';

const router = express.Router();

// User registration
router.post('/api/auth/register', async (req, res) => {
  try {
    const { email, password, name } = req.body;

    // Validate input
    if (!email || !password) {
      return res.status(400).json({ message: 'Email and password are required' });
    }

    // Check if user already exists
    const [existingUsers] = await pool.execute(
      'SELECT * FROM users WHERE email = ?',
      [email]
    );

    if (existingUsers.length > 0) {
      return res.status(400).json({ message: 'Email already registered' });
    }

    const hashedPassword = await bcrypt.hash(password, 10);
    const userName = name || email.split('@')[0]; // Use name if provided, otherwise use email prefix

    const [result] = await pool.execute(
      'INSERT INTO users (email, password, name) VALUES (?, ?, ?)',
      [email, hashedPassword, userName]
    );

    res.json({ user: { id: result.insertId, email, name: userName } });
  } catch (error) {
    console.error('Registration error:', error);
    res.status(500).json({ message: 'Error during registration' });
  }
});

// User login
router.post('/api/auth/login', async (req, res) => {
  try {
    const { email, password } = req.body;

    if (!email || !password) {
      return res.status(400).json({ message: 'Email and password are required' });
    }

    const [users] = await pool.execute(
      'SELECT * FROM users WHERE email = ?',
      [email]
    );

    if (users.length === 0) {
      return res.status(401).json({ message: 'Invalid email or password' });
    }

    const user = users[0];
    const validPassword = await bcrypt.compare(password, user.password);
    if (!validPassword) {
      return res.status(401).json({ message: 'Invalid email or password' });
    }

    const { password: _, ...userWithoutPassword } = user;
    res.json({ user: userWithoutPassword });
  } catch (error) {
    console.error('Login error:', error);
    res.status(500).json({ message: 'Error during login' });
  }
});

// Get all available rides
router.get('/api/rides', async (req, res) => {
  try {
    const [rides] = await pool.execute(`
      SELECT r.*, u.name as driver_name 
      FROM rides r
      JOIN users u ON r.user_id = u.id
      WHERE r.departure_time > NOW()
      ORDER BY r.departure_time ASC
    `);
    res.json(rides);
  } catch (error) {
    console.error('Error fetching rides:', error);
    res.status(500).json({ message: 'Error fetching rides' });
  }
});

// Get rides posted by a specific user
router.get('/api/rides/user/:userId', async (req, res) => {
  try {
    const { userId } = req.params;
    const [rides] = await pool.execute(`
      SELECT r.*, u.name as driver_name 
      FROM rides r
      JOIN users u ON r.user_id = u.id
      WHERE r.user_id = ?
      ORDER BY r.departure_time DESC
    `, [userId]);
    res.json(rides);
  } catch (error) {
    console.error('Error fetching user rides:', error);
    res.status(500).json({ message: 'Error fetching user rides' });
  }
});

// Post a new ride
router.post('/api/rides', async (req, res) => {
  try {
    const { origin, destination, departureTime, fare, availableSeats, userId } = req.body;

    // Validate input
    if (!origin || !destination || !departureTime || !fare || !userId) {
      return res.status(400).json({ message: 'All fields are required' });
    }

    const [result] = await pool.execute(
      'INSERT INTO rides (origin, destination, departure_time, fare, available_seats, user_id) VALUES (?, ?, ?, ?, ?, ?)',
      [origin, destination, departureTime, fare, availableSeats || 4, userId]
    );

    const [ride] = await pool.execute(
      `SELECT r.*, u.name as driver_name 
       FROM rides r
       JOIN users u ON r.user_id = u.id
       WHERE r.id = ?`,
      [result.insertId]
    );

    res.json(ride[0]);
  } catch (error) {
    console.error('Error posting ride:', error);
    res.status(500).json({ message: 'Error posting ride' });
  }
});

// Update a ride
router.put('/api/rides/:rideId', async (req, res) => {
  try {
    const { rideId } = req.params;
    const { origin, destination, departureTime, fare, availableSeats, userId } = req.body;

    // Validate input
    if (!origin || !destination || !departureTime || !fare || !userId) {
      return res.status(400).json({ message: 'All fields are required' });
    }

    // Check if the ride belongs to the user
    const [existingRide] = await pool.execute(
      'SELECT * FROM rides WHERE id = ? AND user_id = ?',
      [rideId, userId]
    );

    if (existingRide.length === 0) {
      return res.status(403).json({ message: 'Not authorized to update this ride' });
    }

    await pool.execute(
      'UPDATE rides SET origin = ?, destination = ?, departure_time = ?, fare = ?, available_seats = ? WHERE id = ?',
      [origin, destination, departureTime, fare, availableSeats, rideId]
    );

    const [updatedRide] = await pool.execute(
      `SELECT r.*, u.name as driver_name 
       FROM rides r
       JOIN users u ON r.user_id = u.id
       WHERE r.id = ?`,
      [rideId]
    );

    res.json(updatedRide[0]);
  } catch (error) {
    console.error('Error updating ride:', error);
    res.status(500).json({ message: 'Error updating ride' });
  }
});

// Delete a ride
router.delete('/api/rides/:rideId', async (req, res) => {
  try {
    const { rideId } = req.params;
    const { userId } = req.body;

    // Check if the ride belongs to the user
    const [existingRide] = await pool.execute(
      'SELECT * FROM rides WHERE id = ? AND user_id = ?',
      [rideId, userId]
    );

    if (existingRide.length === 0) {
      return res.status(403).json({ message: 'Not authorized to delete this ride' });
    }

    await pool.execute('DELETE FROM rides WHERE id = ?', [rideId]);
    res.json({ message: 'Ride deleted successfully' });
  } catch (error) {
    console.error('Error deleting ride:', error);
    res.status(500).json({ message: 'Error deleting ride' });
  }
});

export default router;