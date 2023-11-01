import React from 'react';
import File from '../File/File';
import './FileList.css'
import { ScrollArea } from '@mantine/core';

const FileList = ({ files, remove }) => {

  return (
    <div className="fileList">
      <ScrollArea h={660} type="never" offsetScrollbars scrollbarSize={8} scrollHideDelay={0}>
      {files.map((file, index) => (      
        <File key={file.name} file={file} remove={remove} index={index}/>             
      ))}
      </ScrollArea>
    </div>
  );
};

export default FileList;
