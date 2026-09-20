"use client";

import Link from "next/link";
import { useActionState } from "react";
import { criarCliente } from "@/app/actions/clientes";

export default function NovoClientePage() {
  const [state, formAction, pending] = useActionState(criarCliente, undefined);

  return (
    <>
      <div className="topbar">
        <h1>Novo cliente</h1>
        <div style={{ display: "flex", gap: 10 }}>
          <Link href="/clientes" className="btn btn-outline">
            Cancelar
          </Link>
          <button className="btn btn-primary" type="submit" form="form-novo-cliente" disabled={pending}>
            {pending ? "Salvando..." : "Salvar cliente"}
          </button>
        </div>
      </div>
      <div className="content">
        <div className="builder-grid">
          <div className="builder-main">
            <div className="card">
              <h3>Dados do cliente</h3>

              {state?.erro && <div className="form-error">{state.erro}</div>}

              <form id="form-novo-cliente" action={formAction}>
                <div className="field">
                  <label htmlFor="nome">Nome completo</label>
                  <input id="nome" name="nome" type="text" required placeholder="Como aparece no orçamento" />
                </div>
                <div className="field-row">
                  <div className="field">
                    <label htmlFor="whatsapp">WhatsApp</label>
                    <input id="whatsapp" name="whatsapp" type="text" required placeholder="(47) 90000-0000" />
                  </div>
                  <div className="field">
                    <label htmlFor="cpf">CPF (opcional aqui)</label>
                    <input id="cpf" name="cpf" type="text" placeholder="Pode preencher depois" />
                  </div>
                </div>
                <div className="field">
                  <label htmlFor="email">E-mail (opcional)</label>
                  <input id="email" name="email" type="email" placeholder="cliente@email.com" />
                </div>
                <div className="field" style={{ marginBottom: 0 }}>
                  <label htmlFor="observacoesInternas">Observações internas (opcional)</label>
                  <input
                    id="observacoesInternas"
                    name="observacoesInternas"
                    type="text"
                    placeholder="Só você vê isso — ex: prefere treino de manhã"
                  />
                </div>
              </form>
            </div>
          </div>

          <div className="builder-preview">
            <div style={{ fontSize: 11.5, color: "var(--text-muted)", fontWeight: 600, marginBottom: 8 }}>
              COMO VAI APARECER
            </div>
            <div className="card" style={{ padding: 16 }}>
              <div className="client-row-name" style={{ marginBottom: 10 }}>
                <div className="avatar-sm" style={{ width: 34, height: 34, fontSize: 13 }}>
                  ?
                </div>
                <div>
                  <div style={{ fontWeight: 700, fontSize: 13.5 }}>Nome do cliente</div>
                  <div style={{ fontSize: 11.5, color: "var(--text-muted)" }}>
                    Aparece na sua lista e no orçamento
                  </div>
                </div>
              </div>
              <div
                style={{
                  fontSize: 12,
                  color: "var(--text-muted)",
                  lineHeight: 1.8,
                  borderTop: "1px solid var(--border)",
                  paddingTop: 10,
                }}
              >
                O CPF não é obrigatório aqui porque o próprio cliente confirma esse dado no
                momento de aceitar o orçamento — isso evita erro de digitação e mantém o
                comprovante mais confiável.
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
