import React from 'react';
import { TextInput } from '@mantine/core';
import { IconSearch } from '@tabler/icons-react';
import './RuleInput.css'

const RuleInput = (props) => {
    
    return(
        <TextInput 
        className="ruleInput"
        icon={<IconSearch size="1.1rem" stroke={1.5}/>}
        size="md"
        placeholder="Search"
        rightSectionWidth={42}
        {...props}
      />
    )
}

export default RuleInput;