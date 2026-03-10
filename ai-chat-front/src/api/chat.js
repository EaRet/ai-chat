import axios from 'axios'

const baseURL = '/api'

const chatApi = {
  chat(memoryId, message) {
    return new Promise((resolve, reject) => {
      const xhr = new XMLHttpRequest()
      xhr.open('GET', `${baseURL}/ai/chat?memoryId=${memoryId}&message=${encodeURIComponent(message)}`)
      xhr.setRequestHeader('Accept', 'text/event-stream')
      xhr.setRequestHeader('Cache-Control', 'no-cache')
      
      xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
          if (xhr.status === 200) {
            resolve(xhr.responseText)
          } else {
            reject(new Error(`Request failed with status ${xhr.status}`))
          }
        }
      }
      
      xhr.send()
    })
  },

  chatStream(memoryId, message, onMessage, onError, onComplete) {
    const eventSource = new EventSource(`${baseURL}/ai/chat?memoryId=${memoryId}&message=${encodeURIComponent(message)}`)
    
    eventSource.onmessage = (event) => {
      if (onMessage) {
        onMessage(event.data)
      }
    }
    
    eventSource.onerror = (error) => {
      if (onError) {
        onError(error)
      }
      eventSource.close()
    }
    
    eventSource.addEventListener('complete', () => {
      if (onComplete) {
        onComplete()
      }
      eventSource.close()
    })
    
    return eventSource
  }
}

export default chatApi
