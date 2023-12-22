import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { viewFile, setCurrentFileName, setCurrentPage, setRuleViolations } from '../../reducers/counterReducer';
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
  const files = useSelector((state: RootState) => state.file.files);
  const apiUrl = useSelector((state: RootState) => state.file.apiUrl);
  const dispatch = useDispatch();
  const [documentReport, setDocumentReport] = useState<DocumentReport | null>(null);

  const currentPreset = useSelector((state: RootState) => state.file.currentPreset);
  const handleButtonClick = () => {
    setLoad(true);
    const formData = new FormData();
    console.log(currentPreset)
    console.log("5")
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
          setDocumentReport(null);
        } else {
          setDocumentReport(response.data);
          dispatch(setRuleViolations(response.data.ruleViolations));
        }
      })
      .catch((error) => {
        setDocumentReport(null);
      });
  };
  const handleViewFile = (fileName: string) => {
    dispatch(setCurrentFileName(fileName));
    dispatch(setCurrentPage(0));
    dispatch(viewFile(files[index]));
    setLoad(false);
  };

  const [load, setLoad] = useState(false);

  return (
    <Card className="fileItem" shadow="sm" padding="lg" radius="md" withBorder>
      <Text className="fileName">{file.name}</Text>
      <div>
        {documentReport ? (
          <div>
            <div className="fileButtons">
              <Link to="/ProcessFile">
                <Button
                  className="fileButton"
                  onClick={() => handleViewFile(documentReport.name)}
                  color="violet"

                  variant="light"
                >
                  {documentReport.ruleViolations.length + ' errors'}
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
              <LoadingOverlay
                visible={load}

                loaderProps={{ color: 'violet', size: 'md', type: 'dots' }}
              />
              <Button color="violet" className="fileButton" onClick={handleButtonClick}>
                Start
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