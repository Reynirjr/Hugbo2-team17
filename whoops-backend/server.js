const express = require('express');
const { Pool } = require('pg');
require('dotenv').config();

const app = express();
app.use(express.json());

// PostgreSQL connection pool
const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
  ssl: { rejectUnauthorized: false } // Required for Render
});

// Health check endpoint
app.get('/health', (req, res) => {
  res.json({ status: 'ok' });
});

// Get all menus with sections and items
app.get('/api/menus', async (req, res) => {
  try {
    const result = await pool.query(`
      SELECT json_agg(json_build_object(
        'id', m.id,
        'name', m.name,
        'sections', (
          SELECT json_agg(json_build_object(
            'id', s.id,
            'name', s.name,
            'items', (
              SELECT json_agg(json_build_object(
                'id', i.id,
                'name', i.name,
                'description', i.description,
                'priceIsk', i.price_isk,
                'available', i.available,
                'tags', i.tags
              )) FROM items i WHERE i.section_id = s.id
            )
          )) FROM sections s WHERE s.menu_id = m.id
        )
      )) as menus FROM menus m
    `);
    res.json(result.rows[0].menus || []);
  } catch (error) {
    console.error('Database error:', error);
    res.status(500).json({ error: 'Failed to fetch menus' });
  }
});

// Start server
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});