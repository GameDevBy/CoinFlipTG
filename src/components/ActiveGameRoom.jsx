import React, {useEffect, useState} from 'react';
import {cancelGame, flipCoin} from "../api";
import {Choice} from "../constants";
import CoinFlipAnimation from "./CoinFlipAnimation";

const ActiveGameRoom = ({score, setScore, game, setActiveGame}) => {
    const [isFlipping, setIsFlipping] = useState(false);
    const [coinSide, setCoinSide] = useState(null);
    const [showResult, setShowResult] = useState(false);
    const [userChoice, setUserChoice] = useState(null);

    useEffect(() => {
        if (game) {
            game.initiatorChoice.toLowerCase() === Choice.tails.toLowerCase()
                ? setUserChoice(Choice.heads)
                : setUserChoice(Choice.tails)

        }
    }, [game]);

    const handleFlipCoin = async () => {
        setIsFlipping(true);
        setCoinSide(null);
        setShowResult(false);

        // Start the flipping animation
        const flipInterval = setInterval(() => {
            setCoinSide(prev => prev === Choice.heads.toLowerCase() ? Choice.tails.toLowerCase() : Choice.heads.toLowerCase());
        }, 300); // Change coin side every 150ms

        try {
            const result = await flipCoin(game.id);
            // Continue flipping for 3 seconds
            await new Promise(resolve => setTimeout(resolve, 1500));

            // Stop the flipping and show the result side
            clearInterval(flipInterval);
            setCoinSide(result.coinResult.toLowerCase());
            setIsFlipping(false);

            // Wait for 1.5 seconds to show the final side
            await new Promise(resolve => setTimeout(resolve, 1500));

            setShowResult(true);

            const isWin = !result.isInitiatorWins;
            const gameBet = Number(game.bet);
            const newScore = {
                wins: isWin ? score.wins + 1 : score.wins,
                losses: isWin ? score.losses : score.losses + 1,
                playedGames: score.playedGames + 1,
                flipkyBalance: isWin ? score.flipkyBalance + gameBet : score.flipkyBalance - gameBet,
                totalWinFlipky: isWin ? score.totalWinFlipky + gameBet : score.totalWinFlipky,
                totalLossFlipky: !isWin ? score.totalLossFlipky + gameBet : score.totalLossFlipky
            };
            setActiveGame({...game, result: result});
            setScore(newScore);
        } catch (error) {
            console.error("Error flipping coin:", error);
            clearInterval(flipInterval);
            setIsFlipping(false);
        }
    };

    const handleCancelGame = async () => {
        try {
            const result = await cancelGame(game.id)
            setActiveGame(null)
        } catch (error) {
            console.error("Error flipping coin:", error);
        }
    }
    return (
        <div className="active-game-dialog">
            {!showResult ? (
                <div>
                    <h2>Game with {game.initiatorUsername}</h2>
                    <p>Bet: {game.bet} flipky</p>
                    <p>Your choice: {userChoice}</p>
                    {isFlipping || coinSide ? (
                        <CoinFlipAnimation coinSide={coinSide} isFlipping={isFlipping}/>
                    ) : (
                        <div style={{display: 'flex', justifyContent: 'center', gap: "15px"}}>
                            <button style={{padding: "10px", width: "100px", borderRadius: "15px"}}
                                    className="join-game-btn"
                                    onClick={handleFlipCoin}>Flip Coin
                            </button>
                            <button style={{width: "100px", padding: "10px", borderRadius: "15px"}}
                                    className="delete-game-btn"
                                    onClick={handleCancelGame}>Cancel
                            </button>
                        </div>
                    )}
                </div>
            ) : (
                <div className="game-content">
                    <p>Coin shows: {game.result.coinResult}</p>
                    <img
                        src={`${process.env.PUBLIC_URL}/images/${game.result.coinResult.toLowerCase()}.png`}
                        alt={game.result.coinResult}
                        className="result-coin"
                    />
                    <p>{game.result.isInitiatorWins ? "Sorry, You loss." : "Congrats, you win!"}</p>
                    <div style={{display: 'flex', justifyContent: 'center', width: "100%"}}>
                        <button style={{textAlign: "center"}} className="button" onClick={() => setActiveGame(null)}>
                            Close
                        </button>
                    </div>
                </div>
            )}
        </div>

    );
};

export default ActiveGameRoom;