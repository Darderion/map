import React from 'react';
import { Radio, Accordion } from '@mantine/core';
import Preset from '../Preset/Preset';
import './PresetsList.css';
const PresetList = ({ presets, visible }) => {
    return (
        <div className="presetsList" >
            <Accordion multiple variant="contained">
                <Radio.Group>
                    {presets.map(preset => (
                        <Preset preset={preset} visible={visible} key={preset.id} />
                    ))}
                </Radio.Group>
            </Accordion>
        </div>
    );
};

export default PresetList;