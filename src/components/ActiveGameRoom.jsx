import React, {useState} from 'react';
import {cancelGame, flipCoin} from "../api";

const ActiveGameRoom = ({score, setScore, game, setActiveGame, games, setGames}) => {

    const handleFlipCoin = async () => {
        try {
            const result = await flipCoin(game.id);
            const updatedGames = games.filter(g => g.id !== game.id)
            const isWin = !result.isInitiatorWins
            const newScore = {
                wins: isWin ? score.wins + 1 : score.wins,
                losses: isWin ? score.losses : score.losses + 1,
                playedGames: score.playedGames + 1,
                flipkyBalance: isWin ? score.flipkyBalance + game.bet : score.flipkyBalance - game.bet,
                totalWinFlipky: isWin ? score.totalWinFlipky + game.bet : score.totalWinFlipky,
                totalLossFlipky: !isWin ? score.totalLossFlipky + game.bet : score.totalLossFlipky
            }
            setGames(updatedGames)
            setActiveGame({...game, result: result});
            setScore(newScore)
        } catch (error) {
            console.error("Error flipping coin:", error);
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
            {!game.result ? (
                <div>
                    <h2>Game with {game.initiatorUsername}</h2>
                    <p>Bet: {game.bet} flipky</p>
                    <p>Your choice: {game.initiatorChoice}</p>
                    <div style={{display: 'flex', justifyContent: 'center', gap: "15px"}}>
                        <button style={{padding: "10px", width: "100px", borderRadius: "15px"}}
                                className="join-game-btn"
                                onClick={handleFlipCoin}>Flip Coin
                        </button>
                        <button style={{padding: "10px", borderRadius: "15px"}}
                                className="button"
                                onClick={handleCancelGame}>Cancel
                        </button>
                    </div>
                </div>
            ) : (
                <div className="game-content">
                    <p>Coin shows: {game.result.coinResult}</p>
                    <p>You {game.result.isInitiatorWins ? "loss." : "win!"} </p>
                    <div style={{display: 'flex', justifyContent: 'center'}}>
                        <button style={{textAlign: "center"}} className="button"
                                onClick={() => {
                                    setActiveGame(null)
                                }}>
                            Close
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ActiveGameRoom;