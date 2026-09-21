import { respostaDoComprovante } from "@/lib/comprovante";

export async function GET(_request: Request, { params }: { params: Promise<{ id: string }> }) {
  const { id } = await params;
  return respostaDoComprovante(`/orcamentos/${encodeURIComponent(id)}/comprovante`, true);
}
