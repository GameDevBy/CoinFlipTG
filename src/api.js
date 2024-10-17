const host = process.env.REACT_APP_SERVER_HOST //"http://localhost:8080"
export const ssePoint = `${host}/api/sse/games`
export const fetchUserData = async (user, setInitUser, setScore) => {
    try {
        const response = await fetch(`${host}/api/users`, {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(user),
        });
        const userData = await response.json();
        setInitUser(userData)
        setScore(userData.score)
    } catch (error) {
        console.error("Error fetching user data:", error);
    }
};

export const fetchGames = async () => {
    try {
        const response = await fetch(`${host}/api/games`);
        return await response.json();
    } catch (error) {
        console.error("Error fetching games:", error);
    }
};

export const fetchHistory = async (userId) => {
    try {
        const response = await fetch(`${host}/api/games/${userId}/history`);
        return await response.json();
    } catch (error) {
        console.error("Error fetching games:", error);
    }
};

export const fetchRating = async () => {
    try {
        const response = await fetch(`${host}/api/users/rating`);
        return await response.json();
    } catch (error) {
        console.error("Error fetching games:", error);
    }
};

export const createGame = async (requestData) => {
    try {
        const response = await fetch(`${host}/api/games`, {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(requestData),
        });
        return await response.json()
    } catch (error) {
        console.error("Error creating game:", error);
    }
};

export const deleteGame = async (gameId) => {
    try {
        const response = await fetch(`${host}/api/games/${gameId}`, {
            method: "DELETE",
        });
        return response.ok
    } catch (error) {
        console.error("Error deleting game:", error);
    }
};

export const joinGame = async (userId, gameId) => {
    try {
        const response = await fetch(`${host}/api/games/${userId}/${gameId}/join`, {
            method: "PUT",
        });
        return await response.json()
    } catch (error) {
        console.error("Error join game:", error);
    }
};

export const flipCoin = async (gameId) => {
    try {
        const response = await fetch(`${host}/api/games/${gameId}/flip`, {
            method: "PUT",
        });
        return await response.json()
    } catch (error) {
        console.error("Error join game:", error);
    }
};

export const cancelGame = async (gameId) => {
    try {
        const response = await fetch(`${host}/api/games/${gameId}/cancel`, {
            method: "PUT",
        });
        return await response.json()
    } catch (error) {
        console.error("Error cancel game:", error);
    }
};

export const botGame = async (userId, requestData) => {
    try {
        const response = await fetch(`${host}/api/games/bot/${userId}`, {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(requestData),
        });
        return await response.json()
    } catch (error) {
        console.error("Error creating game:", error);
    }
};