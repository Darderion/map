import { useParams } from 'react-router-dom'
import { createStyles, Container, Tabs, rem } from '@mantine/core';
import RuleViolationsContent from '../RuleViolationsContent/RuleViolationsContent';
import Statistics from '../Statistics/Statistics';

const RuleViolationsHeader = ({ tabs }) => {
  const useStyles = createStyles((theme) => ({
    header: {
      paddingTop: theme.spacing.sm,
      backgroundColor: theme.colorScheme === theme.colors.white,
      borderBottom: `${rem(1)} solid ${theme.colorScheme === theme.colors.gray[2]
        }`,
      marginBottom: rem(0),
    },
  
    tabsList: {
      borderBottom: '0 !important',
    },
  
    tab: {
      fontWeight: 500,
      height: rem(38),
      backgroundColor: 'transparent',
  
      '&:hover': {
        backgroundColor: theme.colors.gray[1],
      },
  
      '&[data-active]': {
        backgroundColor: theme.white,
        borderColor: theme.colors.gray[2],
      },
    },
  }));
  
  const classes = useStyles();
  const { tabValue } = useParams();

  const items = tabs.map((tab) => (
    <Tabs.Tab value={tab} key={tab}>{tab}</Tabs.Tab>
  ));

  return (
    <div className={classes.header}>
      <Container>
        <Tabs
          defaultValue="Violations"
          variant="outline"
          classNames={{
            root: classes.tabs,
            tabsList: classes.tabsList,
            tab: classes.tab,
          }}
          value={tabValue}
        >
          <Tabs.List>{items}</Tabs.List>

          <Tabs.Panel value="Violations">
            <RuleViolationsContent/>
          </Tabs.Panel>

          <Tabs.Panel value="Statistics">
            <Statistics/>
        </Tabs.Panel>

        </Tabs>
      </Container>
    </div>
  );
}

export default RuleViolationsHeader;
