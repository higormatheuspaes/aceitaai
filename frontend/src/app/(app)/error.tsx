"use client";

export default function AppError({
  retry,
}: {
  error: Error & { digest?: string };
  retry: () => void;
}) {
  return (
    <div className="content">
      <div className="card" style={{ maxWidth: 480 }}>
        <h3>Algo deu errado</h3>
        <p style={{ fontSize: 13.5, color: "var(--text-muted)", margin: "0 0 16px" }}>
          Não foi possível carregar essa tela agora. Costuma ser algo passageiro.
        </p>
        <button className="btn btn-primary" type="button" onClick={() => retry()}>
          Tentar de novo
        </button>
      </div>
    </div>
  );
}
