const host = process.env.REACT_APP_SERVER_HOST
export const fetchUserData = async (user,setInitUser, setScore) => {
    try {
        const response = await fetch(`${host}/api/users/${user.telegramId}/${user.username}`);
        const userData = await response.json();
        setInitUser(userData)
        setScore(userData.score);
    } catch (error) {
        console.error("Error fetching user data:", error);
    }
};

export const fetchGames = async (setGames) => {
    try {
        const response = await fetch(`${host}/api/games`);
        const gamesData = await response.json();
        setGames(gamesData);
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
        console.error("Error creating game:", error);
    }
};

export const joinGame = async (telegramId, gameId) => {
    try {
        const response = await fetch(`${host}/api/games/${telegramId}/${gameId}`, {
            method: "PUT",
        });
        return await response.json()
    } catch (error) {
        console.error("Error creating game:", error);
    }
};