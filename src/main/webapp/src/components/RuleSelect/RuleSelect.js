import React from 'react';
import { Select } from '@mantine/core';

const RuleSelect = ({options, defaultValue, value, onChange}) => {
    return(
        <Select
          value={value}
          defaultValue={defaultValue}
          label="Sort"
          placeholder="Pick sort"
          data={options}
          onChange={onChange}
        />        
    )
}
export default RuleSelect;