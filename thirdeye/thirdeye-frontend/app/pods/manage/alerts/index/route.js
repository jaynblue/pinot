import Ember from 'ember';
import fetch from 'fetch';
import _ from 'lodash';

export default Ember.Route.extend({
  model() {
    return Ember.RSVP.hash({
      alerts: fetch('/thirdeye/entity/ANOMALY_FUNCTION').then(res => res.json()),
      subscriberGroups: fetch('/thirdeye/entity/ALERT_CONFIG').then(res => res.json()),
      applications: fetch('/thirdeye/entity/APPLICATION').then(res => res.json())
    })
  },

  setupController(controller, model, transition) {
    const { queryParams } = transition;
    const isSearchModeAll = !queryParams.selectedSearchMode
      || (queryParams.selectedSearchMode === 'All Alerts');
    const filterBlocks = [
      {
        name: 'Quick Filters',
        filterKeys: ['Alerts I subscribe to', 'Alerts I own', 'All alerts']
      },
      {
        name: 'Status',
        filterKeys: ['Active', 'Inactive']
      },
      {
        name: 'Subscription Groups',
        filterKeys: model.subscriberGroups.map(group => group.name)
      },
      {
        name: 'Applications',
        filterKeys: model.applications.map(app => app.application)
      },
      {
        name: 'Owner',
        filterKeys: model.alerts.map(alert => alert.createdBy)
      }
    ];

    // Send filters to controller
    controller.setProperties({
      model,
      filterBlocks
    });

    // Pre-select all alerts if mode is right
    if (isSearchModeAll) {
      controller.setProperties({
        selectedAlerts: model.alerts,
        resultsActive: true
      });
    }
  }
});
