"use client";

import Link from "next/link";
import { useActionState } from "react";
import { login } from "@/app/actions/auth";
import { AuthSide, MobileTopBar } from "@/components/auth-side";

export default function LoginPage() {
  const [state, formAction, pending] = useActionState(login, undefined);

  return (
    <div className="auth-wrap">
      <AuthSide
        title="Orçamento profissional, aceite registrado, sem complicação."
        description="Envie um link, o cliente confirma, você tem o comprovante. Feito para quem trabalha sozinho e não tem tempo pra burocracia."
      />
      <div className="auth-form-side">
        <MobileTopBar title="Entrar" />
        <form className="auth-form" action={formAction}>
          <h1>Entrar</h1>
          <div className="sub">Acesse sua conta pra gerenciar seus orçamentos.</div>

          {state?.erro && <div className="form-error">{state.erro}</div>}

          <div className="field">
            <label htmlFor="email">E-mail</label>
            <input id="email" name="email" type="email" required placeholder="voce@negocio.com.br" />
          </div>
          <div className="field">
            <label htmlFor="senha">Senha</label>
            <input id="senha" name="senha" type="password" required />
          </div>
          <button className="btn btn-primary btn-block" type="submit" disabled={pending}>
            {pending ? "Entrando..." : "Entrar"}
          </button>
          <div className="auth-alt-link">
            Não tem conta? <Link href="/cadastro">Criar conta</Link>
          </div>
        </form>
      </div>
    </div>
  );
}
