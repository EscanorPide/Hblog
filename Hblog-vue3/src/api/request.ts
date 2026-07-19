/** 后续可在此封装 fetch / axios */
const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api'

export async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...init?.headers,
    },
    ...init,
  })

  if (!res.ok) {
    throw new Error(`请求失败: ${res.status}`)
  }

  return res.json() as Promise<T>
}
