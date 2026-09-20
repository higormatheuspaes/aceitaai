import { redirect } from "next/navigation";
import { obterSessao } from "@/lib/session";
import { AppNav } from "@/components/app-nav";

export default async function AppLayout({ children }: { children: React.ReactNode }) {
  const sessao = await obterSessao();

  if (!sessao) {
    redirect("/login");
  }

  const iniciais = sessao.nomeNegocio
    ?.split(" ")
    .slice(0, 2)
    .map((palavra) => palavra[0])
    .join("")
    .toUpperCase();

  return (
    <div className="app-shell">
      <div className="sidebar">
        <div className="brand">
          Aceita<span className="dot">.ai</span>
        </div>
        <AppNav />
        <div className="biz">
          <div className="avatar">{iniciais || "?"}</div>
          <div>{sessao.nomeNegocio || sessao.email}</div>
        </div>
      </div>

      <div className="mobile-header">
        <div className="brand">
          Aceita<span className="dot">.ai</span>
        </div>
        <div className="avatar">{iniciais || "?"}</div>
      </div>

      <div className="main">{children}</div>

      <div className="mobile-tabs">
        <AppNav />
      </div>
    </div>
  );
}
