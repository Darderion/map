import React from 'react';
import { useHistory , useParams} from 'react-router-dom'
import { Container, Tabs, createStyles, rem } from '@mantine/core';
import '../../css/App.css';


const Menu = () => {
  const useStyles = createStyles((theme) => ({
  mainLink: {
    textTransform: 'uppercase',
    fontSize: rem(18),
    color: theme.colors.gray[6],
    padding: `${rem(7)} ${theme.spacing.sm}`,
    fontWeight: 700,
    borderBottom: `${rem(2)} solid transparent`,
    transition: 'border-color 100ms ease, color 100ms ease',
  
    '&:hover': {
      color: theme.black,
      textDecoration: 'none',
    },
  }
  }))
  const history = useHistory();
  function navigateTo(url) {
    history.push(url);
  }
  const { classes } = useStyles();
  const { tabValue } = useParams();
    return (
      <Container className="menu">
        <Tabs defaultValue={`${sessionStorage.getItem("tabs")}`} 
        color="violet" 
        value={tabValue}
        onTabChange={(value) => {
          sessionStorage.setItem("tabs", value);
          navigateTo(`/${value}`)}}>
        <Tabs.List>
        <Tabs.Tab className={classes.mainLink} value="Home">HOME</Tabs.Tab>
          <Tabs.Tab className={classes.mainLink} value="About">ABOUT</Tabs.Tab>
         <Tabs.Tab className={classes.mainLink} value="Presets">RULES SETUP</Tabs.Tab>
          <Tabs.Tab className={classes.mainLink} value="Download">UPLOAD FILES</Tabs.Tab>
        </Tabs.List>
       </Tabs>
    
      </Container>
    ) 
};

export default Menu;