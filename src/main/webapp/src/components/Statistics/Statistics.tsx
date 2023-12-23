import { FC, useState, useEffect, useMemo } from 'react';
import axios from 'axios';
import { useSelector } from 'react-redux';
import { Title, RingProgress, ColorSwatch, Text } from '@mantine/core';
import { MantineReactTable, useMantineReactTable } from 'mantine-react-table'; 
import './Statistics.css';
import { RootState } from '../../store';
interface WordsStatisticResponse {
  topKWords: Record<string, number>;
  wordCount: number;
}

interface PageStatisticResponse {
  sectionsStatistic: {
    section: { title: string };
    sectionSizeInPage: number;
    documentSize: number;
  }[];
}

interface ApiResponse {
  wordsStatistic: WordsStatisticResponse;
  pageStatistic: PageStatisticResponse;
}

interface WordData {
  number: number;
  word: string;
  count: number;
}

interface PageData {
  title: string;
  size: number;
  sizePercentage: number;
}

const Statistics: FC = () => {
  const [wordData, setWordData] = useState<WordData[]>([]);
  const [pageData, setPageData] = useState<PageData[]>([]);
  const [wordCount, setWordCount] = useState<number>(0);
  const [pageCount, setPageCount] = useState<number>(0);
  const currentFileName = useSelector((state: RootState) => state.file.currentFileName);
  const apiUrl = useSelector((state: RootState) => state.file.apiUrl);
  const colors: string[] = ['#FF7F50', '#FFD700', '#40E0D0', '#FF69B4', '#7CFC00', '#FF1493', '#00CED1', '#FF8C00', '#8A2BE2', '#00FF00'];

  useEffect(() => {
    axios.get<ApiResponse>(apiUrl+`/viewPDFStatistic?pdfName=${currentFileName}`, {
      responseType: 'json',
    })
      .then(res => {
        const sortedWords: WordData[] = Object.entries(res.data.wordsStatistic.topKWords)
          .sort((a, b) => b[1] - a[1])
          .slice(0, 100)
          .map(([word, count], index) => ({ number: index + 1, word, count }));
        setWordData(sortedWords);
        setWordCount(res.data.wordsStatistic.wordCount);

        const pageResults: PageData[] = res.data.pageStatistic.sectionsStatistic.map(item => ({
          title: item.section.title.replace(/^\d+\.\s*/, ''),
          size: item.sectionSizeInPage,
          sizePercentage: 0,
        }));
        const totalSize = pageResults.reduce((sum, item) => sum + item.size, 0);
        setPageCount(res.data.pageStatistic.sectionsStatistic[0].documentSize);
        setPageData(pageResults.map(item => ({
          ...item,
          sizePercentage: (item.size / totalSize) * 100,
        })));
      })
      .catch(err => {
        console.error('Error:', err);
      });
  }, [currentFileName]);

  const columns = useMemo(() => [
    { accessorKey: 'number', header: '#' },
    { accessorKey: 'word', header: 'Word' },
    { accessorKey: 'count', header: 'Count' },
  ], []);

  const tableProps = MantineReactTable<WordData>({
    columns,
    data: wordData,
  });

  return (
    <div className="statistics">

    </div>
  );
};

export default Statistics;
