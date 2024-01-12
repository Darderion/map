import React, { FC } from 'react';
import { Card, Group, Text, Button } from '@mantine/core';
import '../css/about.css';

const About: FC = () => {
  return (
    <div className='aboutPage'>
      <Card shadow="sm" padding="lg" radius="md" withBorder>

        <Group mt="md" mb="xs">
          <Text className='label'>Контакты</Text>
        </Group>

        <Text color="dimmed">
          <Text className='lead'>Любые предложения, сообщения об ошибках и вопросы: </Text>
          <Text className='lead'>Discord: Darderion </Text>
          <Text className='lead'>Mail: Eobard1SA@gmail.com </Text>
        </Text>

        <a href="https://github.com/darderion/map" target="_blank" rel="noreferrer" >
          <Button className="githubButton"
            size="lg"
            variant="light"
            color="violet"
            fullWidth>
            Github
          </Button>
        </a>

      </Card>

    </div>
  );
};

export default About;