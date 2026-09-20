"use client";

import Link from "next/link";
import { useEffect, useId, useRef, useState } from "react";
import { iniciais } from "@/lib/formato";
import type { ClienteOpcao } from "@/lib/types";

export function SeletorCliente({
  selecionado,
  onSelecionar,
}: {
  selecionado: ClienteOpcao | null;
  onSelecionar: (cliente: ClienteOpcao | null) => void;
}) {
  const [texto, setTexto] = useState("");
  const [opcoes, setOpcoes] = useState<ClienteOpcao[]>([]);
  const [aberto, setAberto] = useState(false);
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState(false);
  const [ativo, setAtivo] = useState(0);
  const requisicao = useRef(0);
  const idLista = useId();

  useEffect(() => {
    if (!aberto) return;

    const id = ++requisicao.current;
    const timer = setTimeout(async () => {
      setCarregando(true);
      setErro(false);
      try {
        const resposta = await fetch(`/api/clientes?busca=${encodeURIComponent(texto)}`);
        if (!resposta.ok) throw new Error("falha");
        const dados: ClienteOpcao[] = await resposta.json();
        if (id === requisicao.current) {
          setOpcoes(dados);
          setAtivo(0);
        }
      } catch {
        if (id === requisicao.current) setErro(true);
      } finally {
        if (id === requisicao.current) setCarregando(false);
      }
    }, 250);

    return () => clearTimeout(timer);
  }, [texto, aberto]);

  function escolher(cliente: ClienteOpcao) {
    onSelecionar(cliente);
    setAberto(false);
    setTexto("");
  }

  function aoTeclar(evento: React.KeyboardEvent<HTMLInputElement>) {
    if (evento.key === "ArrowDown") {
      evento.preventDefault();
      setAtivo((i) => Math.min(i + 1, Math.max(opcoes.length - 1, 0)));
    } else if (evento.key === "ArrowUp") {
      evento.preventDefault();
      setAtivo((i) => Math.max(i - 1, 0));
    } else if (evento.key === "Enter") {
      evento.preventDefault();
      if (opcoes[ativo]) escolher(opcoes[ativo]);
    } else if (evento.key === "Escape") {
      setAberto(false);
    }
  }

  if (selecionado) {
    return (
      <div className="picker-chip">
        <div className="avatar-sm">{iniciais(selecionado.nome)}</div>
        <div className="info">
          <strong>{selecionado.nome}</strong>
          <span>{selecionado.whatsapp}</span>
        </div>
        <button type="button" className="btn btn-text" onClick={() => onSelecionar(null)}>
          Trocar
        </button>
      </div>
    );
  }

  return (
    <div className="picker">
      <input
        type="text"
        role="combobox"
        aria-expanded={aberto}
        aria-controls={idLista}
        aria-autocomplete="list"
        autoComplete="off"
        placeholder="Buscar cliente por nome..."
        value={texto}
        onChange={(e) => setTexto(e.target.value)}
        onFocus={() => setAberto(true)}
        onBlur={() => setAberto(false)}
        onKeyDown={aoTeclar}
      />
      {aberto && (
        <ul id={idLista} className="picker-list" role="listbox" onMouseDown={(e) => e.preventDefault()}>
          {carregando && opcoes.length === 0 && <li className="picker-vazio">Buscando...</li>}
          {erro && <li className="picker-vazio">Não foi possível buscar agora. Tenta de novo.</li>}
          {!carregando && !erro && opcoes.length === 0 && (
            <li className="picker-vazio">
              Nenhum cliente encontrado. <Link href="/clientes/novo">Cadastrar novo cliente</Link>
            </li>
          )}
          {opcoes.map((cliente, indice) => (
            <li
              key={cliente.id}
              role="option"
              aria-selected={indice === ativo}
              className={indice === ativo ? "picker-item ativo" : "picker-item"}
              onClick={() => escolher(cliente)}
              onMouseEnter={() => setAtivo(indice)}
            >
              <span>{cliente.nome}</span>
              <small>{cliente.whatsapp}</small>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
