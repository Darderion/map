import React, { FC } from 'react';
import { TextInput } from '@mantine/core';
import { IconSearch } from '@tabler/icons-react';
import './RuleInput.css';

interface RuleInputProps {
    className?: string;
    value?: string;
    size?: string;
    placeholder?: string;
    rightSectionWidth?: number;
    onChange: (e: React.ChangeEvent<HTMLInputElement>) => void
}

const RuleInput: FC<RuleInputProps> = (props) => {
    return (
        <TextInput
            className="ruleInput"
            size="md"
            placeholder="Search"
            rightSectionWidth={42}
            {...props}
        />
    );
}

export default RuleInput;