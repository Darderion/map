import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { viewFile, setCurrentFileName, setCurrentPage, setRuleViolations } from '../../actions/fileAction';
import { Card, Text, Button, LoadingOverlay, Box } from '@mantine/core';
import './File.css';
import { Link } from 'react-router-dom';
import axios from 'axios';

const File = ({ file, remove, index }) => {
  const files = useSelector((state) => state.file.files);
  const dispatch = useDispatch();
  const [documentReport, setDocumentReport] = useState(null);
  const currentPreset = useSelector((state) => state.file.currentPreset);
  const [load, setLoad] = useState(false); 
  const [error, setError] = useState(null); 
  useEffect(() => {
    if (file.documentReport) {
      setDocumentReport(file.documentReport);
    }
  }, [file]);

  const handleButtonClick = () => {
    if (file.documentReport) return;

    setLoad(true); 

    const formData = new FormData();
    formData.append('file', file);
    if (currentPreset[0]) {
      formData.append('ruleSet', currentPreset[0]);
      console.log(currentPreset);
    } else {
      formData.append('ruleSet', []);
    }
    
    axios.post('http://localhost:8081/api/uploadPDF', formData)
      .then(response => {
        if (!response.data.ruleViolations) {
          setDocumentReport(null);
          setError('Не найдено ошибок'); 
        } else {
          setDocumentReport(response.data);
          dispatch(setRuleViolations(documentReport));
        }
      })
      .catch(error => {
        setDocumentReport(null);
        setError('Ошибка'); 
      })
      .finally(() => {
        setLoad(false); 
      });
  };

  const handleViewFile = (fileName) => {
    dispatch(setRuleViolations(documentReport.ruleViolations));
    dispatch(setCurrentFileName(fileName));
    dispatch(setCurrentPage(null));
    dispatch(viewFile(files[index]))
  };

  const isPDFArea = documentReport?.ruleViolations?.some(violation => violation.type === 'PDFArea');
  
  return (
    <Card className="fileItem" shadow="sm" padding="lg" radius="md" withBorder>
      <Text className="fileName">{file.name}</Text>
      <div> 
        {documentReport ? (
          <div>
            <div className='fileButtons'>
              {isPDFArea 
                ? <Text>Violation of Type PDFArea</Text>
                : <Link to="/ProcessFile">
                    <Button
                      className="fileButton"
                      onClick={() => handleViewFile(documentReport.name)}
                      color="violet"
                      file={files[index]}
                      variant="light"
                    >
                      {documentReport.ruleViolations.length + " errors"}
                    </Button>
                  </Link>
              }            
              { !file.documentReport && 
                <Button className="fileButton" color="violet" onClick={() => remove(file)}>
                  ✘
                </Button>
              }
            </div>
          </div>
        ) : (
          <div className="filesButtons">
            <Box pos="relative">
              <LoadingOverlay visible={load} overlayProps={{ radius: "sm", blur: 2 }}
                loaderProps={{ color: "violet", size: "md", type: "dots" }} />
              {error ? <Text color="red">{error}</Text> : null} 
              <Button color='violet' className="fileButton" onClick={handleButtonClick}>Start</Button>
            </Box>
            { !file.documentReport &&
              <Button className="fileButton" color="violet" onClick={() => remove(file)}>
                &#10008;
              </Button>
            }
          </div>
        )}
      </div>
    </Card>
  );
};

export default File;