import React, {useEffect, useRef, useState} from "react";

const ScoreContent = ({score}) => {
    const [showScore, setShowScore] = useState(false)
    const scoreDialogRef = useRef(null);
    const scoreButtonRef = useRef(null);

    const winsPercent = (score.wins /
            (score.playedGames ? score.playedGames : 1)) *
        100;

    useEffect(() => {
        function handleClickOutside(event) {
            if (event.target.id !== "score-button-id" && scoreDialogRef.current && !scoreDialogRef.current.contains(event.target)) {
                setShowScore(false);
            }
        }

        window.addEventListener("mousedown", handleClickOutside);
        return () => {
            window.removeEventListener("mousedown", handleClickOutside);
        };
    }, []);

    const handleScoreButtonClick = (event) => {
        event.stopPropagation(); // Stop the event from propagating to the document
        setShowScore(!showScore);
    };

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
                    width: "100%",
                    marginBottom: "20px",
                }}
            >
                <div
                    className="score-block"
                >
                    <button
                        id="score-button-id"
                        style={{margin: 0, padding: "5px 10px", width: "inherit"}} className="button"
                        onClick={handleScoreButtonClick}>
                        Score
                    </button>
                    {showScore &&
                        <div onClick={(e) => e.stopPropagation()} ref={scoreDialogRef} className="score-dialog">
                            <div style={{position: "relative"}}>
                                <button
                                    className="dialog-close-button"
                                    onClick={() => setShowScore(false)}
                                >
                                    ×
                                </button>
                                <div style={{
                                    display: "flex",
                                    flexDirection: "column",
                                    justifyContent: "space-between",
                                    width: "100%",
                                }}>
                                    <p>
                                        Games: <span>{score.playedGames}</span>
                                    </p>
                                    <p>
                                        Wins: <span>{score.wins}</span>
                                    </p>
                                    <p>
                                        Losses: <span>{score.losses}</span>
                                    </p>
                                    <p>
                                        Total win flipky: <span
                                        style={{color: "green"}}>{score.totalWinFlipky}</span>
                                    </p>
                                    <p>
                                        Total loss flipky: <span style={{color: "red"}}>{score.totalLossFlipky}</span>
                                    </p>
                                    <p>Wins rate:{" "}
                                        <span style={{
                                            color: winsPercent === 0 ? "inherit" :
                                                winsPercent > 0 ? "green" : "red"
                                        }}>{winsPercent.toFixed(2)}%</span>
                                    </p>
                                </div>
                            </div>
                        </div>}
                    <div>
                        <p style={{margin: 0, padding: 0}}>Flipky: {score.flipkyBalance}</p>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ScoreContent;
