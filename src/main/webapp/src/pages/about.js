import React from 'react';
import { Card, Group, Text, Button } from '@mantine/core';
import { GithubIcon } from '@mantine/ds';
import '../css/about.css'
const About = () => {
  return (
    <div className='aboutPage'>
      <Card shadow="sm" padding="lg" radius="md" withBorder>

        <Group position="apart" mt="md" mb="xs">
          <Text className='label'>Contacts</Text>
        </Group>

        <Text color="dimmed">
          <Text className='lead'>For any suggestions, bug reports and questions: </Text>
          <Text className='lead'>Discord: Darderion#8682 </Text>
          <Text className='lead'>Mail: Eobard1SA@gmail.com </Text>
        </Text>

        <a href="https://github.com/darderion/map" target="_blank" rel="noreferrer" >
          <Button leftIcon={<GithubIcon size={20} />} className="githubButton" 
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