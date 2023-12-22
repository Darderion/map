import React, { FC } from 'react';
import { Radio, Accordion } from '@mantine/core';
import Preset from '../Preset/Preset';
import './PresetsList.css';

interface PresetListProps {
    presets: Array<any>;
    visible: boolean;
}

const PresetList: FC<PresetListProps> = ({ presets, visible }) => {
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