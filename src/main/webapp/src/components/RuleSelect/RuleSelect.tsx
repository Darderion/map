import React, { FC, ChangeEvent } from 'react';
import { Select } from '@mantine/core';

interface RuleSelectProps {
  options: string[];
  defaultValue: string;
  value: string;
  onChange: (value: string | null) => void;
}

const RuleSelect: FC<RuleSelectProps> = ({ options, defaultValue, value, onChange }) => {
  return (
    <Select
      value={value}
      defaultValue={defaultValue}
      label="Sort"
      placeholder="Pick sort"
      data={options}
      onChange={onChange}
    />
  );
}

export default RuleSelect;