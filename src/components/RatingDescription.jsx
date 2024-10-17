import React from 'react';

const RatingDescription = () => {
    return (
        <div style={{
            textAlign: "left",
        }}>
            <h2 style={{
                marginTop: "0",
                textAlign: "center",
            }}>How Your Rating is Calculated</h2>
            <p>Your rating is calculated based on three key factors:</p>
            <ol>
                <li><strong>Win Rate (40% of your rating):</strong><br/>
                    This factor is calculated as (wins / total
                    games played) and then multiplied by 40. The impact of this factor increases as you play more games,
                    reaching its full potential after 10 games. A higher win rate significantly boosts your rating.
                </li>
                <li><strong>Average Win Amount (35% of your rating):</strong><br/>
                    This is your total winnings in flipky
                    divided by the number of wins. This value is normalized (capped at 1000 flipky per win) and then
                    multiplied by 35. Winning larger amounts of flipky in each game will significantly improve your
                    rating, up to the maximum rated win amount of 1000 flipky.
                </li>
                <li><strong>Games Played (25% of your rating):</strong><br/>
                    This factor rewards active players. It's
                    calculated as (number of games played / 10), capped at 1, and then multiplied by 25. Playing up to
                    10 games will gradually increase this part of your rating. After 10 games, this factor reaches its
                    maximum contribution to your overall rating.
                </li>
            </ol>
            <h3>To improve your rating:</h3>
            <ul>
                <li>Focus on maintaining a high win rate, especially as you play more games</li>
                <li>Aim for higher winnings in flipky for each game (up to 1000 flipky per win for maximum benefit)</li>
                <li>Play regularly (up to 100 games for maximum benefit)</li>
            </ul>
            <p><em>Keep in mind that while your win rate is still important, the amount of flipky you win in each game
                now has a significant impact on your overall rating. Consistency in both winning and earning high
                amounts of flipky will lead to the best rating.</em></p>

        </div>
    );
};

export default RatingDescription;