import React, { useState } from 'react';
import { useForm } from '@mantine/form';
import { Button, TextInput, Text, ScrollArea, Checkbox, Group, Accordion } from '@mantine/core';
import { useSelector } from 'react-redux';
import './RuleForm.css';
import { RootState } from '../../store';
interface Rule {
  description: string;
  name: string;
  checked: boolean;
}

interface Preset {
  id: number;
  name: string;
  rules: string[];
}

interface RuleFormProps {
  create: (preset: Preset) => void;
}

const RuleForm: React.FC<RuleFormProps> = ({ create }) => {
  const rulesFul = useSelector((state: RootState) => state.file.ruleSet);

  const form = useForm({
    initialValues: {
      name: '',
      rules: rulesFul.map(rule => ({ ...rule, checked: false })),
    },
    validate: {
      name: (value: string) => (value.length < 2 ? 'Name must have at least 2 letters' : null),
    },
  });

  const savePreset = (values: { name: string; rules: Rule[] }) => {
    const ruleNames = values.rules.filter(rule => rule.checked).map(rule => rule.name);

    const newPreset: Preset = {
      id: Date.now(),
      name: values.name,
      rules: ruleNames,
    };

    create(newPreset);

    form.reset();
  };

  return (
    <form onSubmit={form.onSubmit((values) => savePreset(values))}>
      <TextInput
        placeholder="Rules preset name"
        size="md"
        {...form.getInputProps('name')}
      />

      <div className="rules-container">
        <Text color="dimmed">Select at least one rule</Text>
        <ScrollArea style={{ height: 550 }} offsetScrollbars scrollbarSize={4}>
          <Accordion style={{ width: '100%' }} variant="contained">
            {form.values.rules.map((rule: Rule, index: number) => (
              <Accordion.Item key={index} value={"id" + index}>
                <Accordion.Control>
                  <Checkbox
                    color="violet"
                    {...form.getInputProps(`rules.${index}.checked`, { type: 'checkbox' })}
                    label={rule.name}
                  />
                </Accordion.Control>
                <Accordion.Panel>
                  <p dangerouslySetInnerHTML={{ __html: rule.description }}></p>
                </Accordion.Panel>
              </Accordion.Item>
            ))}
          </Accordion>
        </ScrollArea>
      </div>

      <Group mt="md">
        <Button type="submit" color="violet" size="md" id="presetButton">
          Save Preset
        </Button>
      </Group>
    </form>
  );
};

export default RuleForm;
