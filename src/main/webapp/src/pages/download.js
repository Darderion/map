import React, { useState, useEffect, useMemo } from 'react';
import { useDispatch } from 'react-redux';
import { FileButton, Button } from '@mantine/core';
import { uploadFile } from '../actions/fileAction';
import ListContainer from '../components/ListContainer/ListContainer';
import FileList from '../components/FileList/FileList'
import RuleInput from '../components/RuleInput/RuleInput';
import axios from 'axios';

const Download = () => {
  const [files, setFiles] = useState([]);
  const [searchQuery, setSearchQuery] = useState('')
  const dispatch = useDispatch();
  const [refreshKey, setRefreshKey] = useState(0);
  useEffect(() => {
    const fetchDocuments = async () => {
      try {
        const response = await axios.get('http://localhost:8081/api/listUploadedPDFsWithReports');
        const newFiles = response.data.filter((respFile) => !files.find((file) => file.name === respFile.name));
        setFiles((existingFiles) => [...existingFiles, ...newFiles]);
      } catch (error) {
        console.error('Error fetching documents:', error);
      }
    };
  
    fetchDocuments();
  }, []);

  console.log(files);

  const selectedFiles = useMemo(() => {
    return files.filter(file => file && file.name && file.name.toLowerCase().includes(searchQuery.toLowerCase()))
  }, [searchQuery, files]);

  const handleFileChange = (props) => {
    setFiles([...props, ...files])
    props.forEach(file => {
      dispatch(uploadFile(file))
    })
  };

  const removeFile = (file) => {
    setFiles(files.filter(f => f !== file));
    setRefreshKey(refreshKey + 1);
  }

  const buttons = [
    <FileButton multiple
      onChange={handleFileChange}
      accept="application/pdf">
      {(props) =>
        <Button className="listButton"
          onChange={handleFileChange}
          {...props}
          color="violet">
          Add Files
        </Button>}
    </FileButton>
  ]

  const search = <RuleInput
    value={searchQuery}
    onChange={e => setSearchQuery(e.target.value)}
  />

  return (
    <ListContainer title='Files'
      buttons={buttons}
      search={search}
      list={<FileList files={selectedFiles} remove={removeFile} />} />
  );
};

export default Download;
