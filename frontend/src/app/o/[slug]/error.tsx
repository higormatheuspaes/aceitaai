"use client";

export default function ErroOrcamentoPublico({
  retry,
}: {
  error: Error & { digest?: string };
  retry: () => void;
}) {
  return (
    <div className="pagina-centralizada">
      <div className="marca">
        Aceita<span className="dot">.ai</span>
      </div>
      <div className="nao-encontrado">
        <h1>Não foi possível abrir o orçamento</h1>
        <p>Costuma ser algo passageiro. Tente de novo em instantes.</p>
        <div className="nao-encontrado-acoes">
          <button type="button" className="btn btn-primary" onClick={() => retry()}>
            Tentar de novo
          </button>
        </div>
      </div>
    </div>
  );
}
