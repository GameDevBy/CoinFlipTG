import React from "react";

const ScoreContent = ({score}) => {
    return (
        <div
            style={{
                display: "flex",
                justifyContent: "center",
                width: "100%",
            }}
        >
            <div
                style={{
                    maxWidth: "300px", // Adjust this value as needed
                    width: "100%",
                    marginBottom: "20px",
                }}
            >
                <h3>Your Score:</h3>
                <div
                    className="score-block"
                    style={{
                        display: "flex",
                        flexDirection: "column",
                        alignItems: "flex-start",
                        textAlign: "left",
                    }}
                >
                    <p>
                        Total played games: <span>{score.playedGamesAmount}</span>
                    </p>
                    <p>
                        Wins: <span>{score.winsAmount}</span>
                    </p>
                    <p>
                        Percent of wins:{" "}
                        <span>
              {(
                  (score.winsAmount /
                      (score.playedGamesAmount ? score.playedGamesAmount : 1)) *
                  100
              ).toFixed(2)}
                            %
            </span>
                    </p>
                    <p>
                        Flipky Balance: <span>{score.flipkyBalance}</span>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default ScoreContent;
