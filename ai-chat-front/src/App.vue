<template>
  <div :class="['app-container', theme]">
    <header class="app-header">
      <div class="header-left">
        <div class="logo">
          <svg viewBox="0 0 100 100" class="chatgpt-icon">
            <path d="M50 10C27.9 10 10 27.9 10 50s17.9 40 40 40 40-17.9 40-40S72.1 10 50 10zm0 70c-16.5 0-30-13.5-30-30s13.5-30 30-30 30 13.5 30 30-13.5 30-30 30z" fill="currentColor"/>
            <circle cx="50" cy="50" r="15" fill="currentColor"/>
          </svg>
        </div>
        <span class="app-title">AI 编程小助手</span>
      </div>
      <button class="theme-toggle" @click="toggleTheme" :title="theme === 'dark' ? '切换到白天模式' : '切换到黑夜模式'">
        <svg v-if="theme === 'dark'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="5"/>
          <path d="M12 1v2M12 21v2M4.22 4.22l1.42 1.42M18.36 18.36l1.42 1.42M1 12h2M21 12h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42"/>
        </svg>
        <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/>
        </svg>
      </button>
    </header>
    <ChatRoom :theme="theme" @edit-message="handleEditMessage" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import ChatRoom from './components/ChatRoom.vue'

const theme = ref(localStorage.getItem('theme') || 'dark')
const editMessage = ref(null)

const toggleTheme = () => {
  theme.value = theme.value === 'dark' ? 'light' : 'dark'
  localStorage.setItem('theme', theme.value)
}

const handleEditMessage = (message) => {
  editMessage.value = message
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

:root {
  --bg-primary: #171717;
  --bg-secondary: #2f2f2f;
  --bg-tertiary: #3e3e3e;
  --bg-hover: #1f1f1f;
  --text-primary: #ececec;
  --text-secondary: #c5c5c5;
  --text-muted: #8e8e8e;
  --border-color: #4e4e4e;
  --accent-green: #19c37d;
  --accent-purple: #5436da;
  --input-bg: #3e3e3e;
  --shadow-color: rgba(0, 0, 0, 0.3);
}

.light {
  --bg-primary: #ffffff;
  --bg-secondary: #f7f7f8;
  --bg-tertiary: #f0f0f0;
  --bg-hover: #f0f0f0;
  --text-primary: #343a40;
  --text-secondary: #495057;
  --text-muted: #6c757d;
  --border-color: #dee2e6;
  --accent-green: #10a37f;
  --accent-purple: #7859ea;
  --input-bg: #ffffff;
  --shadow-color: rgba(0, 0, 0, 0.1);
}

html, body {
  height: 100%;
  transition: background-color 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  background-color: var(--bg-primary);
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen',
    'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue',
    sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: var(--text-primary);
  transition: color 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

#app {
  height: 100vh;
}

.app-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: var(--bg-primary);
  transition: background-color 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 24px;
  border-bottom: 1px solid var(--border-color);
  background: var(--bg-primary);
  position: sticky;
  top: 0;
  z-index: 100;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  width: 36px;
  height: 36px;
  color: var(--accent-green);
  transition: color 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.chatgpt-icon {
  width: 100%;
  height: 100%;
}

.app-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  transition: color 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.theme-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background: var(--bg-tertiary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  cursor: pointer;
  color: var(--text-primary);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.theme-toggle:hover {
  background: var(--bg-hover);
  transform: rotate(15deg);
}

.theme-toggle svg {
  width: 20px;
  height: 20px;
  transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

@media (max-width: 768px) {
  .app-header {
    padding: 12px 16px;
  }
  
  .app-title {
    font-size: 16px;
  }
}
</style>