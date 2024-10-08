import React, {useState} from 'react';
import {cancelGame, flipCoin} from "../api";
import {Choice} from "../constants";

const ActiveGameRoom = ({score, setScore, game, setActiveGame, games, setGames}) => {
    const [isFlipping, setIsFlipping] = useState(false);
    const [coinSide, setCoinSide] = useState(null);
    const [showResult, setShowResult] = useState(false);

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

            const updatedGames = games.filter(g => g.id !== game.id);
            const isWin = !result.isInitiatorWins;
            const newScore = {
                wins: isWin ? score.wins + 1 : score.wins,
                losses: isWin ? score.losses : score.losses + 1,
                playedGames: score.playedGames + 1,
                flipkyBalance: isWin ? score.flipkyBalance + game.bet : score.flipkyBalance - game.bet,
                totalWinFlipky: isWin ? score.totalWinFlipky + game.bet : score.totalWinFlipky,
                totalLossFlipky: !isWin ? score.totalLossFlipky + game.bet : score.totalLossFlipky
            };
            setGames(updatedGames);
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
            const updatedGames = games.map(g => g.id === game.id ? result : g)
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
                    <p>Your choice: {game.initiatorChoice}</p>
                    {isFlipping || coinSide ? (
                        <div className="coin-flip-animation">
                            <img
                                src={`${process.env.PUBLIC_URL}/images/${coinSide || 'heads'}.png`}
                                alt="Flipping coin"
                                className={isFlipping ? "flipping-coin" : "result-coin"}
                            />
                        </div>
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