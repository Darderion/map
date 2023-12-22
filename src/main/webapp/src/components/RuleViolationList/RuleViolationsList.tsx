import React, { FC } from 'react';
import { Accordion } from '@mantine/core';
import RuleViolation from '../RuleViolation/RuleViolation';

interface RuleViolationListProps {
  categoryName: string;
  categories: string[];
  violations: any[]; // Replace with the actual type of violations
}

const RuleViolationList: FC<RuleViolationListProps> = ({ categoryName, categories, violations }) => {
  return (
    <Accordion multiple variant="filled" chevronPosition="left">
      {categories.map(category => (
        <RuleViolation violations={violations} categoryName={categoryName} category={category} />
      ))}
    </Accordion>
  );
}

export default RuleViolationList;