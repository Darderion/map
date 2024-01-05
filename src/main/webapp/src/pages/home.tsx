import React from 'react';
import { Link } from 'react-router-dom';
import { resetVariable } from '../reducers/counterReducer';
import { Container, Text, Title, Button, Group } from '@mantine/core';
import { Accordion } from '@mantine/core';
import '../css/home.css'
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../store';

const Home = () => {
  const rulesFull = useSelector((state: RootState) => state.file.ruleSet);

  const dispatch = useDispatch();
  const handleReset = () => {
    sessionStorage.setItem("tabs", "Presets");
    dispatch(resetVariable());
  };

  return (
    <div className='homePage'>
      <div className="homeHeader">
        <Container size={700} className="homeText">
          <Title className="name">
            Приложение{' '}<br />
            <Text component="span" color="violet">
              MAP
            </Text>{' '}<br />-
            это инструмент<br /> для проверки студенческих <br />работ
          </Title>

          <Text className="description" color="dimmed">
          "Map" сканирует текст работы и выделяет
            проблемные участки. Таким образом, студенты получают
            немедленную обратную связь и имеют возможность
            улучшить свои навыки письма.
            Приложение также предоставляет статистику работы.
            "Map" анализирует параметры, такие как частотность слов,
            использование академической лексики и структуру текста.
						Эта информация позволяет студентам
            лучше понимать свои слабые стороны и
            сосредоточиться на их улучшении.
          </Text>

          <Group>
            <Link
              to="/Presets"
            ><Button
              size="xl"
              color="violet"
              onClick={handleReset}>
                Начать
              </Button></Link>
          </Group>

        </Container>



      </div>
      <Title style={{ paddingTop: '200px', paddingBottom: '30px' }}>Правила</Title>
      <div className='list'>
        <Accordion style={{ width: 'calc(180px + 495 * (100vw / 1280))' }} variant="default">
          {rulesFull.map((rule: { name: string | number | boolean | React.ReactElement<any, string | React.JSXElementConstructor<any>> | Iterable<React.ReactNode> | React.ReactPortal | null | undefined; description: any; }, index: number) => (
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
