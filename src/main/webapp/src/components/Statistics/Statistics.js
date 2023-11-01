import { MantineReactTable, useMantineReactTable } from 'mantine-react-table';
import React, { useState, useEffect, useMemo } from 'react';
import axios from 'axios'; 
import { useSelector } from 'react-redux';
import { Title, RingProgress, ColorSwatch, Text} from '@mantine/core';
import './Statistics.css'
const Statistics = () => {
  const [wordData, setWordData] = useState([]);
  const [pageData, setPageData] = useState([]);
  const [wordCount, setWordCount] = useState(0);
  const [pageCount, setPageCount] = useState(0);
  const currentFileName = useSelector((state) => state.file.currentFileName);
  const colors = ['#FF7F50', '#FFD700', '#40E0D0', '#FF69B4', '#7CFC00', '#FF1493', '#00CED1', '#FF8C00', '#8A2BE2', '#00FF00'];

  useEffect(() => {
    axios.get(`http://localhost:8081/api/viewPDFStatistic?pdfName=${currentFileName}`)
      .then(res => {
        const sortedWords = Object.entries(res.data.wordsStatistic.topKWords)
            .sort((a, b) => b[1] - a[1]) 
            .slice(0, 100)
            .map(([word, count], index) => ({ number: index + 1, word, count }));
        setWordData(sortedWords);
        setWordCount(res.data.wordsStatistic.wordCount); 

        const pageResults = res.data.pageStatistic.sectionsStatistic.map(item => ({
          title: item.section.title.replace(/^\d+\.\s*/, ''),
          size: item.sectionSizeInPage
        }));
        const totalSize = pageResults.reduce((sum, item) => sum + item.size, 0);
        const resultsWithPercentage = pageResults.map(item => ({ 
          ...item,
          sizePercentage: (item.size / totalSize) * 100
        }));
        setPageCount(res.data.pageStatistic.sectionsStatistic[0].documentSize);
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
        header: '#',
      },
      {
        accessorKey: 'word',
        header: 'Word',
      },
      {
        accessorKey: 'count',
        header: 'Count',
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
      <Title className="title">Words statistics</Title>
      <Text color="dimmed">{wordCount} words</Text>
      <MantineReactTable table={table} />

      <Title className="title">Pages statistics</Title>
      <Text color="dimmed">{pageCount} pages</Text>
      <div className="diagramWithTitles">
  <RingProgress
    size={400}
    thickness={70}
    sections={pageData.map((item, index) => ({ value: item.sizePercentage, color: colors[index % colors.length] }))}
  />
  <ul className="sectionsList">
    {pageData.map((item, index) => (
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