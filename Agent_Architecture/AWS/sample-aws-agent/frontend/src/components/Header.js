import React from 'react';
import './Header.css';

function Header() {
  return (
    <header className="header">
      <div className="header-content">
        <h1>🤖 AWS AI Agent</h1>
        <p className="subtitle">Amazon Bedrock 기반 AI Agent 샘플</p>
      </div>
    </header>
  );
}

export default Header;
