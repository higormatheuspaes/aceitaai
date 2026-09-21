import type { NextConfig } from "next";

// Hosts extras que podem abrir o dev server (ex: IP da maquina na rede local, para testar no celular).
const origensDeDesenvolvimento = (process.env.DEV_ALLOWED_ORIGINS ?? "")
  .split(",")
  .map((origem) => origem.trim())
  .filter(Boolean);

const nextConfig: NextConfig = {
  allowedDevOrigins: origensDeDesenvolvimento,
};

export default nextConfig;
