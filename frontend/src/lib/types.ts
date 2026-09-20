export type PageResponse<T> = {
  conteudo: T[];
  pagina: number;
  tamanho: number;
  totalElementos: number;
  totalPaginas: number;
};

export type StatusOrcamento = "PENDENTE" | "ACEITO" | "RECUSADO" | "AJUSTE" | "EXPIRADO";

export type OrcamentoResumo = {
  id: number;
  clienteId: number;
  clienteNome: string;
  descricao: string;
  total: number;
  status: StatusOrcamento;
  criadoEm: string;
  linkSlug: string;
};

export type ItemOrcamento = {
  id: number;
  descricao: string;
  quantidade: number;
  valorUnitario: number;
  desconto: number;
  total: number;
};

export type Orcamento = {
  id: number;
  clienteId: number;
  clienteNome: string;
  clienteWhatsapp: string;
  versao: number;
  status: StatusOrcamento;
  validadeDias: number | null;
  validaAte: string | null;
  formaPagamento: string | null;
  observacoes: string | null;
  linkSlug: string;
  criadoEm: string;
  itens: ItemOrcamento[];
  total: number;
};

export type DashboardResumo = {
  enviadosNoMes: number;
  aceitosNoMes: number;
  taxaAceite: number | null;
};

export type ClienteOpcao = {
  id: number;
  nome: string;
  whatsapp: string;
};

export type NovoOrcamentoPayload = {
  clienteId: number;
  validadeDias: number | null;
  formaPagamento: string;
  observacoes: string;
  itens: {
    descricao: string;
    quantidade: number;
    valorUnitario: number;
    desconto: number;
  }[];
};
