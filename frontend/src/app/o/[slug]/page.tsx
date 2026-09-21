import type { Metadata } from "next";
import Link from "next/link";
import { notFound } from "next/navigation";
import { ApiError, apiGetAuth } from "@/lib/api";
import { formatarData, formatarDataHora, formatarMoeda } from "@/lib/formato";
import type { DecisaoPublica, OrcamentoPublico } from "@/lib/types";
import { PainelDecisao } from "@/components/painel-decisao";

export const metadata: Metadata = {
  title: "Orçamento",
  robots: { index: false, follow: false },
};

function Resultado({ decisao, nomeNegocio }: { decisao: DecisaoPublica; nomeNegocio: string }) {
  const quando = formatarDataHora(decisao.timestamp);

  const conteudo = {
    ACEITO: {
      classe: "",
      icone: "✓",
      titulo: "Orçamento aceito",
      texto: "Guarde o código do comprovante abaixo. Ele identifica o seu aceite.",
    },
    RECUSADO: {
      classe: "recusado",
      icone: "✕",
      titulo: "Orçamento recusado",
      texto: `Sua resposta foi registrada. Se mudar de ideia, fale com ${nomeNegocio}.`,
    },
    AJUSTE: {
      classe: "ajuste",
      icone: "↺",
      titulo: "Pedido de ajuste enviado",
      texto: `${nomeNegocio} vai analisar o que você pediu e retornar com uma nova versão.`,
    },
  }[decisao.tipo];

  return (
    <div className="accept-panel resultado">
      <div className={`confirm-check ${conteudo.classe}`}>{conteudo.icone}</div>
      <h3>{conteudo.titulo}</h3>
      <p className="texto-apoio">{conteudo.texto}</p>

      <div className="receipt-box">
        <div>
          <span>Resposta de</span>
          <span>{decisao.nome}</span>
        </div>
        <div>
          <span>CPF</span>
          <span>{decisao.cpfMascarado}</span>
        </div>
        <div>
          <span>Data e hora</span>
          <span>{quando}</span>
        </div>
        <div>
          <span>Código do comprovante</span>
          <span>{decisao.codigo}</span>
        </div>
      </div>

      {decisao.comentario && (
        <p className="texto-apoio" style={{ marginBottom: 0 }}>
          <strong>Sua mensagem:</strong> {decisao.comentario}
        </p>
      )}
    </div>
  );
}

export default async function PaginaPublicaOrcamento({ params }: { params: Promise<{ slug: string }> }) {
  const { slug } = await params;

  let orcamento: OrcamentoPublico;
  try {
    orcamento = await apiGetAuth<OrcamentoPublico>(`/publico/orcamentos/${encodeURIComponent(slug)}`);
  } catch (error) {
    if (error instanceof ApiError && (error.status === 404 || error.status === 400)) notFound();
    throw error;
  }

  const { decisao } = orcamento;
  const expirado = !decisao && orcamento.status === "EXPIRADO";

  return (
    <div className="public-wrap">
      <div className="public-doc">
        <div className="pd-brandbar">
          <div className="mark">A</div>
          <span>Orçamento gerado via Aceita.ai</span>
        </div>

        <div className="public-card">
          <div className="biz-name">
            {orcamento.nomeNegocio}
            {orcamento.localNegocio ? ` · ${orcamento.localNegocio}` : ""}
          </div>
          <h1>Orçamento de serviço</h1>
          <div className="to-line">
            Preparado para <strong>{orcamento.clienteNome}</strong> · enviado em {formatarData(orcamento.criadoEm)}
          </div>

          <table className="public-table">
            <thead>
              <tr>
                <th>Item</th>
                <th className="centro">Qtd.</th>
                <th className="direita">Valor</th>
              </tr>
            </thead>
            <tbody>
              {orcamento.itens.map((item, indice) => (
                <tr key={indice}>
                  <td>
                    {item.descricao}
                    {item.desconto > 0 && (
                      <div className="nota-desconto">desconto de {formatarMoeda(item.desconto)}</div>
                    )}
                  </td>
                  <td className="centro">{item.quantidade}</td>
                  <td className="direita">{formatarMoeda(item.total)}</td>
                </tr>
              ))}
            </tbody>
          </table>

          <div className="total-line">
            <span className="lbl">Total</span>
            <span className="val">{formatarMoeda(orcamento.total)}</span>
          </div>

          <div className="conditions">
            <div>
              <div className="lbl">Validade</div>
              <div className="v">
                {orcamento.validaAte ? `até ${formatarData(orcamento.validaAte)}` : "Sem validade definida"}
              </div>
            </div>
            {orcamento.formaPagamento && (
              <div>
                <div className="lbl">Pagamento</div>
                <div className="v">{orcamento.formaPagamento}</div>
              </div>
            )}
            {orcamento.observacoes && (
              <div>
                <div className="lbl">Observação</div>
                <div className="v">{orcamento.observacoes}</div>
              </div>
            )}
          </div>
        </div>

        {decisao ? (
          <Resultado decisao={decisao} nomeNegocio={orcamento.nomeNegocio} />
        ) : expirado ? (
          <div className="accept-panel">
            <h3>Este orçamento expirou</h3>
            <p className="texto-apoio" style={{ marginBottom: 0 }}>
              O prazo para responder terminou
              {orcamento.validaAte ? ` em ${formatarData(orcamento.validaAte)}` : ""}. Peça um novo orçamento a{" "}
              {orcamento.nomeNegocio}.
            </p>
          </div>
        ) : orcamento.visualizacaoDoDono ? (
          <div className="accept-panel">
            <h3>Você está vendo como o cliente vê</h3>
            <p className="texto-apoio" style={{ marginBottom: 14 }}>
              Você é o responsável por este orçamento, então não pode respondê-lo. Envie o link ao cliente: só ele
              responde por aqui.
            </p>
            <Link href="/dashboard" className="btn btn-outline">
              Voltar para meus orçamentos
            </Link>
          </div>
        ) : (
          <PainelDecisao slug={slug} nomeNegocio={orcamento.nomeNegocio} total={orcamento.total} />
        )}
      </div>
    </div>
  );
}
