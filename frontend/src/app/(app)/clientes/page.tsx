import Link from "next/link";
import { cookies } from "next/headers";
import { apiGetAuth } from "@/lib/api";
import type { PageResponse } from "@/lib/types";
import { COOKIE_TAMANHO, urlClientes } from "@/lib/clientes-url";
import { AutoTamanhoPagina } from "@/components/auto-tamanho-pagina";

type Cliente = {
  id: number;
  nome: string;
  whatsapp: string;
  cpf: string | null;
  email: string | null;
  observacoesInternas: string | null;
  criadoEm: string;
};

const TAMANHO_INICIAL = 10;

function mascararCpf(cpf: string | null) {
  if (!cpf) return "—";
  const digitos = cpf.replace(/\D/g, "");
  if (digitos.length !== 11) return cpf;
  return `•••.•••.${digitos.slice(6, 9)}-${digitos.slice(9)}`;
}

function iniciais(nome: string) {
  return nome
    .split(" ")
    .slice(0, 2)
    .map((p) => p[0])
    .join("")
    .toUpperCase();
}

export default async function ClientesPage({
  searchParams,
}: {
  searchParams: Promise<{ pagina?: string; busca?: string }>;
}) {
  const params = await searchParams;
  const cookieStore = await cookies();

  const paginaUrl = Math.floor(Number(params.pagina ?? "1")) || 1;
  const pagina = Math.max(0, Math.min(100000, paginaUrl - 1));
  const tamanho = Math.max(
    5,
    Math.min(100, Math.floor(Number(cookieStore.get(COOKIE_TAMANHO)?.value)) || TAMANHO_INICIAL)
  );
  const busca = params.busca ?? "";

  const query = new URLSearchParams({ pagina: String(pagina), tamanho: String(tamanho) });
  if (busca) query.set("busca", busca);

  const resultado = await apiGetAuth<PageResponse<Cliente>>(`/clientes?${query.toString()}`);

  return (
    <>
      <div className="topbar">
        <h1>Clientes</h1>
        <Link href="/clientes/novo" className="btn btn-primary">
          ＋ Novo cliente
        </Link>
      </div>
      <div className="content content-fit">
        <form className="filter-bar" action="/clientes">
          <input key={busca} type="text" name="busca" placeholder="Buscar por nome..." defaultValue={busca} />
          <button className="btn btn-outline" type="submit">
            Buscar
          </button>
        </form>

        <div className="table-wrap" data-table-wrap>
          {resultado.conteudo.length === 0 ? (
            <div className="empty-state">
              {busca
                ? `Nenhum cliente encontrado para "${busca}".`
                : 'Você ainda não cadastrou nenhum cliente. Clica em "Novo cliente" pra começar.'}
            </div>
          ) : (
            <table className="tabela-linhas">
              <thead>
                <tr>
                  <th>Cliente</th>
                  <th>WhatsApp</th>
                  <th>CPF</th>
                  <th>E-mail</th>
                </tr>
              </thead>
              <tbody>
                {resultado.conteudo.map((cliente) => (
                  <tr key={cliente.id}>
                    <td>
                      <div className="client-row-name">
                        <div className="avatar-sm">{iniciais(cliente.nome)}</div>
                        <strong>{cliente.nome}</strong>
                      </div>
                    </td>
                    <td>{cliente.whatsapp}</td>
                    <td>{mascararCpf(cliente.cpf)}</td>
                    <td>{cliente.email || "—"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>

        <div className="pagination">
          <span>
            {resultado.totalElementos === 0
              ? "0 clientes"
              : `Página ${resultado.pagina + 1} de ${resultado.totalPaginas} · ${resultado.totalElementos} clientes`}
          </span>
          <div className="pagination-links">
            {pagina > 0 ? (
              <Link href={urlClientes(pagina - 1, busca)}>← Anterior</Link>
            ) : (
              <span className="disabled">← Anterior</span>
            )}
            {pagina + 1 < resultado.totalPaginas ? (
              <Link href={urlClientes(pagina + 1, busca)}>Próxima →</Link>
            ) : (
              <span className="disabled">Próxima →</span>
            )}
          </div>
        </div>
      </div>

      <AutoTamanhoPagina tamanhoAtual={tamanho} pagina={pagina} busca={busca} />
    </>
  );
}
