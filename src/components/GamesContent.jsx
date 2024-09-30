import React, {useState} from 'react';
import {formatDate} from "../utils";

const GamesContent = ({games, initUser}) => {
    console.log(games)
    const [activeGameId, setActiveGameId] = useState(null);

    const actionButton = (game) => {
        return game.initiatorId === initUser.id ? (
            <button
                className="delete-game-btn"
                onClick={(e) => handleDeleteGame(e, game.id)}
            >
                Delete Game
            </button>
        ) : (
            <button
                className="join-game-btn"
                onClick={(e) => handleJoinGame(e, game.id)}
            >
                Join Game
            </button>
        );
    };

    const handleGameClick = (gameId) => {
        setActiveGameId(activeGameId === gameId ? null : gameId);
    };

    const handleDeleteGame = (gameId) => {
        // Implement the logic to delete the game
        console.log(`Deleting game with id: ${gameId}`);
        // You might want to make an API call here and then update the games state
    };

    const handleJoinGame = (gameId) => {
        // Implement the logic to join the game
        console.log(`Joining game with id: ${gameId}`);
        // You might want to make an API call here and then update the games state
    };

    return (
        <div className="game-table">
            <div className="table-header">
                <div className="header-cell">Username</div>
                <div className="header-cell">Bet (flipky)</div>
                <div className="header-cell">Created</div>
            </div>
            <div className="table-body">
                {games.map((game) => (
                    <div key={game.id} className="table-row" onClick={() => handleGameClick(game.id)}>
                        <div className="table-cell" style={{
                            padding: activeGameId === game.id ? "6px 10px" : "10px"
                        }}>
                            {activeGameId === game.id
                                ? actionButton(game)
                                : game.initiatorId === initUser.id ? "Me" : game.initiatorUsername}
                        </div>
                        <div className="table-cell">{game.bet}</div>
                        <div className="table-cell">{formatDate(game.createdAt)}</div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default GamesContent;