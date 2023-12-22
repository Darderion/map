import { useSelector } from 'react-redux';
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Document, Page, pdfjs } from 'react-pdf';
import './FileView.css';
import { RootState } from '../../store';

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

    const fetchFirstPagePdfData = async () => {
      const response = await axios.get<ArrayBuffer>(apiUrl +
        `/api/viewPDF.pdf?pdfName=${currentFileName}`,
        {
          responseType: 'arraybuffer',
        }
      );
      setFirstPagePdfData(response.data);
    };

    fetchPdfData();
    fetchFirstPagePdfData();
  }, [currentLine, currentPage]);


  return (
    <div className="filePage">
      {(pdfData && currentPage) || firstPagePdfData ? (
        <Document
          file={new Blob([(pdfData || firstPagePdfData)!], { type: 'application/pdf' })}
        >
          <Page renderAnnotationLayer={false} renderTextLayer={false} pageNumber={currentPage + 1} />
        </Document>
      ) : (
        <div>No PDF data available.</div>
      )}
    </div>
  );
};

export default FileView;