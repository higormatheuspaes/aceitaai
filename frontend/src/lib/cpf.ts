export function somenteDigitos(texto: string) {
  return texto.replace(/\D/g, "");
}

export function formatarCpf(texto: string) {
  const d = somenteDigitos(texto).slice(0, 11);
  let resultado = d.slice(0, 3);
  if (d.length > 3) resultado += `.${d.slice(3, 6)}`;
  if (d.length > 6) resultado += `.${d.slice(6, 9)}`;
  if (d.length > 9) resultado += `-${d.slice(9, 11)}`;
  return resultado;
}

function digitoVerificador(d: string, quantidade: number) {
  let soma = 0;
  for (let i = 0; i < quantidade; i++) {
    soma += Number(d[i]) * (quantidade + 1 - i);
  }
  const resto = (soma * 10) % 11;
  return resto === 10 ? 0 : resto;
}

export function cpfValido(texto: string) {
  const d = somenteDigitos(texto);
  if (d.length !== 11 || /^(\d)\1{10}$/.test(d)) return false;
  return digitoVerificador(d, 9) === Number(d[9]) && digitoVerificador(d, 10) === Number(d[10]);
}
