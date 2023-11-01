import React from 'react';
import RuleViolationsHeader from '../components/RuleViolationsHeader/RuleViolationsHeader';
import '../css/processfile.css'

const ProcessFile = () => {
    const tabs = ['Violations','Statistics'] /**add 'Statistics' */

    return(
        <div className="ruleViolationsHeader">
            <RuleViolationsHeader tabs={tabs} />
        </div>
    )
}

export default ProcessFile;