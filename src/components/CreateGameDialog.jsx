import React from 'react';
import {choices} from "../constants";

const CreateGameDialog = ({
                              score,
                              choice,
                              handleChoiceChange,
                              bet,
                              handleBetChange,
                              setOpenModal,
                              buttonAction,
                              buttonText
                          }) => {
    return (
        <div>
            <div
                style={{
                    display: "flex",
                    flexDirection: "column",
                    justifyContent: "center",
                    alignItems: "center",
                    padding: "20px",
                    gap: "15px",
                    position: "relative"
                }}
            >
                <button
                    className="dialog-close-button"
                    onClick={setOpenModal}
                >
                    ×
                </button>
                <input
                    style={{
                        width: "150px",
                        border: "1px dashed gray",
                        borderRadius: "15px",
                        paddingLeft: "10px",
                    }}
                    min={1}
                    max={+score.flipkyBalance}
                    type="text"
                    inputMode="numeric"
                    pattern="[0-9]*"
                    placeholder="Enter your bet"
                    value={bet}
                    onChange={handleBetChange}
                />
                <div className="choice-radios">
                    {choices.map((option) => (
                        <div key={option} className="radio-container">
                            <input
                                type="radio"
                                id={option}
                                name="choice"
                                value={option}
                                checked={choice === option}
                                onChange={() => handleChoiceChange(option)}
                            />
                            <label htmlFor={option}>{option}</label>
                        </div>
                    ))}
                </div>
                <button
                    className="button"
                    style={{
                        width: "150px",
                    }}
                    disabled={!bet || !choice || bet > score.flipkyBalance}
                    onClick={() => {
                        if (bet && choice) {
                            buttonAction();
                        }
                    }}
                >
                    {buttonText}
                </button>
            </div>
        </div>
    );
};

export default CreateGameDialog;