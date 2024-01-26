import React from 'react';
import PDFLine from '../../classes/PDFLine';
import {Text} from '@mantine/core';

interface PDFLinesProps {
  lines: PDFLine[];
}

const PDFLines: React.FC<PDFLinesProps> = ({ lines }) => {
  const getColorByArea = (area: string) => {
    const colors = ['#FF7F50', '#FFD700', '#00FFFF', '#FF00FF', '#00FF00', '#800080', '#FFFF00', '#00CED1', '#FFA500', '#008000'];
    let index = 0;
    switch (area){
        case "TITLE_PAGE": {
            index = 1;
            break;
        }
        case "BIBLIOGRAPHY": {
            index = 2;
            break;
        }
        case "TABLE_OF_CONTENT": {
            index = 3;
            break;
        }
        case "SECTION": {
            index = 4;
            break;
        }
        case "FOOTNOTE": {
            index = 5;
            break;
        }
        case "PAGE_INDEX": {
            index = 6;
            break;
        }
        case "CODE": {
            index = 7;
            break;
        }
    }
    return colors[index];
  };

  return (   
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        {lines.map((line) => (
          <div
            key={line.index}
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