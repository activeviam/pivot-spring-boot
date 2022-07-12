import { Env } from "@activeviam/sandbox-clients/";

declare global {
    interface Window {
        env: Env;
        customEnv: CustomEnv
    }
}

export interface CustomEnv {
    security: string;
}
