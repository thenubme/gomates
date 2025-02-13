import express from 'express';
import bcrypt from 'bcryptjs';
import pool from './db.js';

const router = express.Router();

// User registration
router.post('/api/auth/register', async (req, res) => {
  try {
    const { email, password } = req.body;
    const hashedPassword = await bcrypt.hash(password, 10);

    const [result] = await pool.execute(
      'INSERT INTO users (email, password) VALUES (?, ?)',
      [email, hashedPassword]
    );

    res.json({ user: { id: result.insertId, email } });
  } catch (error) {
    console.error('Registration error:', error);
    res.status(500).json({ message: 'Error during registration' });
  }
});

// User login
router.post('/api/auth/login', async (req, res) => {
  try {
    const { email, password } = req.body;
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

// Get all rides
router.get('/api/rides', async (req, res) => {
  try {
    const [rides] = await pool.execute('SELECT * FROM rides ORDER BY departure_time DESC');
    res.json(rides);
  } catch (error) {
    console.error('Error fetching rides:', error);
    res.status(500).json({ message: 'Error fetching rides' });
  }
});

// Post a new ride
router.post('/api/rides', async (req, res) => {
  try {
    const { origin, destination, fare, userId, departureTime } = req.body;
    const [result] = await pool.execute(
      'INSERT INTO rides (origin, destination, fare, user_id, departure_time) VALUES (?, ?, ?, ?, ?)',
      [origin, destination, fare, userId, departureTime]
    );
    
    const [ride] = await pool.execute('SELECT * FROM rides WHERE id = ?', [result.insertId]);
    res.json(ride[0]);
  } catch (error) {
    console.error('Error posting ride:', error);
    res.status(500).json({ message: 'Error posting ride' });
  }
});

export default router;
