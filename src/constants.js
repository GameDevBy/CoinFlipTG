export const MIN_BET_VALUE = 1;

export const Tab = {
    home: "Home",
    games: "Games",
    shop: "Shop",
    history: "History",
    rating: "Rate"
}

export const GameAction = {
    newGame: "GAME_NEW",
    deleteGame: "GAME_DELETE",
    updateGame: "GAME_UPDATE",
    updateScore: "SCORE_UPDATE",
}

export const SseType = {
    game: "GAME",
    score: "SCORE",
    heartbeat: "ping"
}

export const tabs = Object.values(Tab);
export const Choice={
    heads: "HEADS",
    tails: "TAILS"
}
export const choices = Object.values(Choice);

export const GameState = {
    WAITING_FOR_OPPONENT: "WAITING_FOR_OPPONENT",
    IN_PROGRESS: "IN_PROGRESS",
    FINISHED: "FINISHED",
}