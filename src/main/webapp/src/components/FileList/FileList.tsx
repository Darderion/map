import React, { FC, useState, useEffect } from 'react';
import File from '../File/File';
import { useDispatch, useSelector } from 'react-redux';
import { addReport, setRuleViolations } from '../../reducers/counterReducer';
import { RootState } from '../../store';
import { Button } from '@mantine/core';
import axios from 'axios';
import { Link } from 'react-router-dom';

interface FileListProps {
  files: Array<File>;
  remove: (file: File) => void;
}

const FileList: FC<FileListProps> = ({ files, remove }) => {
  const fullReport = useSelector((state: RootState) => state.file.fullPerort);
  const dispatch = useDispatch();
  const apiUrl = useSelector((state: RootState) => state.file.apiUrl);
  const currentPreset = useSelector((state: RootState) => state.file.currentPreset);
  const [processing, setProcessing] = useState(false);

  useEffect(() => {
    if (processing) {
      processFilesSequentially();
    }
  }, [processing]);

  const handleProcessAll = () => {
    setProcessing(true);
  };

  const processFile = async (file: File) => {
    const formData = new FormData();
    const containsName = fullReport.some(report => report.name === file.name);
    formData.append('file', file);
    if (currentPreset[0]) {
      formData.append('ruleSet', currentPreset[0]);
    } else {
      formData.append('ruleSet', '');
    }
    if(!containsName){       
      try {
        const response = await axios.post(apiUrl + '/uploadPDF', formData);
        if (response.data.ruleViolations) {
          dispatch(addReport(response.data));  
        }  
    } catch (error) {
      console.error(error);
    }}
  };

  const processFilesSequentially = async () => {
    for (let i = 0; i < files.length; i++) {
      await processFile(files[i]);
    }
    setProcessing(false);
  };

  return (
    <div className="fileList">
      <Button
        color="violet"
        disabled={processing}
        onClick={handleProcessAll}
        style={{ marginBottom: '1rem' }}
      >
        Process All Files
      </Button>
      {fullReport.length > 0 && (
        <Link to="/report">
          <Button color="blue" style={{ marginTop: '1rem' }}>
            Посмотреть статистику по работам
            Количество работ - {fullReport.length}
          </Button>
        </Link>
      )}
      {files.map((file, index) => (
        <File key={file.name} file={file} remove={() => remove(file)} index={index} />
      ))}
      
    </div>
  );
  
};

export default FileList;
