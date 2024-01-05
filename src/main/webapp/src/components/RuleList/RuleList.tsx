import React from 'react';
import { Checkbox, Accordion, Group } from '@mantine/core';
import Rule from '../Rule/Rule';
import './RuleList.css';
import { RuleProps } from '../Rule/Rule'


interface RuleListProps {
  rules: RuleProps[];
  value: string[];
  onChange: (value: string[]) => void;
  visibility: string;
}
const RuleList: React.FC<RuleListProps> = ({ rules, value, onChange, visibility }) => {
  return (
    <div className="rulesList" style={{ maxHeight: '600px', overflowY: 'auto' }}>
      <Accordion
        color="violet"
        variant="filled"
        chevronPosition="left"
        defaultValue="customization">
        <Accordion.Item value="customization">
          <Accordion.Control>Правила</Accordion.Control>
          <Accordion.Panel>
            <Checkbox.Group
              value={value}
              onChange={onChange}
              defaultValue={rules.map(r => r.name)}
            >
              <Group>
                {rules.map((ruleProps, index) => (

                  <Rule key={index} {...ruleProps} />
                ))}
              </Group>
            </Checkbox.Group>
          </Accordion.Panel>
        </Accordion.Item>
      </Accordion>
    </div>
  );
};

export default RuleList;
