"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { COOKIE_TAMANHO, urlClientes } from "@/lib/clientes-url";

const ALTURA_LINHA_PADRAO = 52;
const ALTURA_CABECALHO_PADRAO = 40;

export function AutoTamanhoPagina({
  tamanhoAtual,
  pagina,
  busca,
}: {
  tamanhoAtual: number;
  pagina: number;
  busca: string;
}) {
  const router = useRouter();

  useEffect(() => {
    let timer: ReturnType<typeof setTimeout>;

    function ajustar() {
      const area = document.querySelector<HTMLElement>("[data-table-wrap]");
      if (!area) return;

      const cabecalho = area.querySelector<HTMLElement>("thead");
      const linha = area.querySelector<HTMLElement>("tbody tr");
      const alturaLinha = linha?.getBoundingClientRect().height || ALTURA_LINHA_PADRAO;
      const alturaCabecalho = cabecalho?.getBoundingClientRect().height || ALTURA_CABECALHO_PADRAO;

      const disponivel = area.clientHeight - alturaCabecalho - 2;
      const novoTamanho = Math.max(5, Math.min(100, Math.floor(disponivel / alturaLinha)));
      if (novoTamanho === tamanhoAtual) return;

      document.cookie = `${COOKIE_TAMANHO}=${novoTamanho}; path=/; max-age=31536000; samesite=lax`;

      const novaPagina = Math.floor((pagina * tamanhoAtual) / novoTamanho);
      const destino = urlClientes(novaPagina, busca);
      const atual = window.location.pathname + window.location.search;

      if (destino === atual) {
        router.refresh();
      } else {
        router.replace(destino);
      }
    }

    function aoRedimensionar() {
      clearTimeout(timer);
      timer = setTimeout(ajustar, 200);
    }

    ajustar();
    window.addEventListener("resize", aoRedimensionar);
    return () => {
      clearTimeout(timer);
      window.removeEventListener("resize", aoRedimensionar);
    };
  }, [tamanhoAtual, pagina, busca, router]);

  return null;
}
