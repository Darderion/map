import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Container, Tabs } from '@mantine/core';
import '../../css/App.css';

const Menu = () => {
  const navigate = useNavigate();
  const location = useLocation();

  function navigateTo(path: string): void {
    navigate(path);
  }

  const activeTab = location.pathname.split('/')[1] || 'Home';

  return (
    <Container className="menu">
      <Tabs
        defaultValue="Home"
        color="red"
        value={activeTab}
      >
        <Tabs.List>
          <Tabs.Tab value="Home" onClick={() => navigateTo('/Home')}>Home</Tabs.Tab>
          <Tabs.Tab value="About" onClick={() => navigateTo('/About')}>About</Tabs.Tab>
          <Tabs.Tab value="Presets" onClick={() => navigateTo('/Presets')}>RULES SETUP</Tabs.Tab>
          <Tabs.Tab value="Download" onClick={() => navigateTo('/Download')}>UPLOAD FILES</Tabs.Tab>
        </Tabs.List>
      </Tabs>
    </Container>
  );
};

export default Menu;
