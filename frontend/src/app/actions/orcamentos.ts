"use server";

import { redirect } from "next/navigation";
import { apiPostAuth } from "@/lib/api";
import type { NovoOrcamentoPayload, Orcamento } from "@/lib/types";

export async function criarOrcamento(
  payload: NovoOrcamentoPayload
): Promise<{ erro: string } | undefined> {
  let id: number;

  try {
    const criado = await apiPostAuth<Orcamento>("/orcamentos", payload);
    id = criado.id;
  } catch (error) {
    return { erro: error instanceof Error ? error.message : "Erro ao criar o orçamento." };
  }

  redirect(`/orcamentos/${id}`);
}
