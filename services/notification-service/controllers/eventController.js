const Event = require('../models/Event');

// @desc    Get all events for a specific project
// @route   GET /api/events/project/:projectId
// @access  Private
exports.getProjectEvents = async (req, res) => {
    try {
        const { projectId } = req.params;

        // Find events where metadata.projectId matches or entityId matches (if it's a project event)
        // We look for events from 'project' service or events that have projectId in metadata
        const events = await Event.find({
            $or: [
                { 'metadata.projectId': parseInt(projectId) },
                { 'metadata.projet': parseInt(projectId) },
                { entityId: projectId, serviceSource: 'project' }
            ]
        }).sort({ timestamp: -1 });

        // Map to legacy format expected by frontend
        const mappedEvents = events.map(event => {
            const meta = event.metadata || {};
            return {
                id_event: event._id,
                action: event.action,
                created_at: event.timestamp,
                metadata: meta,
                user: event.userId,
                // These fields are needed by the frontend EventHistoryTable
                projet: meta.projectId || meta.projet || (event.serviceSource === 'project' ? parseInt(event.entityId) : null),
                article: meta.articleId || meta.article || null,
                bloc: meta.blocId || meta.bloc || null,
                ouvrage: meta.ouvrageId || meta.ouvrage || null,
                lot: meta.lotId || meta.lot || null,
                // Add userData placeholder if missing (frontend might expect it)
                userData: meta.userData || {
                    id: event.userId,
                    nom_utilisateur: meta.actorName || meta.userName || 'Utilisateur',
                    email: ''
                }
            };
        });

        res.status(200).json({
            success: true,
            data: mappedEvents
        });
    } catch (err) {
        console.error('[EventController] Error:', err);
        res.status(500).json({ success: false, error: err.message });
    }
};
