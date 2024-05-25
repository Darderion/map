import React, { useState } from 'react';
import { Card, Text, Button, Title } from '@mantine/core';
import firstStepGif from "../resources/firstStep.gif"
import secondStepGif from "../resources/secondStep.gif"
import thirdStepGif from "../resources/thirdStep.gif"
import fourthStepGif from "../resources/fourthStep.gif"
import fifthStepGif from "../resources/fifthStep.gif"
import sixthStepGif from "../resources/sixthStep.gif"
import '../css/guide.css';
const GuidePage: React.FC = () => {
    const [selectedCard, setSelectedCard] = useState<number | null>(null);
    const arrowDownCard = 2;
    const arrowRightCards = [0,1];
    const arrowLeftCards = [5,4];
    const steps = [
        { text: 'Для начала вам следует выбрать набор правил. Если вы хотите провести проверку со всеми правилами, тогда игнорируйте этот пункт. Вам следует перейти в пункт "Наборы правил и там выбрать один из существующих наборов или создать свой."', gif: firstStepGif },
        { text: 'После выбора набора правил, вам следует перейти на страницу для загрузки файлов. Здесь надо выбрать файл и начать загрузку. После обработки нажмите на количество ошибок и сможете перейти на обзор файла.', gif: secondStepGif },
        { text: 'Вам откроется страница с тремя полями, в первом идет список ошибок, нажав на которые их можно просмотреть. Также тут возможно фильтровать ошибки по названиям или страницам.', gif: thirdStepGif },
        { text: 'Наконец, если среди нарушений вы нашли ошибочное, например, вам кажется, что тут все правильно, а отчет говорит обратное, то стоит отправить нам отчет, нажав на соответствующую кнопку. ', gif: sixthStepGif },
        { text: 'По центру будет поле со страницей, на которой подчеркнута ошибка. Если ошибка занимает больше одной строки, то будут подчеркнуты несколько строк.', gif: fifthStepGif },
        { text: 'Слева будет поле, на котором будут все найденные типы ошибок. Если навести на тип ошибки, то можно увидеть описание данной ошибки. Также рядом написан тип ошибки: "предупреждение" или "ошибка". Предупреждения не так принципиальны, как ошибки.', gif: fourthStepGif },
    ];

    const handleCardClick = (index: number) => {
        if (selectedCard === index) {
            setSelectedCard(null);
        } else {
            setSelectedCard(index);
        }
    };
    return (
        <div className='guide'>
            <Title className="name">
                Приложение{' '}<br />
                <Text component="span" color="violet">
                    MAP
                </Text>{' '}<br />-
                это инструмент<br /> для проверки студенческих <br />работ
            </Title>
            <div>
                <Text className='description'>Ниже приведен демо файл для тестового прогона программы. Также снизу есть пошаговая инструкция.</Text>
            </div>
            <Button component="a" href={require('../resources/Demo.pdf')} download>Скачать файл</Button>
            <div style={{ display: 'flex', flexWrap: 'wrap' }}>
                {steps.map((step, index) => (
                    <Card 
                        className='stepCard'
                        key={index}
                        shadow="sm"
                        style={{                           
                            transform: selectedCard === index ? 'rotateY(180deg)' : 'rotateY(0deg)'                                              
                        }}
                        onClick={() => handleCardClick(index)}
                    >
                        <div style={{ transform: selectedCard === index ? 'rotateY(180deg)' : 'rotateY(0deg)'}}>
                            <Text>{step.text}</Text>
                            {index === arrowDownCard && selectedCard !== index && <div className="arrow down">➜➤</div>}
                            {arrowLeftCards.includes(index) && selectedCard !== index && <div className="arrow left">➜➤</div>}  
                            {selectedCard !== index && arrowRightCards.includes(index) && <div className="arrow right">➜➤</div>}
                            {selectedCard === index && <img src={step.gif} alt={`Step ${index + 1}`} style={{ width: '100%', height: '70%' }} />}                        
                        </div>
                    </Card>
                ))}
            </div>
        </div>
    );
};

export default GuidePage;