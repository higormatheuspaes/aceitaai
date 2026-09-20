import Link from "next/link";

export function NaoEncontrado() {
  return (
    <div className="nao-encontrado">
      <div className="nao-encontrado-codigo">404</div>
      <h1>Não encontramos essa página</h1>
      <p>
        O endereço pode estar errado, ou o item não existe ou não pertence à sua conta. Confere o
        link e tenta de novo.
      </p>
      <div className="nao-encontrado-acoes">
        <Link href="/dashboard" className="btn btn-primary">
          Ir para orçamentos
        </Link>
        <Link href="/clientes" className="btn btn-outline">
          Ver clientes
        </Link>
      </div>
    </div>
  );
}
