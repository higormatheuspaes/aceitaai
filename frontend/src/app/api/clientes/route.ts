import { ApiError, apiGetAuth } from "@/lib/api";
import type { ClienteOpcao, PageResponse } from "@/lib/types";

export async function GET(request: Request) {
  const busca = new URL(request.url).searchParams.get("busca")?.trim().slice(0, 80) ?? "";

  const params = new URLSearchParams({ pagina: "0", tamanho: "8" });
  if (busca) params.set("busca", busca);

  try {
    const resultado = await apiGetAuth<PageResponse<ClienteOpcao>>(`/clientes?${params.toString()}`);
    const opcoes = resultado.conteudo.map(({ id, nome, whatsapp }) => ({ id, nome, whatsapp }));
    return Response.json(opcoes);
  } catch (error) {
    const status = error instanceof ApiError && error.status === 401 ? 401 : 502;
    return Response.json({ erro: "Nao foi possivel buscar clientes" }, { status });
  }
}
