import React, { useEffect, useState } from 'react';
import PDFLines from './PDFLine';
import PDFLine from '../../classes/PDFLine';
import axios from 'axios';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../../store';
import { setCurrentFileName } from '../../reducers/counterReducer';

const Sections: React.FC = () => {
  const dispatch = useDispatch();
  const [lines, setLines] = useState<PDFLine[]>([]);

  let currentFileName = useSelector((state: RootState) => state.file.currentFileName);
  const apiUrl = useSelector((state: RootState) => state.file.apiUrl);
  const pathname = window.location.pathname;
  useEffect(() => {
    if (currentFileName == null && pathname.endsWith("=share")) {
     
      const segments = pathname.split('/');
      currentFileName = segments[segments.length - 1].replace('=share', '');
     
    }
    axios.get(apiUrl + `/viewPDFLines?pdfName=${currentFileName}`)
      .then(res => {           
        setLines(res.data);
      });        
  }, []);

  return (
    <div className='sections' style={{ maxHeight: '800px', overflowY: 'auto', overflowX: 'hidden'}}>
      <PDFLines lines={lines} />
    </div>    
  );
};

export default Sections;
