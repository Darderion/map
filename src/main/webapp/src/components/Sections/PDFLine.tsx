import React from 'react';
import PDFLine from '../../classes/PDFLine';
import { Text } from '@mantine/core';

interface PDFLinesProps {
  lines: PDFLine[];
}

const PDFLines: React.FC<PDFLinesProps> = ({ lines }) => {
  const getColorByArea = (area: string) => {
    const colors: Record<string, string> = {
      TITLE_PAGE: '#FF7F50',
      BIBLIOGRAPHY: '#FFD700',
      TABLE_OF_CONTENT: '#00FFFF',
      SECTION: '#FF00FF',
      FOOTNOTE: '#00FF00',
      PAGE_INDEX: '#800080',
      CODE: '#FFFF00',
    };

    return colors[area];
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
      {lines.map((line) => (
        <div
          style={{
            backgroundColor: getColorByArea(line.area),
            width: '100%',
            padding: '10px',
            marginTop: '10px',
            borderRadius: '4px',
          }}
        >
          <Text align="center">{line.content}</Text>
        </div>
      ))}
    </div>
  );
};

export default PDFLines;
