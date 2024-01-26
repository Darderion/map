import { MantineReactTable, useMantineReactTable } from 'mantine-react-table';
import React, { useState, useEffect, useMemo } from 'react';
import axios from 'axios'; 
import { useSelector } from 'react-redux';
import { Title, RingProgress, ColorSwatch, Text} from '@mantine/core';
import './Statistics.css'
import { RootState } from '../../store';
const Statistics = () => {
  const [wordData, setWordData] = useState([]);
  const [pageData, setPageData] = useState([]);
  const [wordCount, setWordCount] = useState(0);
  const [pageCount, setPageCount] = useState(0);
  const currentFileName = useSelector((state: RootState) => state.file.currentFileName);
  const apiUrl = useSelector((state: RootState) => state.file.apiUrl);
  const colors = ['#FF7F50', '#FFD700', '#00FFFF', '#FF00FF', '#00FF00', '#800080', '#FFFF00', '#00CED1', '#FFA500', '#008000'];
  
  useEffect(() => {
    axios.get(apiUrl+`/viewPDFStatistic?pdfName=${currentFileName}`)
      .then(res => {
        const sortedWords:any = Object.entries(res.data.wordsStatistic.topKWords)
            .sort((a:any, b:any) => b[1] - a[1]) 
            .slice(0, 100)
            .map(([word, count], index) => ({ number: index + 1, word, count }));
        setWordData(sortedWords);
        setWordCount(res.data.wordsStatistic.wordCount); 
        const pageResults = res.data.pageStatistic.sectionsStatistics.map((item: {
          areaSizeInPage: number; section: { title: string; };}) => ({
          title: item.section.title.replace(/^\d+\.\s*/, ''),
          size: item.areaSizeInPage
        }));
        const totalSize = pageResults.reduce((sum: number, item: { size: number; }) => sum + item.size, 0);
        
        const resultsWithPercentage = pageResults.map((item: { size: number; }) => ({ 
          ...item,
          sizePercentage: (item.size / totalSize) * 100
        }));
        
        setPageCount(res.data.pageStatistic.bibliographyStatistics.documentSize);
        setPageData(resultsWithPercentage);

      })
      .catch(err => {
        console.error('Error:', err);
      });
  }, [currentFileName]); 

  const columns = useMemo(
    () => [
      {
        accessorKey: 'number', 
        header: 'Номер',
      },
      {
        accessorKey: 'word',
        header: 'Слово',
      },
      {
        accessorKey: 'count',
        header: 'Колличество',
      },
    ],
    [],
  );
  const table = useMantineReactTable({
    columns,
    data: wordData, 
  });

  return (
    <div className="statistics">
      <Title className="title">Статистика слов</Title>
      <Text color="dimmed">{wordCount} слов</Text>
      <MantineReactTable table={table} />

      <Title className="title">Статистика секций</Title>
      <Text color="dimmed">{pageCount} страниц</Text>
      <div className="diagramWithTitles">
      <RingProgress
        size={400}
        thickness={70}
        sections={pageData.map((item:any, index) => ({ value: item.sizePercentage, color: colors[index % colors.length] }))}
      />   
  <ul className="sectionsList">
    {pageData.map((item:any, index) => (
      <li key={index} className="section">
        <ColorSwatch className="swatch" color={colors[index % colors.length]} />
        <Text>{item.title}</Text>
      </li>
    ))}
  </ul>
</div>
    </div>
  );
};

export default Statistics;
