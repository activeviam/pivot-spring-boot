var baseUrl = window.location.href.split("/admin/ui")[0];

window.env = {
  contentServerUrl: baseUrl,
  contentServerVersion: "5.11.x",
  activePivotServers: {
    sandboxActivePivot: {
      url: baseUrl,
      version: "5.11.8",
    },
  },
};
