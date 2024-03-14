var baseUrl = window.location.href.split("/ui")[0];

window.env = {
  jwtServer: {
    "url": baseUrl,
    "version": "6.0.12"
  },
  contentServerUrl: baseUrl,
  contentServerVersion:  "6.0.12",
  // WARNING: Changing the keys of activePivotServers will break previously saved widgets and dashboards.
  // If you must do it, then you also need to update each one's serverKey attribute on your content server.
  activePivotServers: {
    "trainingActivePivot": {
      url: baseUrl,
      version:  "6.0.12"
    }
  }
};