import { useSelector } from 'react-redux';
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Document, Page, pdfjs } from 'react-pdf';
import './FileView.css';
import { RootState } from '../../store';
import { Text } from '@mantine/core';

interface FileViewProps { }
const FileView: React.FC<FileViewProps> = () => {
  const [pdfData, setPdfData] = useState<BlobPart | null>(null);
  const [firstPagePdfData, setFirstPagePdfData] = useState<ArrayBuffer | null>(null);
  const currentPage = useSelector((state: RootState) => state.file.currentPage);
  const currentLine = useSelector((state: RootState) => state.file.currentLine);
  const apiUrl = useSelector((state: RootState) => state.file.apiUrl);
  const currentFileName = useSelector((state: RootState) => state.file.currentFileName);
  pdfjs.GlobalWorkerOptions.workerSrc = `//unpkg.com/pdfjs-dist@${pdfjs.version}/legacy/build/pdf.worker.min.js`;
  useEffect(() => {
    const fetchPdfData = async () => {
      const response = await axios.get<ArrayBuffer>(
        apiUrl + `/viewPDFRuleViolations.pdf?pdfName=${currentFileName}&page=${currentPage}&lines=${currentLine}`,
        {
          responseType: 'arraybuffer',
        }
      );
      setPdfData(response.data);
    };
    fetchPdfData();
  }, [currentLine, currentPage]);
  return (
    <div className="filePage">
      {(pdfData ?? currentPage > -1)  ? (
        <Document
          file={new Blob([(pdfData || firstPagePdfData)!], { type: 'application/pdf' })}
        >
          <Page renderAnnotationLayer={false} renderTextLayer={false} pageNumber={currentPage + 1} />
        </Document>
      ) : (
        <div className="noPdfMessage">
        <Text className="instructions">Для начала выберите одну из ошибок</Text>
        <Text className="instructions">Вы также можете выбрать какие правила хотите видеть</Text>
        <Text className="instructions">Или отсортировать правила по предложенным категориям</Text>
        <div className="arrow">  ➜➤</div>
      </div>
      )}
    </div>
  );
};

export default FileView;  