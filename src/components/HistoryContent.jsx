import React, {useEffect} from 'react';
import {fetchHistory} from "../api";
import {formatDate} from "../utils";
import {Choice} from "../constants";

const HistoryContent = ({initUser, history, setHistory}) => {
    useEffect(() => {
        setUpHistory();
    }, []);

    const setUpHistory = async () => {
        const res = await fetchHistory(initUser.id);
        setHistory(res)
    }

    const displayChoice = (game) => {
        if (initUser.telegramId === game.initiatorId) {
            return game.initiatorChoice;
        } else {
            return game.initiatorChoice === Choice.heads ? Choice.tails : Choice.heads;
        }
    };
    const displayResult = (game) => {
        if (initUser.telegramId === game.initiatorId) {
            return game.result.isInitiatorWins ? "Win" : "Loss";
        } else {
            return game.result.isInitiatorWins ? "Loss" : "Win";
        }
    };
    return (
        <div className="content active">
            <h2>Played Games</h2>
            <div className="game-table">
                <div className="table-header-history">
                    <div className="header-cell header-cell-history">Game VS</div>
                    <div className="header-cell header-cell-history">Bet</div>
                    <div className="header-cell header-cell-history">Choice</div>
                    <div className="header-cell header-cell-history">Played</div>
                    <div className="header-cell header-cell-history">Result</div>
                </div>
                <div className="table-body">
                    {history.map((game) => (
                        <div key={game.id} className="table-row-history">
                            <div className="table-cell">{game.opponentUsername}</div>
                            <div className="table-cell">{game.bet}</div>
                            <div className="table-cell">{displayChoice(game)}</div>
                            <div className="table-cell">{formatDate(game.playedAt)}</div>
                            <div className="table-cell">{displayResult(game)}</div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default HistoryContent;