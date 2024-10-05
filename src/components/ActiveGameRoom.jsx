import React from 'react';
import {cancelGame, flipCoin} from "../api";

const ActiveGameRoom = ({initUser, game, setActiveGame}) => {
    if (!game) return null;

    const handleFlipCoin = async () => {
        try {
            const result = await flipCoin(game.id);
            console.log(result)
            setActiveGame(result);
        } catch (error) {
            console.error("Error flipping coin:", error);
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
                        <button style={{padding: "10px", width: "100px", borderRadius: "15px"}}
                                className="delete-game-btn"
                                onClick={handleCancelGame}>Cancel
                        </button>
                    </div>
                </div>
            ) : (
                <div className="game-content">
                    <p>Result: </p>
                    <p>Coin shows: {game.result.coinResult}</p>
                    <p>You {game.result.isInitiatorWins ? "loss." : "win!"} </p>
                    <div style={{display: 'flex', justifyContent: 'center'}}>
                        <butto style={{width: "100px"}} className="delete-game-btn" onClick={() => setActiveGame(null)}>
                            Close
                        </butto>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ActiveGameRoom;