import mysql from 'mysql2/promise';

const pool = mysql.createPool({
  host: 'sql12.freesqldatabase.com',
  user: 'sql12762099',
  password: 'fVlHYMv9cA',
  database: 'sql12762099',
  port: 3306,
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0
});

export default pool;
