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

export type TipoDecisao = "ACEITO" | "RECUSADO" | "AJUSTE";

export type DecisaoAutonomo = {
  tipo: TipoDecisao;
  codigo: string;
  nome: string;
  cpfMascarado: string;
  ip: string;
  timestamp: string;
  comentario: string | null;
  hashDocumento: string;
};

export type DecisaoPublica = {
  tipo: TipoDecisao;
  codigo: string;
  nome: string;
  cpfMascarado: string;
  timestamp: string;
  comentario: string | null;
};

export type OrcamentoPublico = {
  nomeNegocio: string;
  localNegocio: string | null;
  clienteNome: string;
  itens: {
    descricao: string;
    quantidade: number;
    valorUnitario: number;
    desconto: number;
    total: number;
  }[];
  total: number;
  validadeDias: number | null;
  validaAte: string | null;
  formaPagamento: string | null;
  observacoes: string | null;
  criadoEm: string;
  versao: number;
  status: StatusOrcamento;
  decisao: DecisaoPublica | null;
  visualizacaoDoDono: boolean;
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
  decisao: DecisaoAutonomo | null;
  versaoAtualId: number;
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

export type RevisaoOrcamentoPayload = Omit<NovoOrcamentoPayload, "clienteId">;
