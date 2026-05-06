export async function sendMessage(prompt: string) {
const response = await fetch("http://localhost:8080/api/chat", {
method: "POST",
headers: {
"Content-Type": "application/json",
},
body: JSON.stringify({ prompt }),
});
return response.json();
}