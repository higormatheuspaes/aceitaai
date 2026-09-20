import Link from "next/link";
import { cookies } from "next/headers";
import { sair } from "@/app/actions/auth";
import { apiGetAuth } from "@/lib/api";
import { formatarDataHora, formatarMoeda } from "@/lib/formato";
import { cookieTamanho, urlLista } from "@/lib/lista-url";
import type { DashboardResumo, OrcamentoResumo, PageResponse } from "@/lib/types";
import { AutoTamanhoPagina } from "@/components/auto-tamanho-pagina";
import { StatusPill } from "@/components/status-pill";

const TAMANHO_INICIAL = 10;

export default async function DashboardPage({
  searchParams,
}: {
  searchParams: Promise<{ pagina?: string }>;
}) {
  const params = await searchParams;
  const cookieStore = await cookies();

  const paginaUrl = Math.floor(Number(params.pagina ?? "1")) || 1;
  const pagina = Math.max(0, Math.min(100000, paginaUrl - 1));
  const tamanho = Math.max(
    5,
    Math.min(100, Math.floor(Number(cookieStore.get(cookieTamanho("dashboard"))?.value)) || TAMANHO_INICIAL)
  );

  const [resumo, resultado] = await Promise.all([
    apiGetAuth<DashboardResumo>("/orcamentos/resumo"),
    apiGetAuth<PageResponse<OrcamentoResumo>>(`/orcamentos?pagina=${pagina}&tamanho=${tamanho}`),
  ]);

  return (
    <>
      <div className="topbar">
        <h1>Orçamentos</h1>
        <div style={{ display: "flex", gap: 10 }}>
          <Link href="/orcamentos/novo" className="btn btn-primary">
            ＋ Novo orçamento
          </Link>
          <form action={sair}>
            <button className="btn btn-outline" type="submit">
              Sair
            </button>
          </form>
        </div>
      </div>

      <div className="content content-fit">
        <div className="stat-row">
          <div className="stat">
            <div className="label">Enviados este mês</div>
            <div className="value">{resumo.enviadosNoMes}</div>
          </div>
          <div className="stat">
            <div className="label">Aceitos</div>
            <div className="value">{resumo.aceitosNoMes}</div>
          </div>
          <div className="stat">
            <div className="label">Taxa de aceite</div>
            <div className="value">{resumo.taxaAceite === null ? "—" : `${resumo.taxaAceite}%`}</div>
          </div>
        </div>

        <div className="table-wrap" data-table-wrap>
          {resultado.conteudo.length === 0 ? (
            <div className="empty-state">
              Você ainda não enviou nenhum orçamento. Clica em &quot;Novo orçamento&quot; pra
              começar.
            </div>
          ) : (
            <table className="tabela-linhas">
              <thead>
                <tr>
                  <th>Cliente</th>
                  <th>Descrição</th>
                  <th>Valor</th>
                  <th>Status</th>
                  <th>Enviado em</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {resultado.conteudo.map((orcamento) => (
                  <tr key={orcamento.id}>
                    <td>
                      <strong>{orcamento.clienteNome}</strong>
                    </td>
                    <td className="cell-ellipsis">{orcamento.descricao}</td>
                    <td className="num">{formatarMoeda(orcamento.total)}</td>
                    <td>
                      <StatusPill status={orcamento.status} />
                    </td>
                    <td>{formatarDataHora(orcamento.criadoEm)}</td>
                    <td>
                      <Link href={`/orcamentos/${orcamento.id}`} className="link-acao">
                        Ver
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>

        <div className="pagination">
          <span>
            {resultado.totalElementos === 0
              ? "0 orçamentos"
              : `Página ${resultado.pagina + 1} de ${resultado.totalPaginas} · ${resultado.totalElementos} orçamentos`}
          </span>
          <div className="pagination-links">
            {pagina > 0 ? (
              <Link href={urlLista("dashboard", pagina - 1)}>← Anterior</Link>
            ) : (
              <span className="disabled">← Anterior</span>
            )}
            {pagina + 1 < resultado.totalPaginas ? (
              <Link href={urlLista("dashboard", pagina + 1)}>Próxima →</Link>
            ) : (
              <span className="disabled">Próxima →</span>
            )}
          </div>
        </div>
      </div>

      <AutoTamanhoPagina rota="dashboard" tamanhoAtual={tamanho} pagina={pagina} />
    </>
  );
}
