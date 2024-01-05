import React from 'react';
import RuleViolationsHeader from '../components/RuleViolationsHeader/RuleViolationsHeader';
import '../css/processfile.css';

const ProcessFile: React.FC = () => {
    const tabs: string[] = ['Violations'];

    return (
        <div className="ruleViolationsHeader">
            <RuleViolationsHeader tabs={tabs} />
        </div>
    );
}

export default ProcessFile;
