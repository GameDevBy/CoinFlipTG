import React, {useState} from 'react';
import {createGameUrl, createShareUrl, formatDate} from "../utils";
import {useTelegram} from "../hooks/useTelegram";
import {deleteGame, joinGame} from "../api";
import ConfirmModal from "./ConfirmModal";

const GamesContent = ({games, initUser, setScore, setGames, setActiveGame}) => {
    const {tg} = useTelegram()
    const [activeGameId, setActiveGameId] = useState(null);
    const [showConfirmModal, setShowConfirmModal] = useState(false);
    const [gameToDelete, setGameToDelete] = useState(null);

    const handleGameClick = (gameId) => {
        setActiveGameId(activeGameId === gameId ? null : gameId);
    };

    const handleDeleteGame = (e, game) => {
        e.preventDefault();
        setGameToDelete(game);
        setShowConfirmModal(true);
    };

    const cancelDelete = () => {
        setShowConfirmModal(false);
        setGameToDelete(null);
    };

    const confirmDelete = async () => {
        if (gameToDelete) {
            try {
                const isDelete = await deleteGame(gameToDelete.id);
                if (isDelete) {
                    const updatedArray = games.filter(el => el.id !== gameToDelete.id);
                    setGames(updatedArray);
                    setScore(prevScore => ({
                        ...prevScore,
                        flipkyBalance: prevScore.flipkyBalance + gameToDelete.bet
                    }));
                    console.log(`Deleting game with id: ${gameToDelete.id}`);
                }
            } catch (err) {
                console.error(err);
                // Handle error (maybe show an error message)
            }
        }
        setShowConfirmModal(false);
        setGameToDelete(null);
    };

    const handleShareGame = (e, game) => {
        e.preventDefault();
        const gameUrl = createGameUrl(game);
        const text = `Let's play CoinFlip! Bet: ${game.bet} flipky\nMy choice: ${game.initiatorChoice}`;
        const shareUrl = createShareUrl(gameUrl, text);
        tg.openTelegramLink(shareUrl);
    };

    const handleJoinGame = async (e, game) => {
        e.preventDefault();
        try {
            const joinedGame = await joinGame(initUser.id, game.id);
            if (joinedGame) {
                setActiveGame(joinedGame)
            }else {
                alert("This game is already finished or in progress. Please choose another game.");
            }
        } catch (err) {
            alert("Something went wrong. Please try another game.")
            console.error(err)
        }
    };

    const actionButton = (game) => {
        return game.initiatorId === initUser.telegramId ? ([
                <button
                    className="delete-game-btn"
                    onClick={(e) => handleDeleteGame(e, game)}
                >
                    Delete Game
                </button>, <button
                    className="share-game-btn"
                    onClick={(e) => handleShareGame(e, game)}
                >
                    Share Game
                </button>]
        ) : (
            [<button
                disabled={initUser.score.flipkyBalance < game.bet}
                className="join-game-btn"
                style={{
                    background: initUser.score.flipkyBalance < game.bet ? "gray" : "#46b449"
                }}
                onClick={(e) => handleJoinGame(e, game)}
            >
                Join Game
            </button>]
        );
    };

    return (
        <div className="content active">
            <h2>Current Games</h2>
            <div className="game-table">
                <div className="table-header">
                    <div className="header-cell">Username</div>
                    <div className="header-cell">Bet</div>
                    <div className="header-cell">Choice</div>
                    <div className="header-cell">Created</div>
                </div>
                <div className="table-body">
                    {games.map((game) => (
                        <div key={game.id} className="table-row" onClick={() => handleGameClick(game.id)}>
                            <div className="table-cell" style={{
                                padding: activeGameId === game.id ? "6px 10px" : "10px",
                            }}>
                                {activeGameId === game.id
                                    ? actionButton(game)[0]
                                    : game.initiatorId === initUser.telegramId ? "Me" : game.initiatorUsername}
                            </div>
                            <div className="table-cell">{game.bet}</div>
                            <div className="table-cell">{game.initiatorChoice}</div>
                            <div className="table-cell" style={{
                                padding: activeGameId === game.id && game.initiatorId === initUser.telegramId ? "6px 10px" : "10px"
                            }}>
                                {activeGameId === game.id && game.initiatorId === initUser.telegramId
                                    ? actionButton(game)[1]
                                    : formatDate(game.createdAt)}
                            </div>
                        </div>
                    ))}
                </div>
            </div>
            {showConfirmModal && (
                <ConfirmModal
                    message={`Are you sure you want to delete this game?`}
                    onConfirm={confirmDelete}
                    onCancel={cancelDelete}
                />
            )}
        </div>
    );
};

export default GamesContent;