import React, { useEffect, useState } from 'react';
import PDFLines from './PDFLine';
import PDFLine from '../../classes/PDFLine';
import axios from 'axios';
import { useSelector } from 'react-redux';
import { RootState } from '../../store';

const Sections: React.FC = () => {
    let lines: PDFLine[] = [];
    const [lines, setLines] = useState<PDFLine[]>([]);
    const currentFileName = useSelector((state: RootState) => state.file.currentFileName);
    const apiUrl = useSelector((state: RootState) => state.file.apiUrl);
    useEffect(() => {
        axios.get(apiUrl+`/viewPDFLines?pdfName=${currentFileName}`)
          .then(res => {           
            setLines(res.data);
          })        
      }, [currentFileName]); 
  return (
    <div style={{ maxHeight: '800px', overflowY: 'auto', overflowX: 'hidden'}}> <PDFLines lines={lines} /></div>    
  );
};

export default Sections;
