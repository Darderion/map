import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { viewFile, setCurrentFileName, setCurrentPage, setRuleViolations, addReport } from '../../reducers/counterReducer';
import DocumentReport from '../../classes/DocumentReport';
import { Card, Text, Button, LoadingOverlay, Loader, Box } from '@mantine/core';
import './File.css';
import { Link } from 'react-router-dom';
import axios from 'axios';
import { RootState } from '../../store';

interface FileProps {
  file: File;
  remove: (file: File) => void;
  index: number;
}
const File: React.FC<FileProps> = ({ file, remove, index }) => {
  const [uniqueUrl, setuniqueUrl] = useState<string>("false");

  const files = useSelector((state: RootState) => state.file.files);
  const apiUrl = useSelector((state: RootState) => state.file.apiUrl);
  const fullReport = useSelector((state: RootState) => state.file.fullPerort);
  const dispatch = useDispatch();
  const currentPreset = useSelector((state: RootState) => state.file.currentPreset);
  const handleButtonClick = () => {
    setLoad(true);
    const formData = new FormData();
    formData.append('file', file);
    if (currentPreset[0]) {
      formData.append('ruleSet', currentPreset[0]);
    } else {
      formData.append('ruleSet', "");
    }
    axios
      .post(apiUrl + '/uploadPDF', formData)
      .then((response) => {
        if (!response.data.ruleViolations) {
        } else {
          setuniqueUrl(response.data.name)
          dispatch(addReport(response.data));      
        }
      })
      .catch((error) => {
      });
  };
  const containsName = fullReport.some(report => report.name.split('--')[0]=== file.name);
  const docReport = fullReport.find((report) => report.name.split('--')[0] === file.name);
  const handleViewFile = (fileName: string) => {
    dispatch(setCurrentFileName(fileName));
    if(docReport){ dispatch(setRuleViolations(docReport.ruleViolations));}
    dispatch(setCurrentPage(0));
    dispatch(viewFile(files[index]));
    setLoad(false);
  };

  const [load, setLoad] = useState(false);
 
  return (
    <Card className="fileItem" shadow="sm" padding="lg" radius="md" withBorder>
      <Text className="fileName">{file.name}</Text>
      <div>
        {docReport && containsName ? (
          <div>
            <div className="fileButtons">
            <Link to={`/processFile/${uniqueUrl}`}>
                <Button
                  className="fileButton"
                  onClick={() => handleViewFile(docReport.name.split('--')[0])}
                  color="violet"
                  variant="light"
                >
                  {'Количество ошибок - ' + docReport.ruleViolations.length}
                </Button>
              </Link>
              <Button className="fileButton" color="violet" onClick={() => remove(file)}>
                &#10008;
              </Button>
            </div>
          </div>
        ) : (
          <div className="filesButtons">
            <Box pos="relative">
              <LoadingOverlay visible={load} loaderProps={{ color: 'violet', size: 'md', type: 'dots' }} />
              <Button color="violet" className="fileButton" onClick={handleButtonClick}>
                Начать обработку
              </Button>
            </Box>
            <Button className="fileButton" color="violet" onClick={() => remove(file)}>
              &#10008;
            </Button>
          </div>
        )}
      </div>
    </Card>
  );
  
};

export default File;