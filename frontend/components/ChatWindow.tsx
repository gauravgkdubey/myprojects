'use client';
import { useEffect, useRef } from 'react';
import { useChat } from '@/context/ChatContext';
import MessageList from '@/components/MessageList';
import ChatInput from '@/components/ChatInput';

export default function ChatWindow() {
  const { messages, prompt, isLoading, setPrompt, handleSend, clearChat } = useChat();
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  return (
    <div className="flex flex-col h-screen max-w-4xl mx-auto p-4">
      {/* Header */}
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-2xl font-bold">AI Chat Assistant</h1>
        <button
          onClick={clearChat}
          className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"
        >
          Clear Chat
        </button>
      </div>

      {/* Messages */}
      <MessageList messages={messages} isLoading={isLoading} />
      <div ref={messagesEndRef} />

      {/* Input */}
      <ChatInput
        prompt={prompt}
        setPrompt={setPrompt}
        handleSend={handleSend}
        isLoading={isLoading}
      />
    </div>
  );
}
