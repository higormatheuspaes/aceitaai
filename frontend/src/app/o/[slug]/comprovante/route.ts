import { respostaDoComprovante } from "@/lib/comprovante";

export async function GET(_request: Request, { params }: { params: Promise<{ slug: string }> }) {
  const { slug } = await params;
  return respostaDoComprovante(`/publico/orcamentos/${encodeURIComponent(slug)}/comprovante`, false);
}
