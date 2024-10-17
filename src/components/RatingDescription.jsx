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
                <li><strong>Win Rate (45% of your rating):</strong><br/>
                    This factor is calculated as (wins / total games played) and then multiplied by 45. The impact of
                    this factor increases as you play more games, reaching its full potential after 100 games. This
                    means that if you have played fewer than 100 games, your win rate will have a reduced impact on your
                    overall rating to ensure stability and fairness for all players. A higher win rate significantly
                    boosts your rating.
                </li>
                <li><strong>Average Win Amount (40% of your rating):</strong><br/>
                    This is your total winnings in flipky divided by the number of wins. This value is normalized
                    (capped at 500 flipky per win) and then multiplied by 40. Winning larger amounts of flipky in each
                    game will significantly improve your rating, up to the maximum rated win amount of 500 flipky.
                </li>
                <li><strong>Games Played (15% of your rating):</strong><br/>
                    This factor rewards active players. You earn points for each game you play, with bonus points
                    awarded for every 10 games. There's no limit, so the more you play, the higher this part of your
                    rating can go!
                </li>
            </ol>
            <h3>To improve your rating:</h3>
            <ul>
                <li>Focus on maintaining a high win rate, especially as you play more games.</li>
                <li>Aim for higher winnings in flipky for each game (up to 500 flipky per win for maximum benefit).
                </li>
                <li>Play regularly: Every game counts, and you get extra bonuses for each 10 games played.</li>
            </ul>
            <p><em>Keep in mind that your win rate is the most significant factor in determining your rating. While the
                amount of flipky you win in each game does contribute to your overall rating, its impact is less
                pronounced than your win rate. Consistency in winning games is the primary path to improving your
                rating, with higher flipky winnings providing a secondary boost. Regular play also contributes to your
                rating, but to a lesser extent than your performance in games.</em></p>

        </div>
    );
};

export default RatingDescription;