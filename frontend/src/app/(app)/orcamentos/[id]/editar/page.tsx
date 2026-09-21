import { notFound, redirect } from "next/navigation";
import { ApiError, apiGetAuth } from "@/lib/api";
import { obterSessao } from "@/lib/session";
import type { Orcamento } from "@/lib/types";
import { FormularioOrcamento } from "@/components/formulario-orcamento";

export default async function EditarOrcamentoPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = await params;

  let orcamento: Orcamento;
  try {
    orcamento = await apiGetAuth<Orcamento>(`/orcamentos/${encodeURIComponent(id)}`);
  } catch (error) {
    if (error instanceof ApiError && (error.status === 404 || error.status === 400)) notFound();
    throw error;
  }

  // So da pra editar quando o cliente pediu ajuste e esta e a versao mais recente.
  if (orcamento.status !== "AJUSTE" || orcamento.versaoAtualId !== orcamento.id) {
    redirect(`/orcamentos/${orcamento.id}`);
  }

  const sessao = await obterSessao();

  return (
    <FormularioOrcamento
      nomeNegocio={sessao?.nomeNegocio ?? ""}
      revisao={{
        orcamentoId: orcamento.id,
        cliente: { id: orcamento.clienteId, nome: orcamento.clienteNome, whatsapp: orcamento.clienteWhatsapp },
        itens: orcamento.itens.map((item) => ({
          descricao: item.descricao,
          quantidade: item.quantidade,
          valorUnitario: item.valorUnitario,
          desconto: item.desconto,
        })),
        validadeDias: orcamento.validadeDias,
        formaPagamento: orcamento.formaPagamento,
        observacoes: orcamento.observacoes,
        pedidoDoCliente: orcamento.decisao?.comentario ?? null,
      }}
    />
  );
}
