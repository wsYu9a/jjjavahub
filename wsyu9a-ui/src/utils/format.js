export function formatDateTime(dateStr) {
  if (!dateStr) return ''
  // 解析服务器返回的时间字符串，确保使用本地时区
  const date = new Date(dateStr.replace(' ', 'T') + '+08:00')
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  // 添加调试日志
  console.log('原始时间:', dateStr)
  console.log('解析后时间:', date.toLocaleString())
  console.log('当前时间:', now.toLocaleString())
  console.log('时间差(秒):', Math.floor(diff / 1000))
  
  // 转换为秒数
  const seconds = Math.floor(diff / 1000)

  console.log('秒数:', seconds)
  
  // 不同时间段的显示逻辑
  if (seconds < 60) {
    return '刚刚'
  } else if (seconds < 3600) {
    const minutes = Math.floor(seconds / 60)
    return `${minutes} 分钟前`
  } else if (seconds < 86400) {
    const hours = Math.floor(seconds / 3600)
    return `${hours} 小时前`
  } else if (seconds < 604800) { // 7天内
    return `${Math.floor(seconds / 86400)} 天前`
  } else if (seconds < 2592000) { // 30天内
    return `${Math.floor(seconds / 604800)} 周前`
  } else if (seconds < 31536000) { // 365天内
    return `${Math.floor(seconds / 2592000)} 个月前`
  } else if (seconds < 63072000) { // 2年内
    return '1 年前'
  } else {
    // 超过两年，显示具体日期
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  }
}

// 添加一个格式化时间的函数，用于显示当天的具体时间
export function formatTime(dateStr) {
  if (!dateStr) return ''
  // 解析服务器返回的时间字符串，确保使用本地时区
  const date = new Date(dateStr.replace(' ', 'T') + '+08:00')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${hours}:${minutes}`
} 