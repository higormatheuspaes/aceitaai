"use server";

import { redirect } from "next/navigation";
import { apiPostAuth } from "@/lib/api";

export type ClienteFormState = { erro?: string } | undefined;

export async function criarCliente(
  _state: ClienteFormState,
  formData: FormData
): Promise<ClienteFormState> {
  const payload = {
    nome: formData.get("nome"),
    whatsapp: formData.get("whatsapp"),
    cpf: formData.get("cpf") || null,
    email: formData.get("email") || null,
    observacoesInternas: formData.get("observacoesInternas") || null,
  };

  try {
    await apiPostAuth("/clientes", payload);
  } catch (error) {
    return { erro: error instanceof Error ? error.message : "Erro ao cadastrar cliente." };
  }

  redirect("/clientes");
}
