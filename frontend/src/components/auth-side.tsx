export function AuthSide({ title, description }: { title: string; description: string }) {
  return (
    <div className="auth-side">
      <div className="auth-side-glow" />
      <div className="auth-side-glow-2" />

      <div className="brand">
        Aceita<span className="dot">.ai</span>
      </div>

      <div className="auth-side-content">
        <div className="pitch">
          <h2>{title}</h2>
          <p>{description}</p>

          <div className="auth-steps">
            <div className="auth-step">
              <span className="auth-step-num">1</span> Cadastre seu negócio
            </div>
            <div className="auth-step-sep" />
            <div className="auth-step">
              <span className="auth-step-num">2</span> Envie o link
            </div>
            <div className="auth-step-sep" />
            <div className="auth-step">
              <span className="auth-step-num">3</span> Receba o aceite
            </div>
          </div>
        </div>

        <div className="dash-preview">
          <div className="dash-preview-top-badge">
            <span className="pulse" />
            Novo orçamento enviado
          </div>
          <div className="dash-preview-titlebar">
            <span className="dash-preview-dot" />
            <span className="dash-preview-dot" />
            <span className="dash-preview-dot" />
            <span className="dash-preview-url">aceita.ai/dashboard</span>
          </div>
          <div className="dash-preview-body">
            <div className="dash-preview-sidebar">
              <span className="dash-preview-sidebar-item active" />
              <span className="dash-preview-sidebar-item" />
              <span className="dash-preview-sidebar-item" />
              <span className="dash-preview-sidebar-item" />
            </div>
            <div className="dash-preview-main">
              <div className="dash-preview-stats">
                <div className="dash-preview-stat">
                  <span className="dash-preview-stat-value">14</span>
                  <span className="dash-preview-stat-label">Enviados</span>
                </div>
                <div className="dash-preview-stat">
                  <span className="dash-preview-stat-value">9</span>
                  <span className="dash-preview-stat-label">Aceitos</span>
                </div>
                <div className="dash-preview-stat">
                  <span className="dash-preview-stat-value">64%</span>
                  <span className="dash-preview-stat-label">Taxa</span>
                </div>
              </div>
              <div className="dash-preview-rows">
                <div className="dash-preview-row">
                  <span className="dash-preview-row-bar" style={{ width: "68%" }} />
                  <span className="dash-preview-pill aceito">Aceito</span>
                </div>
                <div className="dash-preview-row">
                  <span className="dash-preview-row-bar" style={{ width: "52%" }} />
                  <span className="dash-preview-pill pendente">Pendente</span>
                </div>
                <div className="dash-preview-row">
                  <span className="dash-preview-row-bar" style={{ width: "78%" }} />
                  <span className="dash-preview-pill aceito">Aceito</span>
                </div>
              </div>
            </div>
          </div>

          <div className="dash-preview-floating-badge">
            <span>✓</span> Orçamento aceito · R$ 960,00
          </div>
        </div>
      </div>

      <div className="footnote">© Aceita.ai — um produto Himap Systems</div>
    </div>
  );
}

export function MobileTopBar({ title }: { title: string }) {
  return (
    <div className="mobile-topbar">
      <div className="brand">
        Aceita<span className="dot">.ai</span>
      </div>
      <h1>{title}</h1>
    </div>
  );
}
