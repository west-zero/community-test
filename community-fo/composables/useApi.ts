export const useApi = () => {
    const config = useRuntimeConfig()
    const baseURL = config.public.apiBase

    const fetchApi = async <T>(
        endpoint: string,
        options: RequestInit = {}
    ): Promise<{ success: boolean; message: string; data: T | null }> => {
        try {
            const response = await fetch(`${baseURL}${endpoint}`, {
                ...options,
                headers: {
                    'Content-Type': 'application/json',
                    ...options.headers
                },
                credentials: 'include'
            })
            return await response.json()
        } catch (error) {
            console.error('API Error:', error)
            return { success: false, message: 'Network error', data: null }
        }
    }

    return {
        get: <T>(endpoint: string) => fetchApi<T>(endpoint, { method: 'GET' }),
        post: <T>(endpoint: string, body: unknown) =>
            fetchApi<T>(endpoint, { method: 'POST', body: JSON.stringify(body) }),
        put: <T>(endpoint: string, body: unknown) =>
            fetchApi<T>(endpoint, { method: 'PUT', body: JSON.stringify(body) }),
        delete: <T>(endpoint: string) => fetchApi<T>(endpoint, { method: 'DELETE' })
    }
}
