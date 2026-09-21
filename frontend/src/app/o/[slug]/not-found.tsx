export default function OrcamentoNaoEncontrado() {
  return (
    <div className="pagina-centralizada">
      <div className="marca">
        Aceita<span className="dot">.ai</span>
      </div>
      <div className="nao-encontrado">
        <div className="nao-encontrado-codigo">404</div>
        <h1>Orçamento não encontrado</h1>
        <p>
          Esse link não corresponde a nenhum orçamento. Confira se ele foi copiado por inteiro ou peça um novo
          link a quem enviou.
        </p>
      </div>
    </div>
  );
}
