import React, { useState } from 'react';
import { useParams } from 'react-router-dom';
import { Container, Tabs, Button } from '@mantine/core';
import RuleViolationsContent from '../RuleViolationsContent/RuleViolationsContent';
import Statistics from '../Statistics/Statistics';
import Sections from '../Sections/Sections';

interface RuleViolationsHeaderProps {
  tabs: string[];
}

const RuleViolationsHeader: React.FC<RuleViolationsHeaderProps> = ({ tabs }) => {
  const { tabValue } = useParams<{ tabValue?: string }>();
  const [copied, setCopied] = useState(false);

  const headerStyles: React.CSSProperties = {
    paddingTop: '0.5rem',
    backgroundColor: 'white',
    borderBottom: '1px solid #ccc',
    marginBottom: '0',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
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

  const handleCopyLink = () => {
    const url = window.location.href + "=share";
    navigator.clipboard.writeText(url)
      .then(() => {
        setCopied(true);
        setTimeout(() => setCopied(false), 2000);
      })
      .catch((error) => {
        console.error('Failed to copy link:', error);
      });
  };

  return (
    <div style={headerStyles}>
      <Container>
        <Tabs defaultValue="Нарушения" variant="outline" value={tabValue}>
          <Tabs.List>{items}</Tabs.List>


            <Tabs.Panel value="Нарушения">
              <RuleViolationsContent />
            </Tabs.Panel>
            <Tabs.Panel value="Статистика">
              <Statistics />
            </Tabs.Panel>
            <Tabs.Panel value="Секции">
              <Sections />
            </Tabs.Panel>
            <Button onClick={handleCopyLink} variant="outline" size="sm">
        {copied ? 'Скопировано!' : 'Поделиться работой'}
      </Button>
        </Tabs>
      </Container>
    
    </div>
  );
};

export default RuleViolationsHeader;
