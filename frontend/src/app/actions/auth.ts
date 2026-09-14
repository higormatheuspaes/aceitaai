"use server";

import { redirect } from "next/navigation";
import { apiPost, type AuthResponse } from "@/lib/api";
import { encerrarSessao, salvarSessao } from "@/lib/session";

export type AuthFormState = { erro?: string } | undefined;

export async function cadastrar(
  _state: AuthFormState,
  formData: FormData
): Promise<AuthFormState> {
  const payload = {
    nomeDono: formData.get("nomeDono"),
    nomeNegocio: formData.get("nomeNegocio"),
    documento: formData.get("documento"),
    whatsapp: formData.get("whatsapp"),
    cep: formData.get("cep"),
    email: formData.get("email"),
    senha: formData.get("senha"),
  };

  let token: string;
  try {
    const data = await apiPost<AuthResponse>("/auth/cadastro", payload);
    token = data.token;
  } catch (error) {
    return { erro: error instanceof Error ? error.message : "Erro ao criar conta." };
  }

  await salvarSessao(token);
  redirect("/dashboard");
}

export async function login(
  _state: AuthFormState,
  formData: FormData
): Promise<AuthFormState> {
  const payload = {
    email: formData.get("email"),
    senha: formData.get("senha"),
  };

  let token: string;
  try {
    const data = await apiPost<AuthResponse>("/auth/login", payload);
    token = data.token;
  } catch (error) {
    return { erro: error instanceof Error ? error.message : "E-mail ou senha invalidos." };
  }

  await salvarSessao(token);
  redirect("/dashboard");
}

export async function sair() {
  await encerrarSessao();
  redirect("/login");
}
