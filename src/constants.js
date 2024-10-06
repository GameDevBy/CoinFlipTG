export const MIN_BET_VALUE = 1;

export const Tab = {
    home: "Home",
    games: "Games",
    shop: "Shop",
    history: "History",
    rating: "Rating"
}

export const tabs = Object.values(Tab);
export const Choice={
    heads: "Heads",
    tails: "Tails"
}
export const choices = Object.values(Choice);

export const GameState = {
    WAITING_FOR_OPPONENT: "WAITING_FOR_OPPONENT",
    WAITING_FOR_BET: "WAITING_FOR_BET",
    ENTERING_CUSTOM_BET: "ENTERING_CUSTOM_BET",
    IN_PROGRESS: "IN_PROGRESS",
    FINISHED: "FINISHED",
}