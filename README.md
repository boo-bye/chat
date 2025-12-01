# Hybrid Chat App

本项目实现了一个 Hybrid 聊天系统，前端运行于 WebView，后端基于 WebSocket，实现多客户端实时消息通信，并支持 H5 与 Android 原生能力交互。

## 功能

- Web/H5 页面聊天界面（输入框、发送按钮、消息显示区）
- 支持用户 ID 发送消息，通过 WebSocket 广播给所有客户端
- 安卓 App 集成 WebView 加载本地页面，实现 Hybrid 运行
- 暴露原生能力给前端：
  - 获取设备信息
  - 可扩展麦克风、摄像头、通知推送等
- Node.js 搭建 WebSocket 服务端，实现消息接收与多端推送

## 技术栈

- 前端：HTML + CSS + JavaScript
- App：Android(WebView) + Kotlin/Java
- 通信：WebSocket（ws）
- 服务端：Node.js

## 设计思路

1. 前端页面构建聊天 UI，通过输入框发送文本，WebSocket 实时更新消息列表。
2. Android 集成 WebView 作为渲染层，加载本地 HTML，实现 Web 技术在 App 内运行。
3. 使用 `addJavascriptInterface` 注入原生方法供 JS 调用，实现原生能力扩展。
4. Node.js WebSocket 服务端负责广播消息，实现多客户端实时聊天。
