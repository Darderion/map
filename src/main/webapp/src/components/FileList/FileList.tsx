import React, { FC } from 'react';
import File from '../File/File';
import './FileList.css'
import { ScrollArea } from '@mantine/core';

interface FileListProps {
  files: Array<File>;
  remove: (file: File) => void;
}

const FileList: FC<FileListProps> = ({ files, remove }) => {
  return (
    <div className="fileList">
      <ScrollArea h={660} type="never" offsetScrollbars scrollbarSize={8} scrollHideDelay={0}>
        {files.map((file, index) => (
          <File key={file.name} file={file} remove={() => remove(file)} index={index} />
        ))}
      </ScrollArea>
    </div>
  );
};


export default FileList;
