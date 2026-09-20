"use client";

import { useState } from "react";

export function CopiarLink({ link }: { link: string }) {
  const [copiado, setCopiado] = useState(false);

  async function copiar() {
    try {
      await navigator.clipboard.writeText(link);
      setCopiado(true);
      setTimeout(() => setCopiado(false), 2000);
    } catch {
      window.prompt("Copie o link abaixo:", link);
    }
  }

  return (
    <button type="button" className="btn btn-outline" onClick={copiar}>
      {copiado ? "Copiado ✓" : "Copiar link"}
    </button>
  );
}
