import React from 'react';
import { Accordion } from '@mantine/core'; 
import RuleViolation from '../RuleViolation/RuleViolation';

const RuleViolationList = ({ categoryName, categories, violations }) => {

  return ( 
      <Accordion multiple variant="filled" chevronPosition="left">
        {categories.map(category =>
          <RuleViolation violations={violations} categoryName={categoryName} category={category} />
        )}
      </Accordion>
  )
}

export default RuleViolationList
