import React, { useState, useEffect, useMemo } from 'react';
import { useDispatch } from 'react-redux';
import { FileButton, Button } from '@mantine/core';
import { uploadFile } from '../reducers/counterReducer';
import ListContainer from '../components/ListContainer/ListContainer';
import FileList from '../components/FileList/FileList'
import RuleInput from '../components/RuleInput/RuleInput';


const Download: React.FC = () => {
  const [files, setFiles] = useState<File[]>([]);
  const [searchQuery, setSearchQuery] = useState<string>('')
  const dispatch = useDispatch();
  const [refreshKey, setRefreshKey] = useState<number>(0);

  const selectedFiles = useMemo(() => {
    return files.filter(file => file && file.name && file.name.toLowerCase().includes(searchQuery.toLowerCase()))
  }, [searchQuery, files]);

  const handleFileChange = (props: File[]) => {
    setFiles([...props, ...files])
    props.forEach(file => {
      dispatch(uploadFile(file))
    })
  };

  const removeFile = (file: File) => {
    setFiles(files.filter(f => f !== file));
    setRefreshKey(refreshKey + 1);
  }

  const buttons = [
    <FileButton multiple
      onChange={handleFileChange}
      accept="application/pdf">
      {(props: any) =>
        <Button className="listButton"
          onChange={handleFileChange}
          {...props}
          color="violet">
          Добавить файл
        </Button>}
    </FileButton>
  ]

  const search = <RuleInput
    value={searchQuery}
    onChange={e => setSearchQuery(e.target.value)}
  />

  return (
    <ListContainer title='Файлы'
      subtitle = ''
      buttons={buttons}
      search={search}
      list={<FileList files={selectedFiles} remove={removeFile} />} />
  );
};

export default Download;