import React, { FC } from 'react';
import { Accordion } from '@mantine/core';
import RuleViolation from '../RuleViolation/RuleViolation';

interface RuleViolationListProps {
  categoryName: string;
  categories: string[];
  violations: any[]; 
}

const RuleViolationList: FC<RuleViolationListProps> = ({ categoryName, categories, violations }) => {
  return (
    <Accordion style={{ maxHeight: '900px', overflowY: 'auto' }}multiple variant="filled" chevronPosition="left">
      {categories.map(category => (
        <RuleViolation violations={violations} categoryName={categoryName} category={category} />
      ))}
    </Accordion>
  );
} 

export default RuleViolationList;