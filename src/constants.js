export const tabs = ["home", "games"];
export const choices = ["Heads", "Tails"];

export const createGameUrl = (game) => `https://t.me/${process.env.REACT_APP_TG_BOT_NAME}?start=game_${game.gameCode}`