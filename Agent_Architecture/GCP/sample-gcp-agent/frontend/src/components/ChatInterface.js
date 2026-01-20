import React, { useState, useRef, useEffect } from 'react';
import axios from 'axios';
import './ChatInterface.css';

function ChatInterface() {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [sessionId, setSessionId] = useState(null);
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const sendMessage = async () => {
    if (!input.trim() || loading) return;

    const userMessage = input.trim();
    setInput('');
    setLoading(true);

    const newUserMessage = {
      type: 'user',
      content: userMessage,
      timestamp: new Date().toLocaleTimeString()
    };
    setMessages(prev => [...prev, newUserMessage]);

    try {
      const response = await axios.post('http://localhost:8082/api/agent/chat', {
        message: userMessage,
        sessionId: sessionId
      });

      if (response.data.sessionId && !sessionId) {
        setSessionId(response.data.sessionId);
      }

      const agentMessage = {
        type: 'agent',
        content: response.data.response,
        citations: response.data.citations || [],
        toolUsed: response.data.toolUsed,
        metadata: response.data.metadata,
        timestamp: new Date().toLocaleTimeString()
      };
      setMessages(prev => [...prev, agentMessage]);
    } catch (error) {
      console.error('Error sending message:', error);
      const errorMessage = {
        type: 'error',
        content: '죄송합니다. 오류가 발생했습니다. GCP 백엔드 서버가 실행 중인지 확인해주세요.',
        timestamp: new Date().toLocaleTimeString()
      };
      setMessages(prev => [...prev, errorMessage]);
    } finally {
      setLoading(false);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  };

  const clearChat = () => {
    setMessages([]);
    setSessionId(null);
  };

  return (
    <div className="chat-container">
      <div className="chat-messages">
        {messages.length === 0 && (
          <div className="welcome-message">
            <h2>안녕하세요! 👋</h2>
            <p>GCP AI Agent에 오신 것을 환영합니다.</p>
            <div className="example-queries">
              <p>다음과 같은 질문을 해보세요:</p>
              <ul>
                <li>"5 + 3 계산해줘"</li>
                <li>"현재 날씨 알려줘"</li>
                <li>"지금 몇 시야?"</li>
                <li>"Vertex AI에 대해 알려줘"</li>
                <li>"Agent Engine이 뭐야?"</li>
              </ul>
            </div>
          </div>
        )}

        {messages.map((msg, index) => (
          <div key={index} className={`message ${msg.type}`}>
            <div className="message-content">
              <div className="message-header">
                <span className="message-type">
                  {msg.type === 'user'
                    ? '👤 사용자'
                    : msg.type === 'error'
                    ? '❌ 오류'
                    : '🤖 에이전트'}
                </span>
                <span className="message-time">{msg.timestamp}</span>
              </div>
              <div className="message-text">{msg.content}</div>

              {msg.type === 'agent' && msg.toolUsed && (
                <div className="tool-info">
                  🔧 사용된 도구: <strong>{msg.toolUsed}</strong>
                </div>
              )}

              {msg.type === 'agent' && msg.citations && msg.citations.length > 0 && (
                <div className="citations">
                  <strong>📚 참고 출처:</strong>
                  <ul>
                    {msg.citations.map((citation, idx) => (
                      <li key={idx}>{citation}</li>
                    ))}
                  </ul>
                </div>
              )}
            </div>
          </div>
        ))}

        {loading && (
          <div className="message agent">
            <div className="message-content">
              <div className="loading-indicator">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </div>
        )}

        <div ref={messagesEndRef} />
      </div>

      <div className="chat-input-container">
        <div className="chat-input-wrapper">
          <textarea
            className="chat-input"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder="메시지를 입력하세요... (Enter로 전송)"
            rows={1}
            disabled={loading}
          />
          <button
            className="send-button"
            onClick={sendMessage}
            disabled={loading || !input.trim()}
          >
            {loading ? '전송 중...' : '전송'}
          </button>
          <button
            className="clear-button"
            onClick={clearChat}
            disabled={loading}
          >
            초기화
          </button>
        </div>
      </div>
    </div>
  );
}

export default ChatInterface;

