<template>
  <div class="chat-container" :class="theme">
    <div class="chat-messages" ref="messagesContainer">
      <div v-if="messages.length === 0" class="empty-hint">
        <div class="logo">
          <svg viewBox="0 0 100 100" class="chatgpt-logo">
            <path d="M50 10C27.9 10 10 27.9 10 50s17.9 40 40 40 40-17.9 40-40S72.1 10 50 10zm0 70c-16.5 0-30-13.5-30-30s13.5-30 30-30 30 13.5 30 30-13.5 30-30 30z" fill="currentColor"/>
            <circle cx="50" cy="50" r="15" fill="currentColor"/>
          </svg>
        </div>
        <h2>我可以帮助你</h2>
        <p>你可以问我任何问题，比如：</p>
        <div class="suggestions">
          <button class="suggestion-btn" @click="fillSuggestion('如何学习编程？')">如何学习编程？</button>
          <button class="suggestion-btn" @click="fillSuggestion('帮我解释一段代码')">帮我解释一段代码</button>
          <button class="suggestion-btn" @click="fillSuggestion('如何准备面试？')">如何准备面试？</button>
        </div>
      </div>
      
      <div
        v-for="(msg, index) in messages"
        :key="index"
        :class="['message', msg.role]"
      >
        <div class="avatar">
          <svg v-if="msg.role === 'user'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="8" r="4"/>
            <path d="M4 20c0-4 4-6 8-6s8 2 8 6"/>
          </svg>
          <svg v-else viewBox="0 0 100 100" class="chatgpt-icon">
            <path d="M50 10C27.9 10 10 27.9 10 50s17.9 40 40 40 40-17.9 40-40S72.1 10 50 10zm0 70c-16.5 0-30-13.5-30-30s13.5-30 30-30 30 13.5 30 30-13.5 30-30 30z" fill="currentColor"/>
            <circle cx="50" cy="50" r="15" fill="currentColor"/>
          </svg>
        </div>
        <div class="content">
          <div class="bubble" v-html="formatMessage(msg.content)"></div>
          <div class="message-actions">
            <button class="action-btn" @click="copyMessage(msg.content)" title="复制">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="9" y="9" width="13" height="13" rx="2" ry="2"/>
                <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/>
              </svg>
              <span>复制</span>
            </button>
            <button v-if="msg.role === 'user'" class="action-btn" @click="editMessage(msg, index)" title="重新编辑">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
              </svg>
              <span>重新编辑</span>
            </button>
          </div>
        </div>
      </div>
      
      <div v-if="isReceiving" class="message ai">
        <div class="avatar">
          <svg viewBox="0 0 100 100" class="chatgpt-icon">
            <path d="M50 10C27.9 10 10 27.9 10 50s17.9 40 40 40 40-17.9 40-40S72.1 10 50 10zm0 70c-16.5 0-30-13.5-30-30s13.5-30 30-30 30 13.5 30 30-13.5 30-30 30z" fill="currentColor"/>
            <circle cx="50" cy="50" r="15" fill="currentColor"/>
          </svg>
        </div>
        <div class="content">
          <div class="bubble typing">
            <span class="dot"></span>
            <span class="dot"></span>
            <span class="dot"></span>
          </div>
        </div>
      </div>
    </div>
    
    <div v-if="showToast" class="toast" :class="toastType">{{ toastMessage }}</div>
    
    <div class="chat-input-wrapper">
      <div class="chat-input">
        <textarea
          v-model="inputMessage"
          @keydown.enter.exact.prevent="sendMessage"
          placeholder="发送消息..."
          :disabled="isReceiving"
          rows="1"
          ref="inputRef"
        ></textarea>
        <button 
          @click="sendMessage" 
          :disabled="isReceiving || !inputMessage.trim()"
          class="send-btn"
        >
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z"/>
          </svg>
        </button>
      </div>
      <p class="disclaimer">AI 可能会产生错误信息，请核实重要内容。</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import chatApi from '../api/chat.js'

const props = defineProps({
  theme: {
    type: String,
    default: 'dark'
  }
})

const emit = defineEmits(['edit-message'])

const memoryId = ref(0)
const messages = ref([])
const inputMessage = ref('')
const isReceiving = ref(false)
const messagesContainer = ref(null)
const inputRef = ref(null)
const showToast = ref(false)
const toastMessage = ref('')
const toastType = ref('success')

onMounted(() => {
  memoryId.value = Math.floor(Math.random() * 100000)
})

const fillSuggestion = (text) => {
  inputMessage.value = text
  inputRef.value?.focus()
}

const showToastMessage = (message, type = 'success') => {
  toastMessage.value = message
  toastType.value = type
  showToast.value = true
  setTimeout(() => {
    showToast.value = false
  }, 2000)
}

const copyMessage = async (content) => {
  try {
    await navigator.clipboard.writeText(content)
    showToastMessage('已复制到剪贴板')
  } catch (err) {
    showToastMessage('复制失败', 'error')
  }
}

const editMessage = (msg, index) => {
  inputMessage.value = msg.content
  messages.value.splice(index, 1)
  inputRef.value?.focus()
}

const formatMessage = (content) => {
  if (!content) return ''
  let formatted = content
    .replace(/```(\w+)?\n([\s\S]*?)```/g, '<pre><code class="language-$1">$2</code></pre>')
    .replace(/`([^`]+)`/g, '<code>$1</code>')
    .replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
    .replace(/\n/g, '<br>')
  return formatted
}

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
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 24px 0 140px;
  background-color: var(--bg-primary);
  transition: background-color 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.chat-messages::-webkit-scrollbar {
  width: 8px;
}

.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: var(--border-color);
  border-radius: 4px;
  transition: background 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: var(--text-muted);
}

.empty-hint {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60%;
  padding: 40px 20px;
  text-align: center;
}

.logo {
  width: 80px;
  height: 80px;
  margin-bottom: 24px;
  color: var(--accent-green);
  transition: color 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.chatgpt-logo {
  width: 100%;
  height: 100%;
}

.empty-hint h2 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 12px;
  color: var(--text-primary);
  transition: color 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.empty-hint p {
  color: var(--text-muted);
  margin-bottom: 16px;
  transition: color 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.suggestions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
  max-width: 400px;
}

.suggestion-btn {
  padding: 10px 16px;
  background: var(--bg-tertiary);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  color: var(--text-secondary);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.suggestion-btn:hover {
  background: var(--bg-hover);
  border-color: var(--accent-green);
  color: var(--text-primary);
  transform: translateY(-2px);
}

.message {
  display: flex;
  gap: 12px;
  padding: 24px 24px;
  max-width: 768px;
  margin: 0 auto;
  width: 100%;
  transition: background-color 0.3s ease;
}

.message:hover {
  background: var(--bg-hover);
}

.message:hover .message-actions {
  opacity: 1;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  overflow: hidden;
}

.avatar svg {
  width: 24px;
  height: 24px;
}

.message.user .avatar {
  background: var(--accent-purple);
  color: white;
  transition: background 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.message.ai .avatar {
  background: var(--accent-green);
  color: white;
  transition: background 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.chatgpt-icon {
  width: 28px;
  height: 28px;
}

.content {
  flex: 1;
  min-width: 0;
}

.bubble {
  line-height: 1.6;
  font-size: 16px;
  white-space: pre-wrap;
  word-wrap: break-word;
  color: var(--text-primary);
  transition: color 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.bubble :deep(pre) {
  background: var(--bg-secondary);
  padding: 16px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 12px 0;
  border: 1px solid var(--border-color);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.bubble :deep(code) {
  font-family: 'Fira Code', 'JetBrains Mono', 'Consolas', monospace;
  font-size: 14px;
  background: var(--bg-tertiary);
  padding: 2px 6px;
  border-radius: 4px;
  color: var(--text-secondary);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.bubble :deep(pre code) {
  background: transparent;
  padding: 0;
  color: var(--text-primary);
}

.bubble :deep(strong) {
  font-weight: 600;
  color: var(--text-primary);
}

.message-actions {
  display: flex;
  gap: 8px;
  margin-top: 8px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  background: var(--bg-tertiary);
  border: 1px solid var(--border-color);
  border-radius: 6px;
  color: var(--text-muted);
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-btn svg {
  width: 14px;
  height: 14px;
}

.action-btn:hover {
  background: var(--bg-hover);
  color: var(--text-primary);
  border-color: var(--accent-green);
}

.typing {
  display: flex;
  align-items: center;
  gap: 4px;
  min-height: 24px;
}

.typing .dot {
  width: 8px;
  height: 8px;
  background: var(--text-muted);
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out;
  transition: background 0.4s cubic-bezier(0.4, 0, 0.2, 1);
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

.chat-input-wrapper {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px 24px 24px;
  background: linear-gradient(to top, var(--bg-primary) 60%, transparent);
  z-index: 10;
  transition: background 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.chat-input {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  max-width: 768px;
  margin: 0 auto;
  background: var(--input-bg);
  border-radius: 16px;
  padding: 12px 16px;
  border: 1px solid var(--border-color);
  box-shadow: 0 4px 12px var(--shadow-color);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.chat-input:focus-within {
  border-color: var(--accent-green);
  box-shadow: 0 4px 16px var(--shadow-color), 0 0 0 2px rgba(25, 195, 125, 0.1);
  transform: translateY(-2px);
}

.chat-input textarea {
  flex: 1;
  background: transparent;
  border: none;
  color: var(--text-primary);
  font-size: 16px;
  font-family: inherit;
  resize: none;
  outline: none;
  min-height: 24px;
  max-height: 200px;
  line-height: 1.5;
  transition: color 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.chat-input textarea::placeholder {
  color: var(--text-muted);
  transition: color 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.chat-input textarea:disabled {
  opacity: 0.6;
}

.send-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: var(--accent-green);
  border: none;
  border-radius: 8px;
  color: #fff;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  flex-shrink: 0;
}

.send-btn svg {
  width: 18px;
  height: 18px;
}

.send-btn:hover:not(:disabled) {
  background: #18b06e;
  transform: scale(1.05);
}

.send-btn:disabled {
  background: var(--border-color);
  opacity: 0.5;
  cursor: not-allowed;
}

.disclaimer {
  text-align: center;
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 8px;
  transition: color 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.toast {
  position: fixed;
  top: 80px;
  left: 50%;
  transform: translateX(-50%);
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 14px;
  z-index: 1000;
  animation: slideDown 0.3s ease;
}

.toast.success {
  background: var(--accent-green);
  color: white;
}

.toast.error {
  background: #ef4444;
  color: white;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
}

@media (max-width: 768px) {
  .message {
    padding: 16px 16px;
    max-width: 100%;
  }
  
  .chat-input-wrapper {
    padding: 12px 12px 16px;
  }
  
  .empty-hint {
    padding: 24px 16px;
  }
  
  .suggestions {
    max-width: 100%;
  }
  
  .suggestion-btn {
    font-size: 13px;
    padding: 8px 12px;
  }
  
  .message-actions {
    opacity: 1;
  }
}
</style>