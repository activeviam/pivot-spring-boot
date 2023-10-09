var baseUrl = window.location.href.split("/ui")[0];

window.env = {
  "jwtServer": {
    "url": baseUrl,
    "version": "6.0.7"
  },
  "contentServer": {
    "url": baseUrl,
    "version": "6.0.7"
  },
  "atotiServers": {
    "my-server": {
      "url": baseUrl,
      "version": "6.0.7"
    }
  }
};