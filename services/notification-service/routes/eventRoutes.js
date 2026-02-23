const express = require('express');
const router = express.Router();
const { getProjectEvents } = require('../controllers/eventController');

router.get('/project/:projectId', getProjectEvents);

module.exports = router;
