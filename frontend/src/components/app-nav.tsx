"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

const ITENS = [
  {
    href: "/dashboard",
    icone: "📋",
    rotulo: "Orçamentos",
    ativo: (p: string) =>
      p.startsWith("/dashboard") || (p.startsWith("/orcamentos") && p !== "/orcamentos/novo"),
  },
  {
    href: "/orcamentos/novo",
    icone: "＋",
    rotulo: "Novo orçamento",
    ativo: (p: string) => p === "/orcamentos/novo",
  },
  {
    href: "/clientes",
    icone: "👥",
    rotulo: "Clientes",
    ativo: (p: string) => p.startsWith("/clientes"),
  },
];

export function AppNav() {
  const pathname = usePathname();

  return (
    <nav>
      {ITENS.map((item) => (
        <Link key={item.href} href={item.href} className={item.ativo(pathname) ? "current" : undefined}>
          <span className="icone">{item.icone}</span>
          {item.rotulo}
        </Link>
      ))}
    </nav>
  );
}
