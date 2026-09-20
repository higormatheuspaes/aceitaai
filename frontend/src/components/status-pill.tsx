import type { StatusOrcamento } from "@/lib/types";

const MAPA: Record<StatusOrcamento, { classe: string; rotulo: string }> = {
  PENDENTE: { classe: "pendente", rotulo: "Pendente" },
  ACEITO: { classe: "aceito", rotulo: "Aceito" },
  RECUSADO: { classe: "recusado", rotulo: "Recusado" },
  AJUSTE: { classe: "ajuste", rotulo: "Em ajuste" },
  EXPIRADO: { classe: "expirado", rotulo: "Expirado" },
};

export function StatusPill({ status }: { status: StatusOrcamento }) {
  const { classe, rotulo } = MAPA[status];
  return (
    <span className={`status-pill ${classe}`}>
      <span className="dot" />
      {rotulo}
    </span>
  );
}
