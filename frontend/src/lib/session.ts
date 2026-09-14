import { cookies } from "next/headers";

export const COOKIE_NAME = "aceitaai_token";
const MAX_AGE_SECONDS = 60 * 60 * 24; // 24h, mesmo prazo de expiracao do token no backend

export async function salvarSessao(token: string) {
  const cookieStore = await cookies();
  cookieStore.set(COOKIE_NAME, token, {
    httpOnly: true,
    secure: process.env.NODE_ENV === "production",
    sameSite: "lax",
    path: "/",
    maxAge: MAX_AGE_SECONDS,
  });
}

export async function obterToken() {
  const cookieStore = await cookies();
  return cookieStore.get(COOKIE_NAME)?.value;
}

export async function encerrarSessao() {
  const cookieStore = await cookies();
  cookieStore.delete(COOKIE_NAME);
}

export async function obterSessao(): Promise<{ autonomoId: number; email: string } | null> {
  const token = await obterToken();
  if (!token) return null;

  try {
    const payloadBase64 = token.split(".")[1];
    const payload = JSON.parse(Buffer.from(payloadBase64, "base64url").toString("utf-8"));
    return { autonomoId: payload.autonomoId, email: payload.sub };
  } catch {
    return null;
  }
}
