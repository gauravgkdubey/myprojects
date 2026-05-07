import ChatWindow from "@/components/ChatWindow";
import { ChatProvider } from "@/context/ChatContext";

export default function Home() {
  return (
    <main className="max-w-3xl mx-auto p-8">
      <h1 className="text-3xl font-bold mb-6">AI Enterprise Assistant</h1>
      <ChatProvider>
        <ChatWindow />
      </ChatProvider>
    </main>
  );
}
