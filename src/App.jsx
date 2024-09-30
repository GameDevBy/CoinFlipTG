import React, {useEffect, useState} from "react";
import "./App.css";
import ScoreContent from "./components/ScoreContent";
import {createGame, fetchGames, fetchUserData} from "./api";
import GamesContent from "./components/GamesContent";
import {useTelegram} from "./hooks/useTelegram";
import {choices, createGameShareUrl, tabs} from "./constants";

function App() {
    const {webAppUser, tg} = useTelegram()
    const [initUser, setInitUser] = useState();
    const [activeTab, setActiveTab] = useState("home");
    const [isCreatingGame, setIsCreatingGame] = useState();
    const [bet, setBet] = useState(1);
    const [choice, setChoice] = useState(choices[0]);
    const [score, setScore] = useState();
    const [games, setGames] = useState([]);
    const [lastCreatedGame, setLastCreatedGame] = useState();

    useEffect(() => {
        if (!webAppUser) {//remove
            const user = {
                id: webAppUser?.id ?? 6900305455, //remove
                username: webAppUser?.username ?? "username",
            };
            setInitUser(user);
            if (user?.id) {
                fetchUserData(user, setScore);
                fetchGames(setGames);
            }
        }
        tg.expand();
    }, [webAppUser]);

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


    const joinGame = async () => {
        try {
            const response = await fetch("/api/games/available");
            const availableGames = await response.json();
            if (availableGames.length > 0) {
                await fetch(`/api/games/${availableGames[0].id}/join`, {
                    method: "POST",
                });
                tg.sendData(
                    JSON.stringify({action: "gameJoined", gameId: availableGames[0].id})
                );
            }
        } catch (error) {
            console.error("Error joining game:", error);
        }
    };

    const sortGames = (criteria) => {
        console.log(`Sorting games by ${criteria}`);
        // Implement sorting logic here
    };

    const handleCreateGame = async () => {
        const requestData = {
            initiatorId: initUser.id,
            initiatorUsername: initUser.username,
            bet: bet,
            initiatorChoice: choice.toLocaleUpperCase(),
        }
        const createdGame = await createGame(requestData)
        setLastCreatedGame(createdGame);
        console.log(createdGame)
        setGames([...games, createdGame]);
    }

    const shareLastCreatedGame = () => {
        if (lastCreatedGame) {
            const gameUrl = createGameShareUrl(lastCreatedGame);
            const text = `Join my CoinFlip game!\nBet: ${lastCreatedGame.bet} flypky\nMy choice: ${lastCreatedGame.initiatorChoice}`;

            if (typeof tg.showPopup === 'function') {
                tg.showPopup({
                    title: 'Share Game',
                    message: 'How would you like to share this game?',
                    buttons: [
                        {id: 'share', type: 'default', text: 'Share URL'},
                        {id: 'cancel', type: 'cancel'},
                    ]
                }, (buttonId) => {
                    if (buttonId === 'share') {
                        shareUrl(gameUrl, text);
                    }
                });
            } else {
                // Fallback for older versions
                tg.showAlert('Sharing game...', () => {
                    shareUrl(gameUrl, text);
                });
            }
        }
    };

    const shareUrl = (url, text) => {
        if (typeof tg.shareUrl === 'function') {
            tg.shareUrl({
                url: url,
                text: text
            });
        } else {
            // Fallback if shareUrl is not available
            tg.openLink(url);
        }
    };

    if (!(initUser || score)) {
        return (
            <div className="content active">
                <h2 style={{marginBottom: "20px"}}>Welcome to CoinFlip!</h2>
                <h3>Loading...</h3>
            </div>
        )
    }

    return (
        <div className="container">
            <div className="tabs">
                {tabs.map((tab) => (
                    <div
                        key={tab}
                        className={`tab ${activeTab === tab ? "active" : ""}`}
                        onClick={() => setActiveTab(tab)}
                    >
                        {tab.charAt(0).toUpperCase() + tab.slice(1)}
                    </div>
                ))}
            </div>
            {activeTab === "home" && (
                <div className="content active">
                    <h2 style={{marginBottom: "20px"}}>Welcome to CoinFlip!</h2>
                    {score && <ScoreContent score={score}/>}
                    <button className="button" onClick={openCreateGameMenu}>
                        Create New Game
                    </button>
                    {isCreatingGame && (
                        <div style={{
                            display: "flex",
                            flexDirection: "row",
                            justifyContent: "center",
                            alignItems: "center",
                            margin: "20px",
                            gap: "15px",
                        }}>
                            <div
                                style={{
                                    display: "flex",
                                    flexDirection: "column",
                                    justifyContent: "center",
                                    alignItems: "center",
                                    margin: "20px",
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
                                    disabled={!bet || !choice}
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
                                        width: "170px",
                                    }}
                                    disabled={!bet || !choice}
                                    onClick={shareLastCreatedGame}
                                >
                                    <p>Share last created game</p>
                                    <p><strong>{lastCreatedGame.bet} flypky</strong></p>
                                    <p><strong>{lastCreatedGame.initiatorChoice}</strong></p>
                                </button>}
                            </div>
                        </div>
                    )}
                </div>
            )}
            {activeTab === "games" && (
                <div className="content active">
                    <h2>Current Games</h2>
                    <GamesContent initUser={initUser} games={games}/>
                </div>
            )}
        </div>
    );
}

export default App;
