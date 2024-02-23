import React, { FC, useMemo, useState, useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { List, Accordion, ScrollArea, ActionIcon } from '@mantine/core';
import RuleViolationList from '../RuleViolationList/RuleViolationsList';
import RuleSelect from '../RuleSelect/RuleSelect';
import RuleList from '../RuleList/RuleList';
import FileView from '../FileView/FileView';
import RuleModal from '../RuleModal/RuleModal';
import FeedbackForm from '../FeedbackForm/FeedbackForm';
import { setCurrentPage, setCurrentLine, setRuleViolations } from '../../reducers/counterReducer';
import './RuleViolationsContent.css';
import { RuleProps } from '../Rule/Rule';
import { RootState } from '../../store';
import { IconAlertHexagon } from '@tabler/icons-react';
import axios from 'axios';

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
  type: string; 
}

interface TargetArrayItem {
  name: string;
  page: number;
  section: string;
  line: number;
  id: string;
}

type listItemRuleViolationType = {
  name: keyof TargetArrayItem;
  displayName: string;
}

const RuleViolationsContent: FC = () => {
  const rulesFull = useSelector((state: RootState) => state.file.ruleSet);
  let currentFile = useSelector((state: RootState) => state.file.currentFileName);
  const dispatch = useDispatch();
  const ruleViolations = useSelector((state: RootState) => state.file.ruleViolations);
  const [selectedItemId, setSelectedItemId] = useState<string | null>(null);
  const apiUrl = useSelector((state: RootState) => state.file.apiUrl);
  const rulesNotFiltered: RuleProps[] = ruleViolations.map((item: RuleViolation) => ({
    name: item.message,
    type: '' 
  }));
  const uniqueRules: RuleProps[] = [];
  const uniqueTitles: string[] = [];
  const pathname = window.location.pathname;
  useEffect(() => {
    if (currentFile == null && pathname.endsWith('=share')) {
      const segments = pathname.split('/');
      currentFile = segments[segments.length - 1].replace('=share', '');
      axios.get(apiUrl + `/getReportByFileName?fileName=${currentFile}`)
        .then((response) => {
          console.log(response.data)
          dispatch(setRuleViolations(response.data.documentReport.ruleViolations));
        })
        .catch((error) => {
          console.error('Error fetching report:', error);
        });
    }
  }, [currentFile, apiUrl, dispatch]);

  rulesNotFiltered.forEach(obj => {
    if (!uniqueTitles.includes(obj.name)) {
      uniqueTitles.push(obj.name);
      uniqueRules.push(obj);
    }
  });

  const rules: Rule[] = rulesFull.filter((item: any) => uniqueRules.some(i => i.name === item.name));
  const targetArray: TargetArrayItem[] = ruleViolations.map((item: RuleViolation) => ({
    name: item.message,
    page: item.lines[0].page,
    section: item.lines[0].area,
    line: item.lines[0].index,
    id: JSON.stringify({
      name: item.message,
      page: item.lines[0].page,
      line: item.lines[0].index,
    }),
  }));

  const options: listItemRuleViolationType[] = [
    { name: 'name', displayName: 'Наименование правила' },
    { name: 'page', displayName: 'Страница' }
  ];

  const [selectedRules, setSelectedRules] = useState<string[]>([...rules].map(r => r.name));
  const [selectedSort, setSelectedSort] = useState<listItemRuleViolationType | null>(null);

  const setSortType = (type: string) => {
    setSelectedSort(options.filter(it => it.displayName === type)[0]);
  }

  const selectedRuleViolations = useMemo(() => {
    return targetArray.sort((a, b) => a.page - b.page)
      .filter(r => selectedRules.includes(r.name));
  }, [selectedRules, targetArray]);

  const categories: string[] = useMemo(() => {
    const categories: string[] = [];

    let selectedKey = selectedSort ? selectedSort.name : '';

    selectedRuleViolations.forEach((violation: any) => {
      if (!categories.includes(violation[selectedKey])) {
        categories.push(violation[selectedKey]);
      }
    });

    return categories;
  }, [selectedSort, selectedRules, selectedRuleViolations]);

  const [modal, setModal] = useState<boolean>(false);

  if (currentFile != null) {
    return (
      <div className="main">
        <div className='ruleList'>
          <RuleList
            value={selectedRules}
            onChange={setSelectedRules}
            rules={uniqueRules}
            visibility={''}
          />
        </div>

        <div className='pageWithSort'>
          <div className="page"><FileView /></div>

          <div className="sortedRuleViolationsList">
            <RuleSelect
              value={selectedSort?.displayName ?? ''}
              onChange={setSortType}
              options={options.map(it => it.displayName)}
              defaultValue={options[0].displayName}
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
                  {(selectedSort == null) ? (
                    <List withPadding spacing="xs">
                      <ScrollArea h={560} type="never" offsetScrollbars scrollbarSize={8} scrollHideDelay={0}>
                        {selectedRuleViolations.map(v => (
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
                                style={{ border: 'solid' }}
                                size="xl"
                                onClick={() => setModal(true)}
                              >
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
                      categoryName={selectedSort.name ?? ''}
                    />
                  )}
                </Accordion.Panel>
              </Accordion.Item>
            </Accordion>
          </div>
        </div>
        <RuleModal visible={modal} setVisible={setModal}>
          <FeedbackForm setModal={setModal} />
        </RuleModal>
      </div>
    );
  }

  return null;
};

export default RuleViolationsContent;
