const baseUrl = window.location.href.split("/admin/ui")[0];

window.env = {
  "jwtServer": {
    "url": baseUrl,
    "version": "6.0.11-sb3"
  },
  "contentServer": {
    "url": baseUrl,
    "version": "6.0.11-sb3"
  },
  "atotiServers": {
    // WARNING: Changing the keys of servers will break previously saved widgets and dashboards.
    // If you must do it, then you also need to update each one's serverKey attribute on your content server.
    "trainingActivePivot": {
      "url": baseUrl,
      "version":  "6.0.11-sb3"
    }
  }
}