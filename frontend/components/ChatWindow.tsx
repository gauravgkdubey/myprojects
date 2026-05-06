"use client";
import { useState } from "react";
import { sendMessage } from "@/services/api";
export default function ChatWindow() {
const [prompt, setPrompt] = useState("");
const [response, setResponse] = useState("");
async function handleSend() {
const result = await sendMessage(prompt);
setResponse(result.response);
}
return (
<div className="p-4 space-y-4">
<textarea
className="border p-2 w-full"
value={prompt}
onChange={(e) => setPrompt(e.target.value)}
/>

<button
className="bg-blue-500 text-white px-4 py-2"
onClick={handleSend}
>
Send
</button>
<div className="border p-4 rounded">
{response}
</div>
</div>
);
}