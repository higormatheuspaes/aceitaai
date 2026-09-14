import { redirect } from "next/navigation";
import { obterSessao } from "@/lib/session";
import { sair } from "@/app/actions/auth";

export default async function DashboardPage() {
  const sessao = await obterSessao();

  if (!sessao) {
    redirect("/login");
  }

  return (
    <div style={{ padding: 40 }}>
      <h1 style={{ fontFamily: "var(--font-display)", fontSize: 22 }}>Orçamentos</h1>
      <p style={{ color: "var(--text-muted)" }}>
        Logado como <strong>{sessao.email}</strong> (autônomo #{sessao.autonomoId})
      </p>
      <form action={sair} style={{ marginTop: 20 }}>
        <button className="btn btn-primary" type="submit">
          Sair
        </button>
      </form>
    </div>
  );
}
