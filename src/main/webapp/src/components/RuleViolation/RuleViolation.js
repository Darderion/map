import React, { useState } from 'react';
import { List, Accordion, Text } from '@mantine/core';
import { useDispatch, useSelector } from 'react-redux';
import { setCurrentLine, setCurrentPage, setSelectedItem } from '../../actions/fileAction';
import './RuleViolation.css';
import { ActionIcon } from '@mantine/core';
import { IconAlertHexagon } from '@tabler/icons-react';
import RuleModal from '../RuleModal/RuleModal'
import FeedbackFrom from '../FeedbackForm/FeedbackForm';

function RuleViolation({ categoryName, category, violations }) {
  const dispatch = useDispatch();
  const selectedItemId = useSelector((state) => state.file.selectedItem);

  const [modal, setModal] = useState(false)

  return (
    <Accordion.Item value={categoryName + category}>
      <Accordion.Control>
        {categoryName} {category}
      </Accordion.Control>
      <Accordion.Panel>

        <Text style={{ maxHeight: '300px', overflowY: 'auto' }}>
          <List withPadding spacing="xs">
            {violations
              .filter((v) => category === v[categoryName])
              .map((violation) => (
                <List.Item
                  onClick={() => {
                    dispatch(setSelectedItem(null));
                    dispatch(setCurrentLine(violation.line));
                    dispatch(setCurrentPage(violation.page));
                    dispatch(setSelectedItem(violation.id));
                  }}
                  className={selectedItemId === violation.id ? 'selected-item' : ''}
                  icon ={ 
                    <ActionIcon 
                    style={{border: 'solid'}}
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