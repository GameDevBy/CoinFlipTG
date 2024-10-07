import React, {useEffect, useState} from "react";
import "./App.css";
import ScoreContent from "./components/ScoreContent";
import {fetchGames, fetchUserData} from "./api";
import GamesContent from "./components/GamesContent";
import {useTelegram} from "./hooks/useTelegram";
import {GameState, Tab, tabs} from "./constants";
import ActiveGameRoom from "./components/ActiveGameRoom";
import HomeContent from "./components/HomeContent";

function App() {
    const {webAppUser, tg, isReady} = useTelegram()
    const [initUser, setInitUser] = useState();
    const [score, setScore] = useState();
    const [activeTab, setActiveTab] = useState(Tab.home);
    const [games, setGames] = useState([]);
    const [activeGame, setActiveGame] = useState(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        if (webAppUser && isReady) {
            const user = {
                telegramId: webAppUser?.id,
                username: webAppUser?.username,
            };
            if (user?.telegramId) {
                fetchUserData(user, setInitUser, setScore);
                fetchGames(setGames);
            }
        }
    }, [webAppUser, isReady]);

    useEffect(() => {
        if (games.length > 0 && initUser) {
            const currentActiveGame = games.find(game => game.state === GameState.IN_PROGRESS && game.opponentId === initUser.telegramId)
            setActiveGame(currentActiveGame)
        }
    }, [games, initUser]);


    const joinGame = async () => {
        try {
            const response = await fetch("/api/games/available");
            const availableGames = await response.json();
            if (availableGames.length > 0) {
                await fetch(`/api/games/${availableGames[0].id}/join`, {
                    method: "POST",
                });
            }
        } catch (error) {
            console.error("Error joining game:", error);
        }
    };

    if (!initUser || !score) {
        return (
            <div className="content active">
                <h2 style={{marginBottom: "20px"}}>Welcome to CoinFlip!</h2>
                <h3>Loading...</h3>
            </div>
        )
    }

    return (
        isLoading &&
        <div className="container">
            <div className="header">
                {score && <ScoreContent score={score}/>}
                <div className="tabs">
                    {tabs.map((tab) => (
                        <div
                            key={tab}
                            className={`tab ${activeTab === tab ? "active" : ""}`}
                            onClick={() => setActiveTab(tab)}
                        >
                            {tab}
                        </div>
                    ))}
                </div>
            </div>
            {activeTab === Tab.home && (
                <HomeContent initUser={initUser} setScore={setScore} games={games} setGames={setGames}/>
            )}
            {activeTab === Tab.games && (
                <GamesContent initUser={initUser} setScore={setScore} games={games} setGames={setGames}
                              setActiveGame={setActiveGame}/>
            )}
            {activeTab === Tab.shop && (
                <div style={{textAlign: "center", marginTop: "140px"}}>
                    <h2>Coming soon...</h2>
                </div>
            )} {activeTab === Tab.history && (
            <div style={{textAlign: "center", marginTop: "140px"}}>
                <h2>Coming soon...</h2>
            </div>
        )} {activeTab === Tab.rating && (
            <div style={{textAlign: "center", marginTop: "140px"}}>
                <h2>Coming soon...</h2>
            </div>
        )}
            {activeGame &&
            <ActiveGameRoom
                score={score}
                setScore={setScore}
                game={activeGame}
                setActiveGame={setActiveGame}
                games={games}
                setGames={setGames}
            />}
        </div>
    );
}

export default App;
