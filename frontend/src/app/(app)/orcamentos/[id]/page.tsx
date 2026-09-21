import Link from "next/link";
import { notFound } from "next/navigation";
import { ApiError, apiGetAuth } from "@/lib/api";
import { formatarData, formatarDataHora, formatarMoeda } from "@/lib/formato";
import { obterSessao } from "@/lib/session";
import type { Orcamento } from "@/lib/types";
import { CopiarLink } from "@/components/copiar-link";
import { StatusPill } from "@/components/status-pill";

const ROTULO_DECISAO = {
  ACEITO: "Aceitou o orçamento",
  RECUSADO: "Recusou o orçamento",
  AJUSTE: "Pediu ajuste",
} as const;

function linkWhatsapp(whatsapp: string, mensagem: string) {
  const digitos = whatsapp.replace(/\D/g, "");
  const numero = digitos.length <= 11 ? `55${digitos}` : digitos;
  return `https://wa.me/${numero}?text=${encodeURIComponent(mensagem)}`;
}

export default async function OrcamentoPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = await params;

  let orcamento: Orcamento;
  try {
    orcamento = await apiGetAuth<Orcamento>(`/orcamentos/${encodeURIComponent(id)}`);
  } catch (error) {
    if (error instanceof ApiError && (error.status === 404 || error.status === 400)) notFound();
    throw error;
  }

  const sessao = await obterSessao();
  const baseUrl = process.env.APP_URL ?? "http://localhost:3000";
  const link = `${baseUrl}/o/${orcamento.linkSlug}`;
  const mensagem = `Olá, ${orcamento.clienteNome}! Segue o orçamento de ${sessao?.nomeNegocio ?? "nosso atendimento"}: ${link}`;

  return (
    <>
      <div className="topbar">
        <h1>Orçamento #{orcamento.id}</h1>
        <div style={{ display: "flex", gap: 10, alignItems: "center" }}>
          <StatusPill status={orcamento.status} />
          <Link href="/dashboard" className="btn btn-outline">
            Voltar
          </Link>
        </div>
      </div>

      <div className="content">
        <div className="builder-grid">
          <div className="builder-main">
            {orcamento.decisao && (
              <div className="card">
                <h3>Resposta do cliente</h3>
                <dl className="detail-list">
                  <dt>Resposta</dt>
                  <dd>{ROTULO_DECISAO[orcamento.decisao.tipo]}</dd>
                  <dt>Nome informado</dt>
                  <dd>{orcamento.decisao.nome}</dd>
                  <dt>CPF</dt>
                  <dd>{orcamento.decisao.cpfMascarado}</dd>
                  <dt>Data e hora</dt>
                  <dd>{formatarDataHora(orcamento.decisao.timestamp)}</dd>
                  <dt>IP</dt>
                  <dd>{orcamento.decisao.ip}</dd>
                  <dt>Comprovante</dt>
                  <dd>{orcamento.decisao.codigo}</dd>
                  <dt>Hash SHA-256</dt>
                  <dd className="hash">{orcamento.decisao.hashDocumento}</dd>
                  {orcamento.decisao.comentario && (
                    <>
                      <dt>Mensagem</dt>
                      <dd>{orcamento.decisao.comentario}</dd>
                    </>
                  )}
                </dl>
              </div>
            )}

            <div className="card">
              <h3>Resumo</h3>
              <dl className="detail-list">
                <dt>Cliente</dt>
                <dd>{orcamento.clienteNome}</dd>
                <dt>Criado em</dt>
                <dd>{formatarDataHora(orcamento.criadoEm)}</dd>
                <dt>Validade</dt>
                <dd>{orcamento.validaAte ? `até ${formatarData(orcamento.validaAte)}` : "Sem validade definida"}</dd>
                <dt>Pagamento</dt>
                <dd>{orcamento.formaPagamento ?? "—"}</dd>
                {orcamento.observacoes && (
                  <>
                    <dt>Observações</dt>
                    <dd>{orcamento.observacoes}</dd>
                  </>
                )}
              </dl>
            </div>

            <div className="card">
              <h3>Itens</h3>
              <div className="table-wrap">
                <table>
                  <thead>
                    <tr>
                      <th>Item</th>
                      <th>Qtd.</th>
                      <th>Unit.</th>
                      <th>Desconto</th>
                      <th>Total</th>
                    </tr>
                  </thead>
                  <tbody>
                    {orcamento.itens.map((item) => (
                      <tr key={item.id}>
                        <td>{item.descricao}</td>
                        <td>{item.quantidade}</td>
                        <td>{formatarMoeda(item.valorUnitario)}</td>
                        <td>{item.desconto > 0 ? formatarMoeda(item.desconto) : "—"}</td>
                        <td className="num">{formatarMoeda(item.total)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
              <div className="pd-total" style={{ fontSize: 18 }}>
                <span>Total</span>
                <span>{formatarMoeda(orcamento.total)}</span>
              </div>
            </div>
          </div>

          <div className="builder-preview">
            <div className="card">
              <h3>Enviar ao cliente</h3>
              <p className="texto-apoio">
                Esse é o link único do orçamento. O cliente abre, confere e responde por ele.
              </p>
              <div className="link-box">{link}</div>
              <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
                <CopiarLink link={link} />
                <a
                  className="btn btn-green"
                  href={linkWhatsapp(orcamento.clienteWhatsapp, mensagem)}
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  Enviar por WhatsApp
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
