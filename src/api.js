const host = 'https://coin-flip-tg-28d6109cebba.herokuapp.com'
export const fetchUserData = async (user, setScore) => {
    try {
        const response = await fetch(`${host}/api/users/${user.id}/${user.username}`);

        const userData = await response.json();
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
        const game = await response.json();
        // No need for tg.sendData here
        console.log("Game created:", game);
        return game
    } catch (error) {
        console.error("Error creating game:", error);
    }
};