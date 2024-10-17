import React from 'react';
import {Choice} from "../constants";

const CoinFlipAnimation = ({coinSide, isFlipping}) => {
    return (
        <div className="coin-flip-animation">
            <div className={`coin ${isFlipping ? 'flipping' : ''}`}>
                <div className="side heads">
                    <img src={`${process.env.PUBLIC_URL}/images/heads.png`} alt="Heads"/>
                </div>
                <div className="side tails">
                    <img src={`${process.env.PUBLIC_URL}/images/tails.png`} alt="Tails"/>
                </div>
            </div>
        </div>
    );
};

export default CoinFlipAnimation;