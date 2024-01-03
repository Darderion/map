import React from 'react';
import { useSelector } from 'react-redux';
import { Badge, Checkbox, Group, HoverCard } from '@mantine/core';
import { RootState } from '../../store';
import './Rule.css'; // Import your CSS file where you can define the ruleName class

export type RuleProps = {
  type: string;
  name: string;
};

const Rule: React.FC<RuleProps> = ({ type, name }) => {
  const rulesFull = useSelector((state: RootState) => state.file.ruleSet);

  const rule = rulesFull.find(rule => rule.name === name);
  const description = rule ? rule.description : 'Description not found.';

  return (
    <Group>
      <HoverCard width={300} arrowPosition="side" arrowOffset={10} arrowSize={4} arrowRadius={10} position="right-start" shadow="md">
        <HoverCard.Target>
          <div className='ruleItem'>
            <Badge variant="light" color="violet" size="sm" radius="lg">{type}</Badge>
            <Checkbox className='ruleName' color="violet" value={name} label={name} />
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
