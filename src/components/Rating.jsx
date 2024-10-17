import React, {useEffect, useState} from 'react';
import {fetchRating} from "../api";
import RatingDescription from "./RatingDescription";

const Rating = () => {
    const [rankedUsers, setRankedUsers] = useState([]);
    const [showDescription, setShowDescription] = useState(false);
    useEffect(() => {
        getRating();
    }, []);

    const getRating = async () => {
        const res = await fetchRating();
        setRankedUsers(res)
    }

    const ratingDescription = "Your rating is calculated based on your win rate, average win amount, and number of games played. Higher win rates, larger average wins, and more games played will increase your rating.";
    return (
        <div className="content active">
            <h2>Leaderboard</h2>
            <button className="button" onClick={() => setShowDescription(true)}>How it calculate...</button>
            <div className="game-table">
                <div className="table-header-rating">
                    <div className="header-cell">Rank</div>
                    <div className="header-cell">Username</div>
                    <div className="header-cell">Rating</div>
                    <div className="header-cell">Games</div>
                    <div className="header-cell">Win rate</div>
                </div>
                <div className="table-body">
                    {rankedUsers.map((rate) => (
                        <div key={rate.rank} className="table-row-rating">
                            <div className="table-cell">{rate.rank}</div>
                            <div className="table-cell">{rate.username.trim() ? rate.username : "!Hidden"}</div>
                            <div className="table-cell">{rate.rating}</div>
                            <div className="table-cell">{rate.playedGames}</div>
                            <div className="table-cell">{rate.winRate}%</div>
                        </div>
                    ))}
                </div>
            </div>
            {showDescription &&
                <div className="rating-description-dialog">
                    <button
                        className="dialog-close-button"
                        onClick={() => setShowDescription(false)}
                        style={{
                            top: "5px",
                            right: "10px",
                            float: "right"
                        }}
                    >
                        ×
                    </button>
                    <div className="description-content">
                        <RatingDescription/>
                    </div>
                </div>}
        </div>
    );
};

export default Rating;