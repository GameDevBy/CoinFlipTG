import Rating from "./components/Rating";

export const MIN_BET_VALUE = 1;

export const Tab = {
    home: "Home",
    games: "Games",
    shop: "Shop",
    history: "History",
    rating: "Rate"
}

export const GameAction = {
    new: "NEW_GAME",
    delete: "DELETE_GAME",
    update: "UPDATE_GAME",
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