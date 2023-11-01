import React from 'react';
import './RuleModal.css'

const RuleModal = ({ children, visible, setVisible }) => {
    const rootClasses = ["ruleForm"]
    if (visible) {
        rootClasses.push("active")
    }
    return (
        <div className={rootClasses.join(' ')} onClick={() => setVisible(false)}>
            <div className="ruleFormContent" onClick={(e) => e.stopPropagation()}>
                {children}
            </div>
        </div>
    );
};

export default RuleModal;

