import { ApiError, apiBaixar } from "@/lib/api";

function textoSimples(mensagem: string, status: number) {
  return new Response(mensagem, {
    status,
    headers: { "Content-Type": "text/plain; charset=utf-8", "Cache-Control": "no-store" },
  });
}

/**
 * Repassa o PDF do backend ao navegador. A URL da API e o token ficam so no servidor do Next:
 * o navegador so conhece o endereco deste proxy.
 */
export async function respostaDoComprovante(path: string, autenticado: boolean): Promise<Response> {
  try {
    const arquivo = await apiBaixar(path, autenticado);
    return new Response(arquivo.body, {
      headers: {
        "Content-Type": "application/pdf",
        "Content-Disposition": arquivo.headers.get("Content-Disposition") ?? 'attachment; filename="comprovante.pdf"',
        "Cache-Control": "no-store",
      },
    });
  } catch (error) {
    if (error instanceof ApiError) {
      if (error.status === 404 || error.status === 400) return textoSimples("Comprovante não encontrado.", 404);
      if (error.status === 409) return textoSimples(error.message, 409);
      if (error.status === 401) return textoSimples("Sua sessão expirou. Entre novamente.", 401);
    }
    return textoSimples("Não foi possível gerar o comprovante agora. Tente novamente em instantes.", 502);
  }
}
