// 📌 引入 WebSocket 库
const WebSocket = require("ws");

// 📌 创建 WebSocket 服务器，监听端口 8080
const wss = new WebSocket.Server({ port: 8080 });

console.log("🚀 WebSocket 服务器已启动，端口: 8080");

// 📌 当有用户连接时触发
wss.on("connection", function (ws) {
    console.log("新用户已连接");

    // 接收消息
    ws.on("message", function (msg) {
        console.log("收到消息:", msg.toString());

        // ★ 广播给所有在线客户端（实现聊天室效果）
        wss.clients.forEach(client => {
            if (client.readyState === WebSocket.OPEN) {
                client.send(msg.toString());
            }
        });
    });

    ws.on("close", () => console.log("用户断开连接"));
});
