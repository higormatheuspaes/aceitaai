const API_URL = process.env.API_URL ?? "http://localhost:8080";

export type AuthResponse = {
  token: string;
  autonomoId: number;
  nomeNegocio: string;
};

export async function apiPost<T>(path: string, body: unknown): Promise<T> {
  const response = await fetch(`${API_URL}${path}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
    cache: "no-store",
  });

  const data = await response.json().catch(() => null);

  if (!response.ok) {
    const mensagem =
      data?.erro ?? data?.message ?? "Erro ao comunicar com o servidor.";
    throw new Error(mensagem);
  }

  return data as T;
}
