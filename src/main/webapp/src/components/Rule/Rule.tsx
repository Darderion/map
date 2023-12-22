import React, { FC } from 'react';
import { Checkbox, Badge } from '@mantine/core';
import { HoverCard, Group } from '@mantine/core';
import './Rule.css';

export type RuleProps = {
  type: string;
  name: string;
  description: string;
}
const Rule: React.FC<RuleProps> = ({ type, name, description }) => {

  return (
    <Group>
      <HoverCard
        width={300}
        arrowPosition="side"
        arrowOffset={10}
        arrowSize={4}
        arrowRadius={10}
        position="right-start"
        shadow="md">
        <HoverCard.Target>
          <div className='ruleItem'>
            <Badge variant="light" color="violet" size="sm" radius="lg">{type}</Badge>
            <Checkbox color="violet" value={name} label={name} />
          </div>
        </HoverCard.Target>
        <HoverCard.Dropdown>
          <p dangerouslySetInnerHTML={{ __html: description }}></p>
        </HoverCard.Dropdown>
      </HoverCard>
    </Group>


  );
};

export default Rule;