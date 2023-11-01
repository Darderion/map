import React, { useMemo, useState } from 'react';
import { useSelector } from 'react-redux';
import { List, Accordion, ScrollArea } from '@mantine/core';
import RuleViolationList from '../RuleViolationList/RuleViolationsList';
import RuleSelect from '../RuleSelect/RuleSelect';
import RuleList from '../RuleList/RuleList';
import FileView from '../FileView/FileView';
import './RuleViolationsContent.css'
import { useDispatch } from 'react-redux';
import { setCurrentLine,setCurrentPage, setSelectedItem} from '../../actions/fileAction';
import { ActionIcon } from '@mantine/core';
import { IconAlertHexagon } from '@tabler/icons-react';
import RuleModal from '../RuleModal/RuleModal'
import FeedbackFrom from '../FeedbackForm/FeedbackForm';

const RuleViolationsContent = () => {
  const rulesFul = useSelector((state) => state.file.ruleSet[0]);
  const dispatch = useDispatch();
  const ruleViolations = useSelector((state)=> state.file.ruleViolations);
  const [selectedItemId, setSelectedItemId] = useState(null);
  const rulesNotFiltred = (ruleViolations.length > 0) ? ruleViolations[0].data.map(item =>({
    name: item.message
  })) : []
  const uniqueRules = [];
  const uniqueTitles = [];
  
  rulesNotFiltred.forEach(obj => {
    const name = obj.name;
    if (!uniqueTitles.includes(name)) {
        uniqueTitles.push(name);
        uniqueRules.push(obj);
    }
});
  const rules = rulesFul.filter(item => uniqueRules.some(i => i.name === item.name));
  const targetArray = (ruleViolations.length > 0) ? ruleViolations[0].data.map(item => ({
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

  const options = ['name', 'page']

  const [selectedRules, setSelectedRules] = useState([...rules].map(r => r.name))
  const [selectedSort, setSelectedSort] = useState('')

  const selectedRuleViolations = useMemo(() => {
     return targetArray
     .filter(r => selectedRules.includes(r["name"]))
  }, [selectedRules]
  );

  const categories = useMemo(() => {
    const categories = []

    selectedRuleViolations
    .forEach(violation => {
      if (!categories.includes(violation[selectedSort])) {
        categories.push(violation[selectedSort])
      }
    })

    return categories
  }, [selectedSort, selectedRules])

  const currentFile = useSelector((state) => state.file.currentFile);
  const [modal, setModal] = useState(false)

  if (currentFile != null) {
    return (
      <div className="main">
      
       <div className='ruleList'>
          <RuleList 
          value={selectedRules} 
          onChange={setSelectedRules}
          rules={rules}/>
        </div>
          
        <div className='pageWithSort'>
          
        <div className="page"><FileView/></div>

        <div className="sortedRuleViolationsList">
          <RuleSelect
            value={selectedSort}
            onChange={setSelectedSort}
            options={[...options]}
            defaultValue={options[0]} />

           <Accordion
              color="violet"
              variant="filled"
              chevronPosition="left"
              defaultValue="customization">
              <Accordion.Item value="customization">
                <Accordion.Control>Rule Violations</Accordion.Control>
                <Accordion.Panel>
          {selectedSort === '' ? <List withPadding spacing="xs" >
          <ScrollArea h={560} type="never" offsetScrollbars scrollbarSize={8} scrollHideDelay={0}>
            
            {selectedRuleViolations
            .map(v => <List.Item  
              onClick={()=>{dispatch(setCurrentLine(v.line));
              dispatch(setCurrentPage(v.page)); 
              dispatch(setSelectedItem(v.name))
               setSelectedItemId(v.id);}}  
               className={selectedItemId === v.id? 'selected-item' : ''}
                 icon ={ 
                   <ActionIcon 
                   style={{border: 'solid'}}
                   size="xl" onClick={() => setModal(true)}>
                     <IconAlertHexagon />
                   </ActionIcon>
                 }
                 >
                {v.name}
                </List.Item>)}
              </ScrollArea>
          </List> :
            <RuleViolationList
              violations={selectedRuleViolations}
              categories={categories}
              categoryName={selectedSort}
            />
          }
              </Accordion.Panel>
              </Accordion.Item>
            </Accordion>

          </div>
        </div>
        <RuleModal visible={modal} setVisible={setModal}>
          <FeedbackFrom />
        </RuleModal>
      </div>
    )
  }
}

export default RuleViolationsContent;
