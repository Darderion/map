import React, { FC } from 'react';
import { Radio } from '@mantine/core';
import { Accordion, Text, Title, List } from '@mantine/core';
import './Preset.css'
import { setCurrentPreset } from '../../reducers/counterReducer';
import { useDispatch } from 'react-redux';

interface PresetProps {
    preset: {
        id: string;
        name: string;
        rules: string[];
    };
    visible: boolean;
}

const Preset: FC<PresetProps> = ({ preset, visible }) => {
    const listVisibility: string = visible ? "hidden" : "";
    const dispatch = useDispatch();

    return (
        <Accordion.Item
            className="preset"
            value={"id" + preset.id}
        >
            <Accordion.Control>
                <Radio
                    color="violet"
                    value={"radio" + preset.id}
                    label={preset.name}
                    onClick={() => {
                        dispatch(setCurrentPreset(preset.rules));
                    }}
                />
            </Accordion.Control>

            <Accordion.Panel >
                <Title
                    order={4}>Rules
                </Title>

                <List color="dimmed" withPadding>
                    {preset.rules.map((str: string) => (
                        <Text color="dimmed">{str} </Text>
                    ))}
                </List>

            </Accordion.Panel>
        </Accordion.Item>
    );
}

export default Preset;