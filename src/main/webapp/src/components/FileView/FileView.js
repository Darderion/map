import { useSelector } from 'react-redux';
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Document, Page, pdfjs } from 'react-pdf';
import './FileView.css'

function FileView() {
  const [pdfData, setPdfData] = useState(null);
  const [firstPagePdfData, seFfirstPagePdfData] = useState(null);
  const [numPages, setNumPages] = useState(null);
  const currentPage = useSelector((state) => state.file.currentPage);
  const currentLine = useSelector((state) => state.file.currentLine);
  const currentFileName = useSelector((state) => state.file.currentFileName);
  pdfjs.GlobalWorkerOptions.workerSrc = `//unpkg.com/pdfjs-dist@${pdfjs.version}/legacy/build/pdf.worker.min.js`;
  useEffect(() => {
    const fileName = currentFileName;
    const page = currentPage;
    const lines = currentLine;
    axios.get(`http://localhost:8081/api/viewPDFRuleViolations.pdf?pdfName=${fileName}&page=${page}&lines=${lines}`, {
      responseType: 'arraybuffer',
    })
      .then(response => {
        setPdfData(response.data);
      })
      .catch(error => {
        console.error('Ошибка при отправке запроса:', error);
      });
  }, [currentLine,currentPage]); 
  useEffect(() => {
    const fileName = currentFileName;
    axios.get(`http://localhost:8081/api/viewPDF.pdf?pdfName=${fileName}`, {
      responseType: 'arraybuffer',
    })
      .then(response => {
        seFfirstPagePdfData(response.data);
      })
      .catch(error => {
        console.error('Ошибка при отправке запроса:', error);
      });
  }, []); 
  const onDocumentLoadSuccess = ({ numPages }) => {
    setNumPages(numPages);
  };
  return (
    <div  className="filePage">
      {pdfData || currentPage ?  (        
          <Document
            file={new Blob([pdfData], { type: 'application/pdf' })}
            onLoadSuccess={onDocumentLoadSuccess}
          >
            <Page renderAnnotationLayer={false} renderTextLayer={false} wrap={true} pageNumber={currentPage+1} />
          </Document>
          
      ) : (
       <Document
        file={new Blob([firstPagePdfData], { type: 'application/pdf' })}
        onLoadSuccess={onDocumentLoadSuccess}
      >
        <Page renderAnnotationLayer={false} renderTextLayer={false} wrap={true} pageNumber={1} />
      </Document>


      )}
    </div>
  );
}

export default FileView;