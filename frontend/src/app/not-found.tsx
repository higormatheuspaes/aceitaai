import { NaoEncontrado } from "@/components/nao-encontrado";

export default function NotFound() {
  return (
    <div className="pagina-centralizada">
      <div className="marca">
        Aceita<span className="dot">.ai</span>
      </div>
      <NaoEncontrado />
    </div>
  );
}
