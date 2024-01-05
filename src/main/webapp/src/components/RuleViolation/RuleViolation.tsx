import React, { useState } from 'react';
import { List, Accordion, Text } from '@mantine/core';
import { useDispatch, useSelector } from 'react-redux';
import { setCurrentPage, setCurrentLine, setCurrentItem } from '../../reducers/counterReducer';
import './RuleViolation.css';
import { ActionIcon } from '@mantine/core';
import { IconAlertHexagon } from '@tabler/icons-react';
import RuleModal from '../RuleModal/RuleModal'
import FeedbackFrom from '../FeedbackForm/FeedbackForm';
import { RootState } from '../../store';

interface RuleViolationProps {
  categoryName: string;
  category: string;
  violations: Array<any>;
}

const RuleViolation: React.FC<RuleViolationProps> = ({ categoryName, category, violations }) => {
  const dispatch = useDispatch();
  const selectedItemId = useSelector((state: RootState) => state.file.selectedItem);

  const [modal, setModal] = useState(false)



  return (
    <Accordion.Item value={ category}>
      <Accordion.Control>
        {} {category}
      </Accordion.Control>
      <Accordion.Panel>

        <Text style={{ maxHeight: '300px', overflowY: 'auto' }}>
          <List withPadding spacing="xs">
            {violations
              .filter((v: any) => category === v[categoryName])
              .map((violation: any) => (
                <List.Item
                  onClick={() => {

                    dispatch(setCurrentPage(violation.page));
                    dispatch(setCurrentLine(violation.line));
                    dispatch(setCurrentItem(violation.id));
                  }}
                  className={selectedItemId === violation.id ? 'selected-item' : ''}
                  icon={
                    <ActionIcon
                      style={{ border: 'solid' }}
                      size="xl" onClick={() => setModal(true)}>
                      <IconAlertHexagon />
                    </ActionIcon>
                  }
                >


                  {violation.name}
                  <RuleModal visible={modal} setVisible={setModal}>
                    <FeedbackFrom />
                  </RuleModal>
                </List.Item>
              ))}
          </List>
        </Text>

      </Accordion.Panel>
    </Accordion.Item>
  );
}

export default RuleViolation;