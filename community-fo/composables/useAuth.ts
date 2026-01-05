interface User {
    id: number
    username: string
    email: string
    nickname: string
    role: string
}

export const useAuth = () => {
    const user = useState<User | null>('user', () => null)
    const api = useApi()

    const checkAuth = async () => {
        const result = await api.get<User>('/api/auth/me')
        if (result.success && result.data) {
            user.value = result.data
        } else {
            user.value = null
        }
    }

    const login = async (username: string, password: string) => {
        const result = await api.post<User>('/api/auth/login', { username, password })
        if (result.success && result.data) {
            user.value = result.data
        }
        return result
    }

    const signup = async (username: string, password: string, email: string, nickname: string) => {
        const result = await api.post<User>('/api/auth/signup', {
            username,
            password,
            email,
            nickname
        })
        return result
    }

    const logout = async () => {
        await api.post('/api/auth/logout', {})
        user.value = null
        navigateTo('/')
    }

    return {
        user,
        checkAuth,
        login,
        signup,
        logout,
        isLoggedIn: computed(() => user.value !== null)
    }
}
