import React from 'react';

const ActiveGameRoom = ({game, onClose, onFlipCoin}) => {
    if (!game) return null;
    return (
        <div className="active-game-dialog">
            <h2>Game with {game.initiatorUsername}</h2>
            <p>Bet: {game.bet} flipky</p>
            <p>Your choice: {game.initiatorChoice}</p>
            {game.result ? (
                <p>Result: {game.result}</p>
            ) : (
                <div style={{display: 'flex', justifyContent: 'center', gap: "15px"}}>
                    <button style={{padding: "10px", width: "100px", borderRadius: "15px"}} className="join-game-btn"
                            onClick={onFlipCoin}>Flip Coin
                    </button>
                    <button style={{padding: "10px", width: "100px", borderRadius: "15px"}} className="delete-game-btn"
                            onClick={onClose}>Cancel
                    </button>
                </div>
            )}
        </div>
    );
};

export default ActiveGameRoom;