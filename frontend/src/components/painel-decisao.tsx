"use client";

import { useRouter } from "next/navigation";
import { useState, useTransition } from "react";
import { responderOrcamento } from "@/app/actions/publico";
import { cpfValido, formatarCpf } from "@/lib/cpf";
import { formatarMoeda } from "@/lib/formato";
import type { TipoDecisao } from "@/lib/types";

type Etapa = "escolha" | "aceitar" | "recusar" | "ajuste";

export function PainelDecisao({
  slug,
  nomeNegocio,
  total,
}: {
  slug: string;
  nomeNegocio: string;
  total: number;
}) {
  const router = useRouter();
  const [nome, setNome] = useState("");
  const [cpf, setCpf] = useState("");
  const [etapa, setEtapa] = useState<Etapa>("escolha");
  const [comentario, setComentario] = useState("");
  const [erro, setErro] = useState<string | null>(null);
  const [enviando, iniciarEnvio] = useTransition();

  const nomeValido = nome.trim().split(/\s+/).filter((parte) => parte.length >= 2).length >= 2;
  const identificado = nomeValido && cpfValido(cpf);

  function voltar() {
    setEtapa("escolha");
    setComentario("");
    setErro(null);
  }

  function enviar(tipo: TipoDecisao) {
    setErro(null);
    if (!identificado) {
      setErro("Informe seu nome completo e um CPF válido.");
      return;
    }
    if (tipo === "AJUSTE" && comentario.trim() === "") {
      setErro("Descreva o que precisa ser ajustado.");
      return;
    }

    iniciarEnvio(async () => {
      const resposta = await responderOrcamento(slug, { tipo, nome: nome.trim(), cpf, comentario });
      if (resposta.ok) {
        router.refresh();
      } else {
        setErro(resposta.erro);
      }
    });
  }

  return (
    <div className="accept-panel">
      <h3>Confirmar recebimento e decisão</h3>

      <div className="field-row">
        <div className="field">
          <label htmlFor="nome">Seu nome completo</label>
          <input
            id="nome"
            type="text"
            autoComplete="name"
            placeholder="Como no seu documento"
            value={nome}
            onChange={(e) => setNome(e.target.value)}
            disabled={enviando}
          />
        </div>
        <div className="field">
          <label htmlFor="cpf">CPF</label>
          <input
            id="cpf"
            type="text"
            inputMode="numeric"
            placeholder="000.000.000-00"
            value={cpf}
            onChange={(e) => setCpf(formatarCpf(e.target.value))}
            disabled={enviando}
          />
        </div>
      </div>

      {!identificado && (
        <div className="hint-linha" style={{ marginTop: 0 }}>
          Informe nome completo e um CPF válido para liberar as opções.
        </div>
      )}

      {erro && <div className="form-error" style={{ marginTop: 14, marginBottom: 0 }}>{erro}</div>}

      {etapa === "escolha" && (
        <div className="action-row">
          <button type="button" className="btn btn-green" disabled={!identificado} onClick={() => setEtapa("aceitar")}>
            Aceitar orçamento
          </button>
          <button
            type="button"
            className="btn btn-danger-outline"
            disabled={!identificado}
            onClick={() => setEtapa("recusar")}
          >
            Recusar
          </button>
          <button type="button" className="btn btn-outline" disabled={!identificado} onClick={() => setEtapa("ajuste")}>
            Pedir ajuste
          </button>
        </div>
      )}

      {etapa === "aceitar" && (
        <div className="etapa-confirmacao">
          <p>
            Você está aceitando o orçamento de <strong>{nomeNegocio}</strong> no valor de{" "}
            <strong>{formatarMoeda(total)}</strong>. Essa resposta não pode ser desfeita.
          </p>
          <div className="action-row">
            <button type="button" className="btn btn-green" disabled={enviando} onClick={() => enviar("ACEITO")}>
              {enviando ? "Registrando..." : "Confirmar aceite"}
            </button>
            <button type="button" className="btn btn-outline" disabled={enviando} onClick={voltar}>
              Voltar
            </button>
          </div>
        </div>
      )}

      {etapa === "recusar" && (
        <div className="etapa-confirmacao">
          <div className="field">
            <label htmlFor="motivo">Quer contar o motivo? (opcional)</label>
            <textarea
              id="motivo"
              value={comentario}
              onChange={(e) => setComentario(e.target.value)}
              disabled={enviando}
              maxLength={1000}
            />
          </div>
          <div className="action-row">
            <button
              type="button"
              className="btn btn-danger-outline"
              disabled={enviando}
              onClick={() => enviar("RECUSADO")}
            >
              {enviando ? "Registrando..." : "Confirmar recusa"}
            </button>
            <button type="button" className="btn btn-outline" disabled={enviando} onClick={voltar}>
              Voltar
            </button>
          </div>
        </div>
      )}

      {etapa === "ajuste" && (
        <div className="etapa-confirmacao">
          <div className="field">
            <label htmlFor="ajuste">O que precisa ser ajustado?</label>
            <textarea
              id="ajuste"
              placeholder="Ex: pode incluir mais uma sessão ou rever o valor?"
              value={comentario}
              onChange={(e) => setComentario(e.target.value)}
              disabled={enviando}
              maxLength={1000}
            />
          </div>
          <div className="action-row">
            <button type="button" className="btn btn-primary" disabled={enviando} onClick={() => enviar("AJUSTE")}>
              {enviando ? "Enviando..." : "Enviar pedido de ajuste"}
            </button>
            <button type="button" className="btn btn-outline" disabled={enviando} onClick={voltar}>
              Voltar
            </button>
          </div>
        </div>
      )}

      <div className="legal-note">
        Ao responder, registramos seu nome, CPF, endereço IP e horário como comprovante da sua decisão. Esses dados
        são usados só para esse fim e ficam disponíveis para você e para {nomeNegocio}.
      </div>
    </div>
  );
}
