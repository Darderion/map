import React from 'react';
import { Checkbox, Accordion, Group } from '@mantine/core';
import Rule from '../Rule/Rule.js';
import './RuleList.css';

const RuleList = ({ rules, value, onChange, visibility }) => {
  return (
    <div className="rulesList" style={{ visibility: visibility, maxHeight: '600px', overflowY: 'auto', scrollbarWidth: '0' }}>

      <Accordion
        color="violet"
        variant="filled"
        chevronPosition="left"
        defaultValue="customization">
        <Accordion.Item value="customization">
          <Accordion.Control>Rules</Accordion.Control>
          <Accordion.Panel>
            <Checkbox.Group
              value={value} onChange={onChange}
              defaultValue={rules.map(r => r.name)}
            >
              <Group >
                {rules.map(rule =>
                  <Rule rule={rule} />
                )}
              </Group>
            </Checkbox.Group>
          </Accordion.Panel>
        </Accordion.Item>
      </Accordion>
    </div>
  );
};

export default RuleList;