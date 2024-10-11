import React, { useState } from 'react';
import './MetricsResult.css';

type Metric = {
    name: string;
    value: number | string;
    description: string;
};

interface MetricsResultProps {
    data: Metric[];
}

const MetricsResult: React.FC<MetricsResultProps> = ({ data }) => {
    const [selectedMetric, setSelectedMetric] = useState<Metric | null>(null);

    const handleMetricClick = (metric: Metric) => {
        setSelectedMetric(metric);
    };

    const handleClosePanel = () => {
        setSelectedMetric(null);
    };

    return (
        <div className="metrics-result">
            <h2>Результаты анализа предложений</h2>
            <ul className="metrics-list">
                {data.map((metric, index) => (
                    <li 
                        key={index} 
                        className="metric-item"
                        onClick={() => handleMetricClick(metric)}
                    >
                        <span className="metric-name">{metric.name}:</span>
                        <span className="metric-value">{metric.value}</span>
                    </li>
                ))}
            </ul>

            {selectedMetric && (
                <div className="metric-details">
                    <button className="close-button" onClick={handleClosePanel}>Закрыть</button>
                    <h3>{selectedMetric.name}</h3>
                </div>
            )}
        </div>
    );
};

export default MetricsResult;
