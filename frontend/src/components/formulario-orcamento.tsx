"use client";

import Link from "next/link";
import { useState, useTransition } from "react";
import { criarOrcamento, revisarOrcamento } from "@/app/actions/orcamentos";
import { formatarMoeda, iniciais, paraNumero } from "@/lib/formato";
import type { ClienteOpcao, RevisaoOrcamentoPayload } from "@/lib/types";
import { SeletorCliente } from "@/components/seletor-cliente";

type ItemForm = {
  chave: number;
  descricao: string;
  quantidade: string;
  valor: string;
  desconto: string;
};

type ItemCalculado = {
  descricao: string;
  quantidade: number;
  valorUnitario: number;
  desconto: number;
  total: number | null;
  erro: string | null;
};

const novoItem = (chave: number): ItemForm => ({
  chave,
  descricao: "",
  quantidade: "1",
  valor: "",
  desconto: "",
});

function temMaisDeDuasCasas(valor: number) {
  return Math.abs(valor * 100 - Math.round(valor * 100)) > 1e-6;
}

function calcular(item: ItemForm, posicao: number): ItemCalculado {
  const prefixo = `Item ${posicao}:`;
  const quantidade = Number(item.quantidade);
  const valorUnitario = paraNumero(item.valor);
  const desconto = item.desconto.trim() === "" ? 0 : paraNumero(item.desconto);
  const base = { descricao: item.descricao.trim(), quantidade, valorUnitario, desconto };

  if (base.descricao === "") return { ...base, total: null, erro: `${prefixo} informe a descrição.` };
  if (!Number.isInteger(quantidade) || quantidade < 1) {
    return { ...base, total: null, erro: `${prefixo} a quantidade deve ser um número inteiro maior que zero.` };
  }
  if (Number.isNaN(valorUnitario)) return { ...base, total: null, erro: `${prefixo} informe o valor unitário.` };
  if (Number.isNaN(desconto)) return { ...base, total: null, erro: `${prefixo} o desconto informado não é válido.` };
  if (temMaisDeDuasCasas(valorUnitario) || temMaisDeDuasCasas(desconto)) {
    return { ...base, total: null, erro: `${prefixo} use no máximo 2 casas decimais.` };
  }

  const bruto = quantidade * valorUnitario;
  if (desconto > bruto + 1e-9) {
    return { ...base, total: null, erro: `${prefixo} o desconto não pode ser maior que o valor do item.` };
  }
  return { ...base, total: bruto - desconto, erro: null };
}

// Preenchido quando o autonomo edita um orcamento em que o cliente pediu ajuste (gera uma nova versao).
export type DadosRevisao = {
  orcamentoId: number;
  cliente: ClienteOpcao;
  itens: { descricao: string; quantidade: number; valorUnitario: number; desconto: number }[];
  validadeDias: number | null;
  formaPagamento: string | null;
  observacoes: string | null;
  pedidoDoCliente: string | null;
};

const VALIDADES_PADRAO = ["7", "15", "30"];

const emTexto = (valor: number) => (valor === 0 ? "" : String(valor).replace(".", ","));

export function FormularioOrcamento({ nomeNegocio, revisao }: { nomeNegocio: string; revisao?: DadosRevisao }) {
  const [cliente, setCliente] = useState<ClienteOpcao | null>(revisao?.cliente ?? null);
  const [itens, setItens] = useState<ItemForm[]>(
    revisao
      ? revisao.itens.map((item, indice) => ({
          chave: indice + 1,
          descricao: item.descricao,
          quantidade: String(item.quantidade),
          valor: emTexto(item.valorUnitario),
          desconto: emTexto(item.desconto),
        }))
      : [novoItem(1)]
  );
  const [proximaChave, setProximaChave] = useState(revisao ? revisao.itens.length + 1 : 2);
  const [validade, setValidade] = useState(
    revisao ? (revisao.validadeDias === null ? "sem" : String(revisao.validadeDias)) : "7"
  );
  const [formaPagamento, setFormaPagamento] = useState(revisao?.formaPagamento ?? "");
  const [observacoes, setObservacoes] = useState(revisao?.observacoes ?? "");
  const [erro, setErro] = useState<string | null>(null);
  const [enviando, iniciarEnvio] = useTransition();

  const calculados = itens.map((item, indice) => calcular(item, indice + 1));
  const totalGeral = calculados.reduce((soma, item) => soma + (item.total ?? 0), 0);

  function atualizar(chave: number, campo: keyof Omit<ItemForm, "chave">, valor: string) {
    setItens((atuais) => atuais.map((item) => (item.chave === chave ? { ...item, [campo]: valor } : item)));
  }

  function adicionarItem() {
    setItens((atuais) => [...atuais, novoItem(proximaChave)]);
    setProximaChave((n) => n + 1);
  }

  function removerItem(chave: number) {
    setItens((atuais) => (atuais.length > 1 ? atuais.filter((item) => item.chave !== chave) : atuais));
  }

  function enviar(evento: React.FormEvent) {
    evento.preventDefault();
    setErro(null);

    if (!cliente) {
      setErro("Escolha o cliente para quem é o orçamento.");
      return;
    }
    const invalido = calculados.find((item) => item.erro);
    if (invalido) {
      setErro(invalido.erro);
      return;
    }

    const conteudo: RevisaoOrcamentoPayload = {
      validadeDias: validade === "sem" ? null : Number(validade),
      formaPagamento: formaPagamento.trim(),
      observacoes: observacoes.trim(),
      itens: calculados.map((item) => ({
        descricao: item.descricao,
        quantidade: item.quantidade,
        valorUnitario: Math.round(item.valorUnitario * 100) / 100,
        desconto: Math.round(item.desconto * 100) / 100,
      })),
    };

    iniciarEnvio(async () => {
      const resultado = revisao
        ? await revisarOrcamento(revisao.orcamentoId, conteudo)
        : await criarOrcamento({ clienteId: cliente.id, ...conteudo });
      if (resultado?.erro) setErro(resultado.erro);
    });
  }

  const linhasPreview = calculados.filter((item) => item.descricao !== "");

  return (
    <form onSubmit={enviar} noValidate>
      <div className="topbar">
        <h1>{revisao ? `Editar orçamento #${revisao.orcamentoId}` : "Novo orçamento"}</h1>
        <div style={{ display: "flex", gap: 10 }}>
          <Link href={revisao ? `/orcamentos/${revisao.orcamentoId}` : "/dashboard"} className="btn btn-outline">
            Cancelar
          </Link>
          <button className="btn btn-primary" type="submit" disabled={enviando}>
            {revisao
              ? enviando
                ? "Enviando..."
                : "Enviar nova versão"
              : enviando
                ? "Gerando..."
                : "Gerar link"}
          </button>
        </div>
      </div>

      <div className="content">
        {erro && <div className="form-error">{erro}</div>}

        <div className="builder-grid">
          <div className="builder-main">
            {revisao && (
              <div className="card">
                <h3>Pedido do cliente</h3>
                <p className="texto-apoio" style={{ marginBottom: 0 }}>
                  {revisao.pedidoDoCliente ?? "O cliente pediu ajuste, mas não deixou uma mensagem."}
                </p>
                <p className="texto-apoio" style={{ margin: "10px 0 0" }}>
                  Ao enviar, o mesmo link passa a mostrar a nova versão e o cliente pode responder de novo. A versão
                  atual fica guardada como histórico.
                </p>
              </div>
            )}

            <div className="card">
              <h3>Cliente</h3>
              {revisao ? (
                <div className="cliente-fixo">{revisao.cliente.nome}</div>
              ) : (
                <>
                  <SeletorCliente selecionado={cliente} onSelecionar={setCliente} />
                  <div className="hint-linha">
                    Não achou? <Link href="/clientes/novo">Cadastrar novo cliente</Link>
                  </div>
                </>
              )}
            </div>

            <div className="card">
              <h3>Itens do orçamento</h3>

              <div className="item-header">
                <div>Descrição</div>
                <div>Qtd.</div>
                <div>Valor unit. (R$)</div>
                <div>Desconto (R$)</div>
                <div />
              </div>

              {itens.map((item) => (
                <div className="item-row" key={item.chave}>
                  <div className="field item-desc">
                    <span className="item-label">Descrição</span>
                    <input
                      type="text"
                      aria-label="Descrição"
                      placeholder="Ex: Sessão de personal training"
                      value={item.descricao}
                      onChange={(e) => atualizar(item.chave, "descricao", e.target.value)}
                    />
                  </div>
                  <div className="field">
                    <span className="item-label">Qtd.</span>
                    <input
                      type="text"
                      inputMode="numeric"
                      aria-label="Quantidade"
                      value={item.quantidade}
                      onChange={(e) => atualizar(item.chave, "quantidade", e.target.value)}
                    />
                  </div>
                  <div className="field">
                    <span className="item-label">Valor unit.</span>
                    <input
                      type="text"
                      inputMode="decimal"
                      aria-label="Valor unitário"
                      placeholder="0,00"
                      value={item.valor}
                      onChange={(e) => atualizar(item.chave, "valor", e.target.value)}
                    />
                  </div>
                  <div className="field">
                    <span className="item-label">Desconto</span>
                    <input
                      type="text"
                      inputMode="decimal"
                      aria-label="Desconto"
                      placeholder="0,00"
                      value={item.desconto}
                      onChange={(e) => atualizar(item.chave, "desconto", e.target.value)}
                    />
                  </div>
                  <button
                    type="button"
                    className="icon-btn"
                    aria-label="Remover item"
                    disabled={itens.length === 1}
                    onClick={() => removerItem(item.chave)}
                  >
                    ✕
                  </button>
                </div>
              ))}

              <button type="button" className="btn btn-text" style={{ paddingLeft: 0 }} onClick={adicionarItem}>
                ＋ Adicionar item
              </button>
            </div>

            <div className="card">
              <h3>Condições</h3>
              <div className="field-row">
                <div className="field">
                  <label htmlFor="validade">Validade da proposta</label>
                  <select id="validade" value={validade} onChange={(e) => setValidade(e.target.value)}>
                    <option value="7">7 dias (padrão)</option>
                    <option value="15">15 dias</option>
                    <option value="30">30 dias</option>
                    {validade !== "sem" && !VALIDADES_PADRAO.includes(validade) && (
                      <option value={validade}>{validade} dias</option>
                    )}
                    <option value="sem">Sem validade definida</option>
                  </select>
                </div>
                <div className="field">
                  <label htmlFor="pagamento">Forma de pagamento</label>
                  <input
                    id="pagamento"
                    type="text"
                    placeholder="Ex: Pix, à vista ou parcelado no cartão"
                    value={formaPagamento}
                    onChange={(e) => setFormaPagamento(e.target.value)}
                  />
                </div>
              </div>
              <div className="field" style={{ marginBottom: 0 }}>
                <label htmlFor="observacoes">Observações (opcional)</label>
                <textarea
                  id="observacoes"
                  value={observacoes}
                  onChange={(e) => setObservacoes(e.target.value)}
                />
              </div>
            </div>
          </div>

          <div className="builder-preview">
            <div className="preview-titulo">PRÉVIA DO ORÇAMENTO</div>
            <div className="preview-doc">
              <div className="pd-header">
                <div className="pd-logo">{iniciais(nomeNegocio) || "?"}</div>
                <div className="pd-biz">{nomeNegocio}</div>
              </div>
              <div className="pd-para">
                Para: <strong>{cliente ? cliente.nome : "—"}</strong>
              </div>

              {linhasPreview.length === 0 ? (
                <div className="pd-vazio">Os itens aparecem aqui conforme você preenche.</div>
              ) : (
                linhasPreview.map((item, indice) => (
                  <div className="pd-line" key={indice}>
                    <span>
                      {item.descricao}
                      {Number.isInteger(item.quantidade) && item.quantidade > 1 ? ` (×${item.quantidade})` : ""}
                    </span>
                    <span>{item.total === null ? "—" : formatarMoeda(item.total)}</span>
                  </div>
                ))
              )}

              <div className="pd-total">
                <span>Total</span>
                <span>{formatarMoeda(totalGeral)}</span>
              </div>
              <div className="pd-valid">
                {validade === "sem" ? "Sem validade definida" : `Válido por ${validade} dias`}
                {formaPagamento.trim() ? ` · ${formaPagamento.trim()}` : ""}
              </div>
            </div>
          </div>
        </div>
      </div>
    </form>
  );
}
