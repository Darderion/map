import React, { useEffect } from 'react';
import PDFLines from './PDFLine';
import PDFLine from '../../classes/PDFLine';
import axios from 'axios';
import { useSelector } from 'react-redux';
import { RootState } from '../../store';

let lines: PDFLine[] = [];

const Sections: React.FC = () => {
    const currentFileName = useSelector((state: RootState) => state.file.currentFileName);
    const apiUrl = useSelector((state: RootState) => state.file.apiUrl);
    useEffect(() => {
        axios.get(apiUrl+`/viewPDFLines?pdfName=${currentFileName}`)
          .then(res => {           
            lines = res.data
          })        
      }, [currentFileName]); 
  return (
    <div style={{ maxHeight: '800px', overflowY: 'auto', overflowX: 'hidden'}}> <PDFLines lines={lines} /></div>    
  );
};

export default Sections;
