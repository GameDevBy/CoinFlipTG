import React from 'react';

const ConfirmModal = ({ message, onConfirm, onCancel }) => {
    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <p><strong>{message}</strong></p>
                <div className="modal-buttons">
                    <button className="button" style={{background: "#ff4d4d"}} onClick={onConfirm}>Confirm</button>
                    <button className="button" style={{background: "#46b449"}}  onClick={onCancel} >Cancel</button>
                </div>
            </div>
        </div>
    );
};

export default ConfirmModal;