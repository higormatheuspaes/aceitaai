export const COOKIE_TAMANHO = "tamanho_clientes";

export function urlClientes(pagina: number, busca: string) {
  const params = new URLSearchParams();
  if (busca) params.set("busca", busca);
  if (pagina > 0) params.set("pagina", String(pagina + 1));
  const query = params.toString();
  return query ? `/clientes?${query}` : "/clientes";
}
