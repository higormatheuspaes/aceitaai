"use client";

import Link from "next/link";
import { useActionState, useState } from "react";
import { cadastrar } from "@/app/actions/auth";
import { AuthSide, MobileTopBar } from "@/components/auth-side";

type ViaCepResponse = {
  erro?: boolean;
  logradouro?: string;
  bairro?: string;
  localidade?: string;
  uf?: string;
};

export default function CadastroPage() {
  const [state, formAction, pending] = useActionState(cadastrar, undefined);

  const [cep, setCep] = useState("");
  const [logradouro, setLogradouro] = useState("");
  const [numero, setNumero] = useState("");
  const [complemento, setComplemento] = useState("");
  const [bairro, setBairro] = useState("");
  const [cidade, setCidade] = useState("");
  const [estado, setEstado] = useState("");
  const [buscandoCep, setBuscandoCep] = useState(false);
  const [erroCep, setErroCep] = useState<string | null>(null);

  async function handleCepBlur(event: React.FocusEvent<HTMLInputElement>) {
    const digits = event.target.value.replace(/\D/g, "");
    setErroCep(null);

    if (digits.length !== 8) {
      return;
    }

    setBuscandoCep(true);
    try {
      const response = await fetch(`https://viacep.com.br/ws/${digits}/json/`);
      const data: ViaCepResponse = await response.json();

      if (data.erro) {
        setErroCep("CEP não encontrado.");
        return;
      }

      setLogradouro(data.logradouro ?? "");
      setBairro(data.bairro ?? "");
      setCidade(data.localidade ?? "");
      setEstado(data.uf ?? "");
    } catch {
      setErroCep("Não foi possível buscar o CEP agora. Preencha manualmente.");
    } finally {
      setBuscandoCep(false);
    }
  }

  return (
    <div className="auth-wrap">
      <AuthSide
        title="Leva 3 minutos. Só pedimos o que precisamos pra validar seu negócio."
        description="Nome, documento e endereço servem pra deixar seu orçamento com validade real perante o cliente — e pra evitar conta fraudulenta na plataforma."
      />
      <div className="auth-form-side">
        <MobileTopBar title="Criar conta" />
        <form className="auth-form" action={formAction}>
          <h1>Criar conta</h1>
          <div className="sub">Dados do seu negócio</div>

          {state?.erro && <div className="form-error">{state.erro}</div>}

          <div className="field">
            <label htmlFor="nomeDono">Seu nome</label>
            <input id="nomeDono" name="nomeDono" type="text" required />
          </div>
          <div className="field">
            <label htmlFor="nomeNegocio">Nome do negócio</label>
            <input
              id="nomeNegocio"
              name="nomeNegocio"
              type="text"
              required
              placeholder="Como aparece pro seu cliente"
            />
          </div>
          <div className="field-row">
            <div className="field">
              <label htmlFor="documento">CPF ou CNPJ</label>
              <input id="documento" name="documento" type="text" required />
            </div>
            <div className="field">
              <label htmlFor="whatsapp">WhatsApp</label>
              <input id="whatsapp" name="whatsapp" type="text" required placeholder="(47) 99999-0000" />
            </div>
          </div>

          <div className="field">
            <label htmlFor="cep">CEP</label>
            <input
              id="cep"
              name="cep"
              type="text"
              placeholder="89010-000"
              value={cep}
              onChange={(e) => setCep(e.target.value)}
              onBlur={handleCepBlur}
              maxLength={9}
            />
            {buscandoCep && <div className="hint">Buscando endereço...</div>}
            {erroCep && <div className="hint cep-error">{erroCep}</div>}
          </div>

          <div className="field-row">
            <div className="field" style={{ flex: 2 }}>
              <label htmlFor="logradouro">Rua</label>
              <input
                id="logradouro"
                name="logradouro"
                type="text"
                value={logradouro}
                onChange={(e) => setLogradouro(e.target.value)}
              />
            </div>
            <div className="field" style={{ flex: 1 }}>
              <label htmlFor="numero">Número</label>
              <input
                id="numero"
                name="numero"
                type="text"
                value={numero}
                onChange={(e) => setNumero(e.target.value)}
              />
            </div>
          </div>

          <div className="field-row">
            <div className="field" style={{ flex: 1 }}>
              <label htmlFor="complemento">Complemento</label>
              <input
                id="complemento"
                name="complemento"
                type="text"
                placeholder="Opcional"
                value={complemento}
                onChange={(e) => setComplemento(e.target.value)}
              />
            </div>
            <div className="field" style={{ flex: 1 }}>
              <label htmlFor="bairro">Bairro</label>
              <input
                id="bairro"
                name="bairro"
                type="text"
                value={bairro}
                onChange={(e) => setBairro(e.target.value)}
              />
            </div>
          </div>

          <div className="field-row">
            <div className="field" style={{ flex: 2 }}>
              <label htmlFor="cidade">Cidade</label>
              <input
                id="cidade"
                name="cidade"
                type="text"
                value={cidade}
                onChange={(e) => setCidade(e.target.value)}
              />
            </div>
            <div className="field" style={{ flex: 1 }}>
              <label htmlFor="estado">UF</label>
              <input
                id="estado"
                name="estado"
                type="text"
                maxLength={2}
                value={estado}
                onChange={(e) => setEstado(e.target.value.toUpperCase())}
              />
            </div>
          </div>

          <div className="field">
            <label htmlFor="email">E-mail</label>
            <input id="email" name="email" type="email" required />
          </div>
          <div className="field">
            <label htmlFor="senha">Senha</label>
            <input id="senha" name="senha" type="password" required minLength={6} />
          </div>
          <button className="btn btn-primary btn-block" type="submit" disabled={pending}>
            {pending ? "Criando conta..." : "Continuar"}
          </button>
          <div className="auth-alt-link">
            Já tem conta? <Link href="/login">Entrar</Link>
          </div>
        </form>
      </div>
    </div>
  );
}
