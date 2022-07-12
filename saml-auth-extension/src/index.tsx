import React, { ComponentType, useMemo, useState } from "react";
import {
  createActivePivotClient,
  createContentClient,
  ClientsProvider,
  UserProvider,
  getUserRoles
} from "@activeviam/activeui-sdk";

export interface Credentials {
  username: string;
  token: string;
}

const extension = {
  activate: async (configuration: any) => {
    if (window.customEnv.security === "saml") {
      configuration.higherOrderComponents = [withSamlAuthentication];
    }
  }
};

/**
 * Higher order component providing the user information to underlying components.
 * Also provides the clients allowing them to communicate with ActivePivot and the content server.
 */
function withSamlAuthentication<P>(
  Component: ComponentType<P>
): ComponentType<P> {
  const Wrapped = (props: P) => {
    const [credentials, setCredentials] = useState(() => {
      const username = localStorage.getItem("activeui-username");
      const token = localStorage.getItem("activeui-token");

      if (token && isTokenExpired(parseToken(token))) {
        localStorage.removeItem("activeui-username");
        localStorage.removeItem("activeui-token");
        return undefined;
      }

      return username && token
        ? {
          username,
          token
        }
        : undefined
    });

    if (!credentials) {
      authenticate().then(setCredentials);
    }

    const userRoles = credentials ? getUserRoles(credentials.token) : [];

    const clients = useMemo(() => {
      if (!credentials) {
        return undefined;
      }
      const requestInit = {
        headers: { authorization: `Jwt ${credentials.token}` },
      }
      return {
        activePivot: {
          [Object.keys(window.env.activePivotServers)[0]]: createActivePivotClient({
            url: Object.values(window.env.activePivotServers)[0].url,
            version: {
              id: "6",
              restPath: "/pivot/rest/v6",
              wsPath: "/pivot/ws/v6",
            },
            requestInit
          }),
        },
        contentClient: createContentClient({
          url: window.env.contentServerUrl,
          version: {
            id: "5",
            restPath: "/content/rest/v5",
            wsPath: "/content/ws/v5",
          },
          requestInit
        }),
      };
    }, [credentials]);

    if (credentials) {
      return (
        <UserProvider value={{ username: credentials.username, userRoles: userRoles }}>
          <ClientsProvider value={clients}>
            <Component {...props} />
          </ClientsProvider>
        </UserProvider>
      );
    } else {
      return <div></div>
    }
  };

  Wrapped.displayName = `withCustomAuthentication(${Component.displayName})`;

  return Wrapped;
}

function parseToken(token: string): any {
  const base64EncodedPayload = token.split('.')[1];
  const serializedPayload = atob(base64EncodedPayload);
  return JSON.parse(serializedPayload);
}

function isTokenExpired(token: any): boolean {
  const now = new Date();
  return now.getTime() > token.exp * 1000;
}

async function authenticate(): Promise<Credentials> {
  const tokenRequest = await fetch(`${Object.values(window.env.activePivotServers)[0].url}/jwt/rest/v1/token`, {
    credentials: "include"
  });

  if (tokenRequest.status === 401) {
    let dashboardUrl = encodeURI(window.location.href);
    // If # is not escaped, url path starting with # is ignored in the url query parameter
    dashboardUrl = dashboardUrl.replace('#', '%23');
    window.location.replace(`${Object.values(window.env.activePivotServers)[0].url}/saml/login?redirectTo=${dashboardUrl}`);
  }

  const body = await tokenRequest.json();
  const token = body.data.token;
  const username = parseToken(token).sub;

  window.localStorage.setItem("activeui-username", username);
  window.localStorage.setItem("activeui-token", token);

  return { username, token };
}

export default extension;