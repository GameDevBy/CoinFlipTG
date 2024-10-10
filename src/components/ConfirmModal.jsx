import React from 'react';

const ConfirmModal = ({ message, onConfirm, onCancel, styleConfirmBtn, styleCancelBtn }) => {
    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <p><strong>{message}</strong></p>
                <div className="modal-buttons">
                    <button className="button" style={styleConfirmBtn} onClick={onConfirm}>Confirm</button>
                    <button className="button" style={styleCancelBtn}  onClick={onCancel} >Cancel</button>
                </div>
            </div>
        </div>
    );
};

export default ConfirmModal;