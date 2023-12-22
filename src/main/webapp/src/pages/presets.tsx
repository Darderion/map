import React, { useState, useMemo, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Button } from '@mantine/core';
import RuleInput from '../components/RuleInput/RuleInput';
import RuleForm from '../components/RuleForm/RuleForm';
import RuleModal from '../components/RuleModal/RuleModal';
import PresetList from '../components/PresetsList/PresetList';
import ListContainer from '../components/ListContainer/ListContainer';
import { useSelector } from 'react-redux';
import { RootState } from '../store';

type Preset = {
    id: number;
    name: string;
    rules: string[];
};

const Presets: React.FC = () => {
    const [presets, setPresets] = useState<Preset[]>([
        {
            id: 1,
            name: "Правила про ссылки",
            rules: [
                "Ссылка на низкокачественную конференцию",
                "Неверный порядок ссылок на литературу",
                "Ссылки разных видов",
                "Нельзя пользоваться url-сокращателями. Ведь если сайт исчезнет или потеряет базу данных то работа потеряет в читабельности."
            ],
        },
    ]);

    useEffect(() => {
        const presetsData = localStorage.getItem('presets');
        if (presetsData) setPresets(JSON.parse(presetsData));
    }, []);


    const [modal, setModal] = useState<boolean>(false);
    const [searchQuery, setSearchQuery] = useState<string>('');

    const selectedPresets = useMemo(() => presets.filter(preset => preset.name.toLowerCase().includes(searchQuery.toLowerCase())), [searchQuery, presets]);

    const createPreset = (newPreset: Preset) => {
        const newPresets = [...presets, newPreset];
        setPresets(newPresets);
        localStorage.setItem('presets', JSON.stringify(newPresets));
        setModal(false);
    };
    const currentPreset = useSelector((state: RootState) => state.file.currentPreset);
    const searchVisibility = modal ? "hidden" : "";

    const buttons: React.ReactNode[] = [
        <Link to="/Download" key="download">
            <Button
                className="listButton"
                color="violet"
                size="md"
            >
                Go to upload
            </Button>
        </Link>,

        <Button
            key="add-preset"
            className="listButton"
            onClick={() => setModal(true)}
            color="violet"
            size="md"
        >
            Add Preset
        </Button>,
    ];

    const search = (
        <RuleInput
            key="search-input"
            value={searchQuery}
            onChange={(e: React.ChangeEvent<HTMLInputElement>) => setSearchQuery(e.target.value)}
        />
    );

    const presetsList = <PresetList key="presets-list" presets={selectedPresets} visible={modal} />;

    return (
        <div>
            <ListContainer
                title="Rule Presets"
                buttons={buttons}
                search={search}
                list={presetsList}
            />
            <RuleModal visible={modal} setVisible={setModal}>
                <RuleForm create={createPreset} />
            </RuleModal>
        </div>
    );
};

export default Presets;
