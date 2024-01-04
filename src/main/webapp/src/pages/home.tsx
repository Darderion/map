import React from 'react';
import { Link } from 'react-router-dom';
import { resetVariable } from '../reducers/counterReducer';
import { Container, Text, Title, Button, Group } from '@mantine/core';
import { Accordion } from '@mantine/core';
import '../css/home.css'
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../store';

const Home = () => {
  const rulesFul = useSelector((state: RootState) => state.file.ruleSet);

  const dispatch = useDispatch();
  const handleReset = () => {
    sessionStorage.setItem("tabs", "Presets");
    dispatch(resetVariable());
  };
  console.log(rulesFul)
  return (
    <div className='homePage'>
      <div className="homeHeader">
        <Container size={700} className="homeText">
          <Title className="name">
            The application{' '}<br />
            <Text component="span" color="violet">
              MAP
            </Text>{' '}<br />
            is a tool designed<br /> to check student's <br />work
          </Title>

          <Text className="description" color="dimmed">
            "Map" scans the text of the paper and highlights
            problematic areas. This way, students receive
            immediate feedback and have the opportunity
            to enhance their writing skills.
            The application provides statistics of work.
            "Map" analyzes parameters such as word frequency,
            use of academic vocabulary,
            and text structure. This information allows students
            to better understand their weaknesses and
            focus on improving.
          </Text>

          <Group>
            <Link
              to="/Presets"
            ><Button
              size="xl"
              color="violet"
              onClick={handleReset}>
                Get started
              </Button></Link>
          </Group>

        </Container>



      </div>
      <Title style={{ paddingTop: '200px', paddingBottom: '30px' }}>Rules</Title>
      <div className='list'>
        <Accordion style={{ width: 'calc(180px + 495 * (100vw / 1280))' }} variant="default">
          {rulesFul.map((rule: { name: string | number | boolean | React.ReactElement<any, string | React.JSXElementConstructor<any>> | Iterable<React.ReactNode> | React.ReactPortal | null | undefined; description: any; }, index: number) => (
            <Accordion.Item
              value={"id" + index}
            >
              <Accordion.Control>
                <Text>{rule.name}</Text>
              </Accordion.Control>
              <Accordion.Panel>
                <p dangerouslySetInnerHTML={{ __html: rule.description }}></p>
              </Accordion.Panel>
            </Accordion.Item>
          ))}
        </Accordion>

      </div>

    </div>
  );
};

export default Home;
