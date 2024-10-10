import React from 'react';
import {Choice} from "../constants";

const CoinFlipAnimation = ({coinSide, isFlipping}) => {
    return (
        <div className="coin-flip-animation">
            <img
                src={`${process.env.PUBLIC_URL}/images/${coinSide || Choice.heads.toLowerCase()}.png`}
                alt="Flipping coin"
                className={isFlipping ? "flipping-coin" : "result-coin"}
            />
        </div>
    );
};

export default CoinFlipAnimation;