export function cookieTamanho(rota: string) {
  return `tamanho_${rota}`;
}

export function urlLista(rota: string, pagina: number, busca = "") {
  const params = new URLSearchParams();
  if (busca) params.set("busca", busca);
  if (pagina > 0) params.set("pagina", String(pagina + 1));
  const query = params.toString();
  return query ? `/${rota}?${query}` : `/${rota}`;
}
