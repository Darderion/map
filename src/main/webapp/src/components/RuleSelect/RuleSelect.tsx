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
      label="Сортировать"
      placeholder="поле"
      data={options}
      onChange={onChange}
    />
  );
}

export default RuleSelect;