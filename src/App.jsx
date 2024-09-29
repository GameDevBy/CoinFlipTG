import React, {useEffect, useState} from "react";
import "./App.css";
import ScoreContent from "./components/ScoreContent";
import {createGame, fetchGames, fetchUserData} from "./api";
import GamesContent from "./components/GamesContent";

const tg = window.Telegram.WebApp;
const tabs = ["home", "games"];
const choices = ["Heads", "Tails"];

function App() {
    const [initUser, setInitUser] = useState();
    const [activeTab, setActiveTab] = useState("home");
    const [isCreatingGame, setIsCreatingGame] = useState();
    const [bet, setBet] = useState(1);
    const [choice, setChoice] = useState(choices[0]);
    const [score, setScore] = useState();
    const [games, setGames] = useState([]);

    useEffect(() => {
        const webAppUser = tg.initDataUnsafe.user;
        const user = {
            id: webAppUser?.id ?? 6900305455,
            username: webAppUser?.username ?? "makar0hka",
        };
        setInitUser(user);
        if (user?.id) {
            fetchUserData(user, setScore);
            fetchGames(setGames);
        }
        tg.expand();
    }, [tg.initDataUnsafe.user]);

    const openCreateGameMenu = () => {
        setIsCreatingGame(!isCreatingGame);
    };

    const handleBetChange = (e) => {
        setBet(e.target.value);
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
                                Confirm
                            </button>
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
