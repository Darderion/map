import React from 'react';
import { Checkbox, Badge } from '@mantine/core';
import { HoverCard, Group } from '@mantine/core';
import './Rule.css'

const Rule = ({ rule }) => {

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
            <Badge variant="light" color="violet" size="sm" radius="lg">{rule.type}</Badge>
            <Checkbox color="violet" value={rule.name} label={rule.name} />
          </div>
        </HoverCard.Target>
        <HoverCard.Dropdown>
        <p dangerouslySetInnerHTML={{ __html: rule.description }}></p>
        </HoverCard.Dropdown>
      </HoverCard>
    </Group>


  );
};

export default Rule;
