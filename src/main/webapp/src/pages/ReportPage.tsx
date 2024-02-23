import React, { FC } from 'react';
import { Card, Group, Text, Button } from '@mantine/core';
import '../css/about.css';
import ErrorCount from '../components/ReportsStatistics/ReportStatistic';

const ReportPage: FC = () => {
  return (
    <div className='aboutPage'>
     <ErrorCount reports={[]}/>
    </div>
  );
};

export default ReportPage;