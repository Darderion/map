import React, { FC, useMemo, useState } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { List, Accordion, ScrollArea, ActionIcon } from '@mantine/core';
import RuleViolationList from '../RuleViolationList/RuleViolationsList';
import RuleSelect from '../RuleSelect/RuleSelect';
import RuleList from '../RuleList/RuleList';
import FileView from '../FileView/FileView';
import RuleModal from '../RuleModal/RuleModal';
import FeedbackForm from '../FeedbackForm/FeedbackForm';
import { setCurrentPage, setCurrentLine } from '../../reducers/counterReducer';
import './RuleViolationsContent.css';
import { RuleProps } from '../Rule/Rule';
import { RootState } from '../../store';
import { IconAlertHexagon } from '@tabler/icons-react';
interface RuleViolation {
  message: string;
  lines: {
    page: number;
    area: string;
    index: number;
  }[];
}

interface Rule {
  name: string;
}

interface TargetArrayItem {
  name: string;
  page: number;
  section: string;
  line: number;
  id: string;
}

const RuleViolationsContent = () => {
  const rulesFull = useSelector((state: RootState) => state.file.ruleSet);
  const dispatch = useDispatch();
  const ruleViolations = useSelector((state: any) => state.file.ruleViolations);
  const [selectedItemId, setSelectedItemId] = useState<string | null>(null);
  const rulesNotFiltered: RuleProps[] = (ruleViolations.length > 0) ? ruleViolations.map((item: RuleViolation) => ({
    name: item.message
  })) : [];
  const uniqueRules: RuleProps[] = [];
  const uniqueTitles: string[] = [];

  rulesNotFiltered.forEach(obj => {
    if (!uniqueTitles.includes(obj.name)) {
      uniqueTitles.push(obj.name);
      uniqueRules.push(obj);
    }
  });
  const rules: Rule[] = rulesFull.filter((item: any) => uniqueRules.some(i => i.name === item.name));
  const targetArray: TargetArrayItem[] = (ruleViolations.length > 0) ? ruleViolations.map((item: RuleViolation) => ({
    name: item.message,
    page: item.lines[0].page,
    section: item.lines[0].area,
    line: item.lines[0].index,
    id: JSON.stringify({
      name: item.message,
      page: item.lines[0].page,
      line: item.lines[0].index,
    }),
  })) : [];
  const options: (keyof TargetArrayItem)[] = ['name', 'page'];

  const [selectedRules, setSelectedRules] = useState<string[]>([...rules].map(r => r.name));
  const [selectedSort, setSelectedSort] = useState<keyof TargetArrayItem | ''>('');

  const selectedRuleViolations = useMemo(() => {
    return targetArray .sort((a, b) => a.page - b.page) 
    .filter(r => selectedRules.includes(r.name));
  }, [selectedRules, targetArray]);

  const categories: string[] = useMemo(() => {
    const categories: string[] = []

    selectedRuleViolations
    .forEach((violation: any) => {
      if (!categories.includes(violation[selectedSort])) {
        categories.push(violation[selectedSort])
      }
    })

    return categories
  }, [selectedSort, selectedRules])
  const currentFile = useSelector((state: RootState) => state.file.currentFile);
  const [modal, setModal] = useState<boolean>(false);

  if (currentFile != null) {
    return (
      <div className="main">
        <div className='ruleList'>
          <RuleList
            value={selectedRules}
            onChange={setSelectedRules}
            rules={uniqueRules} visibility={''} />
        </div>

        <div className='pageWithSort'>
          <div className="page"><FileView /></div>

          <div className="sortedRuleViolationsList">
            <RuleSelect
              value={selectedSort}
              onChange={setSelectedSort as any}
              options={options}
              defaultValue={options[0]}
            />

            <Accordion
              color="violet"
              variant="filled"
              chevronPosition="left"
              defaultValue="customization"
            >
              <Accordion.Item value="customization">
                <Accordion.Control>Нарушения правил</Accordion.Control>
                <Accordion.Panel>
                  {selectedSort === '' ? (
                    <List withPadding spacing="xs">
                      <ScrollArea h={560} type="never" offsetScrollbars scrollbarSize={8} scrollHideDelay={0}>
                        {selectedRuleViolations
                          .map(v => (
                            <List.Item
                              key={v.id}
                              onClick={() => {
                                dispatch(setCurrentPage(v.page));
                                dispatch(setCurrentLine(v.line));
                                setSelectedItemId(v.id);
                              }}
                              className={selectedItemId === v.id ? 'selected-item' : ''}
                              icon={
                                <ActionIcon 
                                style={{border: 'solid'}}
                                size="xl" onClick={() => setModal(true)}>
                                  <IconAlertHexagon />
                                </ActionIcon>
                              }
                            >
                              {v.name}
                            </List.Item>
                          ))}
                      </ScrollArea>
                    </List>
                  ) : (
                    <RuleViolationList
                      violations={selectedRuleViolations}
                      categories={categories}
                      categoryName={selectedSort}
                    />
                  )}
                </Accordion.Panel>
              </Accordion.Item>
            </Accordion>
          </div>
        </div>
        <RuleModal visible={modal} setVisible={setModal}>
          <FeedbackForm setModal={setModal}/>
        </RuleModal>
      </div>
    );
  }

  return null; 
};

export default RuleViolationsContent;
