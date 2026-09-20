import { sair } from "@/app/actions/auth";

export default function DashboardPage() {
  return (
    <>
      <div className="topbar">
        <h1>Orçamentos</h1>
        <form action={sair}>
          <button className="btn btn-outline" type="submit">
            Sair
          </button>
        </form>
      </div>
      <div className="content">
        <div className="stat-row">
          <div className="stat">
            <div className="label">Enviados este mês</div>
            <div className="value">0</div>
          </div>
          <div className="stat">
            <div className="label">Aceitos</div>
            <div className="value">0</div>
          </div>
          <div className="stat">
            <div className="label">Taxa de aceite</div>
            <div className="value">—</div>
          </div>
        </div>

        <div className="table-wrap">
          <div className="empty-state">
            Você ainda não enviou nenhum orçamento. Essa tela vai listar seus orçamentos assim
            que essa parte do sistema estiver pronta.
          </div>
        </div>
      </div>
    </>
  );
}
