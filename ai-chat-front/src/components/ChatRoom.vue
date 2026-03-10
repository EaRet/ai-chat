<template>
  <div class="chat-container">
    <div class="chat-header">
      <h1>AI 编程小助手</h1>
      <span class="session-id">会话ID: {{ memoryId }}</span>
    </div>
    
    <div class="chat-messages" ref="messagesContainer">
      <div v-if="messages.length === 0" class="empty-hint">
        <p>你好！我是AI编程小助手，可以帮助你解答编程学习和求职面试相关的问题。</p>
        <p>有什么我可以帮助你的吗？</p>
      </div>
      
      <div
        v-for="(msg, index) in messages"
        :key="index"
        :class="['message', msg.role]"
      >
        <div class="avatar">
          {{ msg.role === 'user' ? '👤' : '🤖' }}
        </div>
        <div class="content">
          <div class="bubble">{{ msg.content }}</div>
        </div>
      </div>
      
      <div v-if="isReceiving" class="message ai">
        <div class="avatar">🤖</div>
        <div class="content">
          <div class="bubble typing">
            <span class="dot"></span>
            <span class="dot"></span>
            <span class="dot"></span>
          </div>
        </div>
      </div>
    </div>
    
    <div class="chat-input">
      <input
        v-model="inputMessage"
        @keyup.enter="sendMessage"
        placeholder="请输入你的问题..."
        :disabled="isReceiving"
      />
      <button @click="sendMessage" :disabled="isReceiving || !inputMessage.trim()">
        发送
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import chatApi from '../api/chat.js'

const memoryId = ref(0)
const messages = ref([])
const inputMessage = ref('')
const isReceiving = ref(false)
const messagesContainer = ref(null)

onMounted(() => {
  memoryId.value = Math.floor(Math.random() * 100000)
})

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

const sendMessage = () => {
  const message = inputMessage.value.trim()
  if (!message || isReceiving.value) return
  
  messages.value.push({
    role: 'user',
    content: message
  })
  
  const userMessage = message
  inputMessage.value = ''
  isReceiving.value = true
  scrollToBottom()
  
  let aiResponse = ''
  const eventSource = chatApi.chatStream(
    memoryId.value,
    userMessage + '（请使用Windows支持的命令来完成任务）',
    (data) => {
      aiResponse += data
      const lastMsg = messages.value[messages.value.length - 1]
      if (lastMsg && lastMsg.role === 'ai') {
        lastMsg.content = aiResponse
      } else {
        messages.value.push({
          role: 'ai',
          content: aiResponse
        })
      }
      scrollToBottom()
    },
    (error) => {
      console.error('SSE error:', error)
      isReceiving.value = false
    },
    () => {
      isReceiving.value = false
    }
  )
}
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-width: 800px;
  margin: 0 auto;
  background-color: #f5f5f5;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background-color: #fff;
  border-bottom: 1px solid #e0e0e0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.chat-header h1 {
  font-size: 20px;
  margin: 0;
  color: #333;
}

.session-id {
  font-size: 12px;
  color: #999;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.empty-hint {
  text-align: center;
  color: #999;
  padding: 40px 20px;
}

.empty-hint p {
  margin: 10px 0;
}

.message {
  display: flex;
  margin-bottom: 20px;
  align-items: flex-start;
}

.message.user {
  flex-direction: row-reverse;
}

.message.ai {
  flex-direction: row;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  background-color: #e0e0e0;
  flex-shrink: 0;
}

.message.user .avatar {
  background-color: #4CAF50;
}

.message.ai .avatar {
  background-color: #2196F3;
}

.content {
  max-width: 70%;
  margin: 0 12px;
}

.message.user .content {
  display: flex;
  justify-content: flex-end;
}

.bubble {
  padding: 12px 16px;
  border-radius: 12px;
  word-wrap: break-word;
  white-space: pre-wrap;
  line-height: 1.5;
}

.message.user .bubble {
  background-color: #4CAF50;
  color: #fff;
  border-bottom-right-radius: 4px;
}

.message.ai .bubble {
  background-color: #fff;
  color: #333;
  border-bottom-left-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.typing {
  display: flex;
  align-items: center;
  gap: 4px;
}

.typing .dot {
  width: 8px;
  height: 8px;
  background-color: #999;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out;
}

.typing .dot:nth-child(1) {
  animation-delay: 0s;
}

.typing .dot:nth-child(2) {
  animation-delay: 0.2s;
}

.typing .dot:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0.6);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.chat-input {
  display: flex;
  padding: 16px 20px;
  background-color: #fff;
  border-top: 1px solid #e0e0e0;
}

.chat-input input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 24px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s;
}

.chat-input input:focus {
  border-color: #4CAF50;
}

.chat-input input:disabled {
  background-color: #f5f5f5;
}

.chat-input button {
  margin-left: 12px;
  padding: 12px 24px;
  background-color: #4CAF50;
  color: #fff;
  border: none;
  border-radius: 24px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.chat-input button:hover:not(:disabled) {
  background-color: #45a049;
}

.chat-input button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>
