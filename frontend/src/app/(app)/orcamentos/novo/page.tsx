import { obterSessao } from "@/lib/session";
import { FormularioOrcamento } from "@/components/formulario-orcamento";

export default async function NovoOrcamentoPage() {
  const sessao = await obterSessao();

  return <FormularioOrcamento nomeNegocio={sessao?.nomeNegocio ?? ""} />;
}
