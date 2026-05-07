interface ChatInputProps {
  prompt: string;
  setPrompt: (value: string) => void;
  handleSend: () => Promise<void>;
  isLoading: boolean;
}

export default function ChatInput({ prompt, setPrompt, handleSend, isLoading }: ChatInputProps) {
  return (
    <div className="flex space-x-2 mt-4">
      <textarea
        className="flex-1 border p-2 rounded resize-none"
        rows={3}
        value={prompt}
        onChange={(e) => setPrompt(e.target.value)}
        onKeyDown={(e) => {
          if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleSend();
          }
        }}
        placeholder="Type your message..."
        disabled={isLoading}
      />
      <button
        onClick={handleSend}
        disabled={isLoading || !prompt.trim()}
        className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 disabled:opacity-50"
      >
        {isLoading ? 'Sending...' : 'Send'}
      </button>
    </div>
  );
}
