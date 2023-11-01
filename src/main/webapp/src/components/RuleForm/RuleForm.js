import React, { useState } from 'react';
import { useForm } from '@mantine/form';
import { Button, TextInput, Text, ScrollArea, Checkbox, Group, Accordion } from '@mantine/core';
import { useSelector} from 'react-redux';
import './RuleForm.css';

const RuleForm = ({ create }) => {
  const rulesFul = useSelector((state) => state.file.ruleSet[0]);
  const formRules = []
  rulesFul.map(rule => {
    const curRule = {
      name: `${rule.name}`,
      checked: false
    }
    formRules.push(curRule)
  }
  )
  const form = useForm({
    initialValues: {
      name: '',
      rules: formRules
    },

    validate: {
      name: (value) => (value.length < 2 ? 'Name must have at least 2 letters' : null),
      rules: (value) => value.every(obj => obj.checked === false)
    },
  });

  const [preset, setPreset] = useState({
    id: Date.now(),
    name: '',
    rules: [],
  });

  const savePreset = ({ name, rules }) => {
    const ruleTitles = rules.filter(rule => rule.checked === true).map(rule => rule.name);
    const newPreset = {
      id: Date.now(),
      name: name,
      rules: ruleTitles,
    };

    create(newPreset);
    setPreset({
      id: Date.now(),
      name: '',
      rules: [],
    });
  };

  return (
    <form onSubmit={form.onSubmit(values => savePreset(values))}>
      <TextInput
        placeholder="Rules preset name"
        size="md"
        {...form.getInputProps('name')}
      />

      <div className="rules-container">
        <Text color='dimmed'>Preset must not be empty</Text>
      <ScrollArea h={550} offsetScrollbars scrollbarSize={4}>
      <Accordion style={{width: 'calc(180px + 495 * (100vw / 1280))'}}variant="contained">
        {rulesFul.map((rule, index) => (
              <Accordion.Item
                value={"id" + index}
                >
                <Accordion.Control>
                  <Checkbox
                    color='violet'
                    {...form.getInputProps(`rules.${index}.checked`, { type: 'checkbox' })}
                    label={rule.name}
                    value={"id" + index}
                  />
                </Accordion.Control>

                <Accordion.Panel >
                <p dangerouslySetInnerHTML={{ __html: rule.description }}></p>
                </Accordion.Panel>
              </Accordion.Item>
        ))}
         </Accordion>
         </ScrollArea>
      </div>

      <Group justify="flex-end" mt="md">
        <Button type="submit" color="violet" size="md" id="presetButton">
          Save
        </Button>
      </Group>
    </form>
  );
};

export default RuleForm;