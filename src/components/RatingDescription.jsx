import React from 'react';

const RatingDescription = () => {
    return (
        <div style={{
            textAlign: "left",
        }}>
            <h2 style={{
                textAlign: "center",
            }}>How Your Rating is Calculated</h2>
            <p>Your rating is calculated based on three key factors:</p>
            <ol>
                <li>
                    <strong>Win Rate (60% of your rating):</strong> This is the most important factor. It's
                    calculated as (wins / total games played) and then multiplied by 60. A higher win rate
                    significantly boosts your rating.
                </li>
                <li>
                    <strong>Average Win Amount (20% of your rating):</strong> This is your total winnings
                    divided by the number of wins, multiplied by 0.2. Winning larger amounts in each game
                    will
                    improve your rating.
                </li>
                <li>
                    <strong>Games Played (20% of your rating):</strong> This factor rewards active players.
                    It's
                    calculated as (number of games played / 100), capped at 1, and then multiplied by 20.
                    Playing up to 100 games will gradually increase this part of your rating.
                </li>
            </ol>
            <h3>To improve your rating:</h3>
            <ul>
                <li>Focus on winning more games</li>
                <li>Aim for higher winnings in each game</li>
                <li>Play regularly (up to 100 games for maximum benefit)</li>
            </ul>
            <p><em>Keep in mind that your win rate has the biggest impact on your overall rating.</em></p>

        </div>
    );
};

export default RatingDescription;