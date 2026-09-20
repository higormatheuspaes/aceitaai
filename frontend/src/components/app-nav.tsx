"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

const ITENS = [
  { href: "/dashboard", icone: "📋", rotulo: "Orçamentos" },
  { href: "/clientes", icone: "👥", rotulo: "Clientes" },
];

export function AppNav() {
  const pathname = usePathname();

  return (
    <nav>
      {ITENS.map((item) => (
        <Link
          key={item.href}
          href={item.href}
          className={pathname.startsWith(item.href) ? "current" : undefined}
        >
          <span className="icone">{item.icone}</span>
          {item.rotulo}
        </Link>
      ))}
    </nav>
  );
}
