import React from 'react';
import { useParams } from 'react-router-dom';
import { Container, Tabs } from '@mantine/core';
import RuleViolationsContent from '../RuleViolationsContent/RuleViolationsContent';
import Statistics from '../Statistics/Statistics';
import Sections from '../Sections/Sections';

interface RuleViolationsHeaderProps {
  tabs: string[];
}

const RuleViolationsHeader: React.FC<RuleViolationsHeaderProps> = ({ tabs }) => {
  const { tabValue } = useParams<{ tabValue?: string }>();

  const headerStyles: React.CSSProperties = {
    paddingTop: '0.5rem',
    backgroundColor: 'white',
    borderBottom: '1px solid #ccc',
    marginBottom: '0',
  };

  const tabStyles: React.CSSProperties = {
    fontWeight: 500,
    height: '2.375rem',
    backgroundColor: 'transparent',

  };

  const items = tabs.map((tab) => (
    <Tabs.Tab value={tab} key={tab} style={tabStyles}>
      {tab}
    </Tabs.Tab>
  ));

  return (
    <div style={headerStyles}>
      <Container>
        <Tabs
          defaultValue="Нарушения"
          variant="outline"
          value={tabValue }
        >
          <Tabs.List>{items}</Tabs.List>
          <Tabs.Panel value="Нарушения">
            <RuleViolationsContent />
          </Tabs.Panel>
          <Tabs.Panel value="Статистика">
            <Statistics/>
          </Tabs.Panel>
          <Tabs.Panel value="Секции">
            <Sections/>
          </Tabs.Panel>
        </Tabs>
      </Container>
    </div>
  );
};

export default RuleViolationsHeader;
