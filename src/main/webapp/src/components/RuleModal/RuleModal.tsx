import React, { FC, ReactNode } from 'react';
import './RuleModal.css';

interface RuleModalProps {
    visible: boolean;
    setVisible: (visible: boolean) => void;
    children: ReactNode;
}

const RuleModal: FC<RuleModalProps> = ({ visible, setVisible, children }) => {
    const rootClasses: string[] = ["ruleForm"];
    if (visible) {
        rootClasses.push("active");
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