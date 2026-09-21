import { obterToken } from "@/lib/session";

const API_URL = process.env.API_URL ?? "http://localhost:8080";

export type AuthResponse = {
  token: string;
  autonomoId: number;
  nomeNegocio: string;
};

export class ApiError extends Error {
  status: number;

  constructor(mensagem: string, status: number) {
    super(mensagem);
    this.status = status;
  }
}

function montarMensagem(data: { erro?: string; message?: string; campos?: Record<string, string> } | null) {
  const base = data?.erro ?? data?.message ?? "Erro ao comunicar com o servidor.";
  if (data?.campos && Object.keys(data.campos).length > 0) {
    const detalhes = Object.entries(data.campos)
      .map(([campo, motivo]) => `${campo}: ${motivo}`)
      .join("; ");
    return `${base} (${detalhes})`;
  }
  return base;
}

async function request<T>(path: string, options: RequestInit = {}, autenticado = false): Promise<T> {
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...(options.headers as Record<string, string> | undefined),
  };

  if (autenticado) {
    const token = await obterToken();
    if (token) {
      headers.Authorization = `Bearer ${token}`;
    }
  }

  const response = await fetch(`${API_URL}${path}`, { ...options, headers, cache: "no-store" });
  const data = await response.json().catch(() => null);

  if (!response.ok) {
    throw new ApiError(montarMensagem(data), response.status);
  }

  return data as T;
}

export function apiPost<T>(path: string, body: unknown, headers?: Record<string, string>): Promise<T> {
  return request<T>(path, { method: "POST", body: JSON.stringify(body), headers });
}

export function apiGet<T>(path: string): Promise<T> {
  return request<T>(path, { method: "GET" });
}

export function apiGetAuth<T>(path: string): Promise<T> {
  return request<T>(path, { method: "GET" }, true);
}

export function apiPostAuth<T>(path: string, body: unknown, headers?: Record<string, string>): Promise<T> {
  return request<T>(path, { method: "POST", body: JSON.stringify(body), headers }, true);
}

export function apiPutAuth<T>(path: string, body: unknown): Promise<T> {
  return request<T>(path, { method: "PUT", body: JSON.stringify(body) }, true);
}
