import React, {useState} from 'react';
import {Choice, choices, MIN_BET_VALUE} from "../constants";
import {createGame} from "../api";
import {createGameUrl, createShareUrl} from "../utils";
import {useTelegram} from "../hooks/useTelegram";

const HomeContent = ({initUser,setScore, games, setGames}) => {
    const {tg} = useTelegram()
    const [isCreatingGame, setIsCreatingGame] = useState();
    const [lastCreatedGame, setLastCreatedGame] = useState();
    const [bet, setBet] = useState(MIN_BET_VALUE);
    const [choice, setChoice] = useState(Choice.heads);

    const openCreateGameMenu = () => {
        setIsCreatingGame(!isCreatingGame);
    };

    const handleBetChange = (e) => {
        if (typeof +e.target.value === "number") {
            setBet(e.target.value);
        }
    };

    const handleChoiceChange = (choice) => {
        setChoice(choice);
    };

    const handleCreateGame = async () => {
        const requestData = {
            initiatorId: initUser.telegramId,
            initiatorUsername: initUser.username,
            bet: bet,
            initiatorChoice: choice.toLocaleUpperCase(),
        }
        const createdGame = await createGame(requestData)
        setScore(prevScore => ({
            ...prevScore,
            flipkyBalance: prevScore.flipkyBalance - bet
        }));
        setLastCreatedGame(createdGame);
        setGames([...games, createdGame]);
    }

    const shareLastCreatedGame = () => {
        if (lastCreatedGame) {
            const gameUrl = createGameUrl(lastCreatedGame);
            const text = `Let's play CoinFlip! Bet: ${lastCreatedGame.bet} flipky\nMy choice: ${lastCreatedGame.initiatorChoice}`;
            const shareUrl = createShareUrl(gameUrl, text);
            tg.openTelegramLink(shareUrl);
        }
    };

    const handlePlayVsBot = (e) => {

    };

    return (
        <div className="content active">
            <h2 style={{marginBottom: "40px"}}>Welcome to CoinFlip {initUser.username}!</h2>
            <button disabled={true} className="button" onClick={handlePlayVsBot}>
                Play vs Bot
            </button>
            <button className="button" onClick={openCreateGameMenu}>
                Create New Game
            </button>
            {isCreatingGame && (
                <div style={{
                    display: "flex",
                    flexDirection: "row",
                    justifyContent: "center",
                    alignItems: "center",
                    margin: "0px",
                    gap: lastCreatedGame ? "15px" : 0,
                }}>
                    <div
                        style={{
                            display: "flex",
                            flexDirection: "column",
                            justifyContent: "center",
                            alignItems: "center",
                            padding: "20px 20px 20px 10px",
                            gap: "15px",
                        }}
                    >
                        <input
                            style={{
                                width: "150px",
                                border: "none",
                                borderRadius: "15px",
                                paddingLeft: "10px",
                            }}
                            min={1}
                            max={+initUser.score.flipkyBalance}
                            type="number"
                            inputMode="numeric"
                            pattern="[0-9]*"
                            placeholder="Enter your bet"
                            value={bet}
                            onChange={handleBetChange}
                        />
                        <div className="choice-radios">
                            {choices.map((option) => (
                                <div key={option} className="radio-container">
                                    <input
                                        type="radio"
                                        id={option}
                                        name="choice"
                                        value={option}
                                        checked={choice === option}
                                        onChange={() => handleChoiceChange(option)}
                                    />
                                    <label htmlFor={option}>{option}</label>
                                </div>
                            ))}
                        </div>
                        <button
                            className="button"
                            style={{
                                width: "150px",
                            }}
                            disabled={!bet || !choice || bet> initUser.score.flipkyBalance}
                            onClick={() => {
                                if (bet && choice) {
                                    handleCreateGame();
                                }
                            }}
                        >
                            Create
                        </button>
                    </div>
                    <div>
                        {lastCreatedGame && <button
                            className="button"
                            style={{
                                width: "100%",
                            }}
                            disabled={!bet || !choice}
                            onClick={shareLastCreatedGame}
                        >
                            <p>Share last created game</p>
                            <p style={{fontStyle: "italic"}}>{lastCreatedGame.bet} flipky</p>
                            <p style={{fontStyle: "italic"}}>{lastCreatedGame.initiatorChoice}</p>
                        </button>}
                    </div>
                </div>
            )}
        </div>
    );
};

export default HomeContent;