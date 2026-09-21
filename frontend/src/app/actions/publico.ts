"use server";

import { headers } from "next/headers";
import { apiPostAuth } from "@/lib/api";
import type { DecisaoPublica, TipoDecisao } from "@/lib/types";

export type RespostaDecisao = { ok: true; decisao: DecisaoPublica } | { ok: false; erro: string };

function somenteAscii(valor: string | null | undefined, maximo: number) {
  return (valor ?? "").replace(/[^\x20-\x7E]/g, "").slice(0, maximo);
}

export async function responderOrcamento(
  slug: string,
  dados: { tipo: TipoDecisao; nome: string; cpf: string; comentario: string }
): Promise<RespostaDecisao> {
  const cabecalhos = await headers();
  const ip = somenteAscii(
    cabecalhos.get("x-forwarded-for")?.split(",")[0]?.trim() || cabecalhos.get("x-real-ip"),
    45
  );
  const userAgent = somenteAscii(cabecalhos.get("user-agent"), 255);

  try {
    const decisao = await apiPostAuth<DecisaoPublica>(
      `/publico/orcamentos/${encodeURIComponent(slug)}/decisao`,
      dados,
      {
        "X-Internal-Key": process.env.INTERNAL_API_KEY ?? "",
        "X-Client-Ip": ip,
        "X-Client-User-Agent": userAgent,
      }
    );
    return { ok: true, decisao };
  } catch (error) {
    return {
      ok: false,
      erro: error instanceof Error ? error.message : "Não foi possível registrar sua resposta agora.",
    };
  }
}
