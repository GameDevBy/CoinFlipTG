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
    const {webAppUser, tg} = useTelegram()
    const [initUser, setInitUser] = useState();
    const [activeTab, setActiveTab] = useState(Tab.home);
    const [games, setGames] = useState([]);
    const [activeGame, setActiveGame] = useState(null);

    useEffect(() => {
        if (webAppUser) {
            const user = {
                telegramId: webAppUser?.id,
                username: webAppUser?.username,
            };
            if (user?.telegramId) {
                fetchUserData(user, setInitUser);
                fetchGames(setGames);
            }
        }
    }, [webAppUser]);

    useEffect(() => {
        if (games.length > 0) {
            const currentActiveGame = games.find(game => game.state === GameState.IN_PROGRESS && game.opponentId === initUser.telegramId)
            setActiveGame(currentActiveGame)
        }
    }, [games]);

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

    if (!initUser || !initUser.score) {
        return (
            <div className="content active">
                <h2 style={{marginBottom: "20px"}}>Welcome to CoinFlip!</h2>
                <h3>Loading...</h3>
            </div>
        )
    }

    return (
        <div className="container">
            <div className="header">
                {initUser.score && <ScoreContent score={initUser.score}/>}
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
                <HomeContent initUser={initUser} setInitUser={setInitUser} games={games} setGames={setGames}/>
            )}
            {activeTab === Tab.games && (
                <GamesContent initUser={initUser} setInitUser={setInitUser} games={games} setGames={setGames}
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
            <ActiveGameRoom
                initUser={initUser}
                game={activeGame}
                setActiveGame={setActiveGame}
                games={games}
                setGames={setGames}
            />
        </div>
    );
}

export default App;
