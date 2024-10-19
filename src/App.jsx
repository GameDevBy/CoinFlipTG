import React, {useEffect, useState} from "react";
import "./App.css";
import ScoreContent from "./components/ScoreContent";
import {fetchUserData} from "./api";
import GamesContent from "./components/GamesContent";
import {useTelegram} from "./hooks/useTelegram";
import {Choice, GameAction, GameState, Tab, tabs} from "./constants";
import ActiveGameRoom from "./components/ActiveGameRoom";
import HomeContent from "./components/HomeContent";
import HistoryContent from "./components/HistoryContent";
import CoinFlipAnimation from "./components/CoinFlipAnimation";
import Rating from "./components/Rating";
import useSSE from "./hooks/useSSE";

function App() {
    const {webAppUser, tg, isReady} = useTelegram()
    const {setUserId, scoreData, gameData, closeEventSource} = useSSE();
    const [initUser, setInitUser] = useState();
    const [score, setScore] = useState();
    const [activeTab, setActiveTab] = useState(Tab.home);
    const [games, setGames] = useState([]);
    const [history, setHistory] = useState([]);
    const [activeGame, setActiveGame] = useState(null);
    const [lastCreatedGame, setLastCreatedGame] = useState();
    const [isLoading, setIsLoading] = useState(true);
    const [coinSide, setCoinSide] = useState(null);

    useEffect(() => {
        let animationFrameId;
        let lastFlipTime = 0;
        const flipInterval = 100; // Flip every 100ms

        const animate = (currentTime) => {
            if (currentTime - lastFlipTime > flipInterval) {
                setCoinSide(prev => prev === Choice.heads.toLowerCase() ? Choice.tails.toLowerCase() : Choice.heads.toLowerCase());
                lastFlipTime = currentTime;
            }
            animationFrameId = requestAnimationFrame(animate);
        };

        animationFrameId = requestAnimationFrame(animate);
        if (webAppUser && isReady) {
            const user = {
                telegramId: webAppUser?.id,
                username: webAppUser?.username ?? "!Hidden",
            };
            if (user?.telegramId) {
                fetchUserData(user, setInitUser, setScore).finally(() => setIsLoading(false));
            }
        }

        return () => {
            cancelAnimationFrame(animationFrameId);
        };
    }, [webAppUser, isReady]);

    useEffect(() => {
        if (scoreData) {
            handleSSEUpdate(scoreData)
        }
    }, [scoreData]);

    useEffect(() => {
        const handleWebAppClose = () => closeEventSource(initUser?.id);
        if (tg) {
            tg.onEvent('web_app_close', handleWebAppClose);
        }
        return () => {
            if (tg) {
                tg.offEvent('web_app_close', handleWebAppClose);
            }
            closeEventSource(initUser?.id);
        };
    }, [tg, initUser?.id]);

    useEffect(() => {
        if ((!tg || !webAppUser) && initUser?.id) {
            closeEventSource(initUser?.id);
        }
    }, [tg, webAppUser, initUser]);

    useEffect(() => {
        if (initUser?.id) {
            setUserId(initUser.id);
        }
    }, [initUser]);

    useEffect(() => {
        if (games.length > 0 && initUser) {
            const currentActiveGame = games.find(game => game.state === GameState.IN_PROGRESS && game.opponentId === initUser.telegramId)
            if (currentActiveGame) {
                setActiveGame(currentActiveGame)
            }
        }
    }, [games, initUser]);

    const handleSSEUpdate = (update) => {
        if (update.action === GameAction.updateScore) {
            setScore(update.score);
        }
    };

    if (!initUser || !score) {
        return (
            <div className="content active">
                <h2 style={{marginBottom: "20px"}}>Welcome to CoinFlip!</h2>
                <h3>Loading...</h3>
                {isLoading && <CoinFlipAnimation coinSide={coinSide} isFlipping={isLoading}/>}
            </div>
        )
    }

    return (
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
                <HomeContent
                    initUser={initUser}
                    score={score}
                    setScore={setScore}
                    lastCreatedGame={lastCreatedGame}
                    setLastCreatedGame={setLastCreatedGame}
                />
            )}
            {activeTab === Tab.games && (
                <GamesContent
                    initUser={initUser}
                    setScore={setScore}
                    games={games}
                    setGames={setGames}
                    setActiveGame={setActiveGame}
                    setLastCreatedGame={setLastCreatedGame}
                    gameData={gameData}
                />
            )}
            {activeTab === Tab.shop && (
                <div style={{textAlign: "center", marginTop: "140px"}}>
                    <h2>Coming soon...</h2>
                </div>
            )} {activeTab === Tab.history && (
            <HistoryContent initUser={initUser} history={history} setHistory={setHistory}/>
        )} {activeTab === Tab.rating && (
            <Rating/>
        )}
            {activeGame &&
                <ActiveGameRoom
                    score={score}
                    setScore={setScore}
                    game={activeGame}
                    setActiveGame={setActiveGame}
                />}
        </div>
    );
}

export default App;
