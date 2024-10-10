import React, {useState} from 'react';
import {Choice, MIN_BET_VALUE} from "../constants";
import {botGame, createGame} from "../api";
import {createGameUrl, createShareUrl} from "../utils";
import {useTelegram} from "../hooks/useTelegram";
import ConfirmModal from "./ConfirmModal";
import CreateGameDialog from "./CreateGameDialog";
import CoinFlipAnimation from "./CoinFlipAnimation";

const HomeContent = ({initUser, score, setScore, games, setGames, lastCreatedGame, setLastCreatedGame}) => {
    const {tg} = useTelegram()
    const [isCreatingGame, setIsCreatingGame] = useState();
    const [bet, setBet] = useState(MIN_BET_VALUE);
    const [choice, setChoice] = useState(Choice.heads);
    const [isBotGame, setIsBotGame] = useState(false)
    const [botGameConfirm, setBotGameConfirm] = useState(false)
    const [isFlipping, setIsFlipping] = useState(false);
    const [coinSide, setCoinSide] = useState(null);
    const [showResult, setShowResult] = useState(false);
    const [botGameResult, setBotGameResult] = useState()

    const openCreateGameMenu = () => {
        setIsCreatingGame(!isCreatingGame);
    };

    const handleBetChange = (e) => {
        if (typeof +e.target.value === "number") {
            setBet(Number(e.target.value));
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
        setIsCreatingGame(false);
    }

    const shareLastCreatedGame = () => {
        if (lastCreatedGame) {
            const gameUrl = createGameUrl(lastCreatedGame);
            const text = `Let's play CoinFlip! Bet: ${lastCreatedGame.bet} flipky\nMy choice: ${lastCreatedGame.initiatorChoice}`;
            const shareUrl = createShareUrl(gameUrl, text);
            tg.openTelegramLink(shareUrl);
        }
    };

    const handlePlayVsBot = async (e) => {
        setIsFlipping(true);
        setCoinSide(null);
        setShowResult(false);

        // Start the flipping animation
        const flipInterval = setInterval(() => {
            setCoinSide(prev => prev === Choice.heads.toLowerCase() ? Choice.tails.toLowerCase() : Choice.heads.toLowerCase());
        }, 300); // Change coin side every 150ms

        try {
            const requestData = {
                initiatorId: initUser.telegramId,
                initiatorUsername: initUser.username,
                bet: bet,
                initiatorChoice: choice.toLocaleUpperCase(),
            }
            const result = await botGame(initUser.id, requestData);

            // Continue flipping for 3 seconds
            await new Promise(resolve => setTimeout(resolve, 1500));

            // Stop the flipping and show the result side
            clearInterval(flipInterval);
            setCoinSide(result.coinResult.toLowerCase());
            setIsFlipping(false);

            // Wait for 1.5 seconds to show the final side
            await new Promise(resolve => setTimeout(resolve, 1500));

            setShowResult(true);

            const isWin = result.isInitiatorWins;
            const newScore = {
                wins: isWin ? score.wins + 1 : score.wins,
                losses: isWin ? score.losses : score.losses + 1,
                playedGames: score.playedGames + 1,
                flipkyBalance: isWin ? score.flipkyBalance + bet : score.flipkyBalance - bet,
                totalWinFlipky: isWin ? score.totalWinFlipky + bet : score.totalWinFlipky,
                totalLossFlipky: !isWin ? score.totalLossFlipky + bet : score.totalLossFlipky
            };
            setScore(newScore);
            setBotGameResult(result)
        } catch (error) {
            console.error("Error flipping coin:", error);
            clearInterval(flipInterval);
            setIsFlipping(false);
            setCoinSide(null);
            setShowResult(false);
        }
    };

    const handleBotGameConfirm = (e) => {
        setIsBotGame(true)
        setBotGameConfirm(false)
    };
    const handleCloseBotGame = (e) => {
        setIsBotGame(false)
        setShowResult(false)
        setCoinSide(null)
        setBotGameResult(null)
    };

    return (
        <div className="content active">
            <h2 style={{marginBottom: "40px"}}>Welcome to CoinFlip {initUser.username}!</h2>
            <button className="button" onClick={() => setBotGameConfirm(true)}>
                Play vs Bot
            </button>
            {botGameConfirm &&
                <ConfirmModal
                    message={"Be careful, Coin_Bot can cheat..."}
                    onConfirm={handleBotGameConfirm}
                    onCancel={() => setBotGameConfirm(false)}
                    styleConfirmBtn={{background: "#46b449"}}
                    styleCancelBtn={{background: "#ff4d4d"}}
                />}
            {isBotGame && (
                <div className="active-game-dialog">
                    {!showResult ? (
                        isFlipping || coinSide ? (
                            <CoinFlipAnimation coinSide={coinSide} isFlipping={isFlipping}/>
                        ) : (
                            <CreateGameDialog
                                score={score}
                                choice={choice}
                                handleChoiceChange={handleChoiceChange}
                                bet={bet}
                                handleBetChange={handleBetChange}
                                setOpenModal={() => setIsBotGame(false)}
                                buttonAction={handlePlayVsBot}
                                buttonText={"Play"}
                            />)
                    ) : (
                        <div className="game-content">
                            <p>Coin shows: {botGameResult.coinResult}</p>
                            <img
                                src={`${process.env.PUBLIC_URL}/images/${botGameResult.coinResult.toLowerCase()}.png`}
                                alt={botGameResult.coinResult}
                                className="result-coin"
                            />
                            <p>{botGameResult.isInitiatorWins ? "Congrats, you win!" : "Sorry, You loss."}</p>
                            <div style={{display: 'flex', justifyContent: 'center', width: "100%"}}>
                                <button style={{textAlign: "center"}} className="button"
                                        onClick={handleCloseBotGame}>
                                    Close
                                </button>
                            </div>
                        </div>
                    )}
                </div>)}
            <button className="button" onClick={openCreateGameMenu}>
                Create New Game
            </button>
            {isCreatingGame && (
                <div className="active-game-dialog">
                    <CreateGameDialog
                        score={score}
                        choice={choice}
                        handleChoiceChange={handleChoiceChange}
                        bet={bet}
                        handleBetChange={handleBetChange}
                        handleCreateGame={handleCreateGame}
                        setIsCreatingGame={setIsCreatingGame}
                        buttonAction={handleCreateGame}
                        buttonText={"Create"}
                    />
                </div>
            )}
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
    );
};

export default HomeContent;