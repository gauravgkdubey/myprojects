"use client";

import { createContext, useContext, useEffect, useMemo, useReducer, type ReactNode } from "react";
import { sendMessage as sendMessageApi } from "@/services/api";

export type MessageRole = "user" | "assistant";

export interface Message {
  role: MessageRole;
  content: string;
  timestamp: string;
}

interface ChatState {
  messages: Message[];
  prompt: string;
  isLoading: boolean;
}

type ChatAction =
  | { type: "setPrompt"; payload: string }
  | { type: "setLoading"; payload: boolean }
  | { type: "addMessage"; payload: Message }
  | { type: "clearMessages" };

const initialState: ChatState = {
  messages: [],
  prompt: "",
  isLoading: false,
};

const ChatContext = createContext<ChatContextValue | undefined>(undefined);

interface ChatContextValue extends ChatState {
  setPrompt: (value: string) => void;
  handleSend: () => Promise<void>;
  clearChat: () => void;
}

function chatReducer(state: ChatState, action: ChatAction): ChatState {
  switch (action.type) {
    case "setPrompt":
      return { ...state, prompt: action.payload };
    case "setLoading":
      return { ...state, isLoading: action.payload };
    case "addMessage":
      return { ...state, messages: [...state.messages, action.payload] };
    case "clearMessages":
      return { ...state, messages: [] };
    default:
      return state;
  }
}

export function ChatProvider({ children }: { children: ReactNode }) {
  const [state, dispatch] = useReducer(chatReducer, initialState);

  useEffect(() => {
    const saved = localStorage.getItem("chatMessages");
    if (saved) {
      try {
        const parsed = JSON.parse(saved) as Message[];
        const messages = parsed.map((msg) => ({
          ...msg,
          timestamp: msg.timestamp,
        }));
        dispatch({ type: "clearMessages" });
        messages.forEach((message) => dispatch({ type: "addMessage", payload: message }));
      } catch {
        localStorage.removeItem("chatMessages");
      }
    }
  }, []);

  useEffect(() => {
    localStorage.setItem("chatMessages", JSON.stringify(state.messages));
  }, [state.messages]);

  async function handleSend() {
    const trimmed = state.prompt.trim();
    if (!trimmed || state.isLoading) return;

    const userMessage: Message = {
      role: "user",
      content: trimmed,
      timestamp: new Date().toISOString(),
    };

    dispatch({ type: "addMessage", payload: userMessage });
    dispatch({ type: "setPrompt", payload: "" });
    dispatch({ type: "setLoading", payload: true });

    try {
      const result = await sendMessageApi(trimmed);
      const assistantMessage: Message = {
        role: "assistant",
        content: result.response,
        timestamp: new Date().toISOString(),
      };
      dispatch({ type: "addMessage", payload: assistantMessage });
    } catch {
      const errorMessage: Message = {
        role: "assistant",
        content: "Error: Failed to get response from AI.",
        timestamp: new Date().toISOString(),
      };
      dispatch({ type: "addMessage", payload: errorMessage });
    } finally {
      dispatch({ type: "setLoading", payload: false });
    }
  }

  function clearChat() {
    dispatch({ type: "clearMessages" });
  }

  const value = useMemo(
    () => ({
      ...state,
      setPrompt: (value: string) => dispatch({ type: "setPrompt", payload: value }),
      handleSend,
      clearChat,
    }),
    [state]
  );

  return <ChatContext.Provider value={value}>{children}</ChatContext.Provider>;
}

export function useChat() {
  const context = useContext(ChatContext);
  if (!context) {
    throw new Error("useChat must be used within a ChatProvider");
  }
  return context;
}
