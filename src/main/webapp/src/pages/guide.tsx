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

    const steps = [
        { text: 'Для начала вам следует выбрать набор правил. Если вы хотите провести проверку со всеми правилами, тогда игнорируйте этот пункт. Вам следует перейти в пункт "Наборы правил и там выбрать один из существующих наборов, или создать свой.."', gif: firstStepGif },
        { text: 'После выбора набора правил, вам следует перейти на страницу для загрузки файлов. Здесь надо выбрать файл и начать загрузку. После обработки нажмите на количество ошибок и сможете перейти на обзор файла.', gif: secondStepGif },
        { text: 'Вам откроется страница с тремя полями, в первом идет список ошибок, нажав на которые их можно просмотреть. Также тут возможно фильтровать ошибки по названиям, или страницам.', gif: thirdStepGif },

        { text: 'Наконец если среди нарушений вы нашли ошибочное. Например вам кажется что тут все правильно, а отчет говорит обратное, то стоит отправить нам отчет нажав на соответствующую кнопку. ', gif: sixthStepGif },
        { text: 'По центру будет поле со страницей, с подчеркнутой ошибкой, если ошибка занимает больше одной строки то будут подчеркнуты несколько', gif: fifthStepGif },
        { text: 'Слева будет поле, на котором будут все найденные типы ошибок, если навести на тип ошибки, то можно увидеть описание данной ошибки. Также рядом написан тип ошибки: предупреждение или ошибка. Предупреждения не так принципиальны как ошибки.', gif: fourthStepGif },
    ];

    const handleCardClick = (index: number) => {
        if (selectedCard === index) {
            setSelectedCard(null);
        } else {
            setSelectedCard(index);
        }
    };
    return (
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', padding: '20px' }}>
            <Title className="name">
                Приложение{' '}<br />
                <Text component="span" color="violet">
                    MAP
                </Text>{' '}<br />-
                это инструмент<br /> для проверки студенческих <br />работ
            </Title>
            <div style={{ display: 'flex', flexDirection: 'row', alignItems: 'center', padding: '20px' }}>
                <Text style={{ fontSize: '24px', fontWeight: 'bold', marginBottom: '20px', maxWidth: '700px' }}>Ниже приведен демо файл для тестового прогона программы. Также снизу есть пошаговая инструкция.</Text>
                //<iframe width="560" height="315" src="https://www.youtube.com/embed/video" title="YouTube video player" frameBorder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowFullScreen></iframe>
            </div>
            <Button component="a" href={require('../resources/Demo.pdf')} download style={{ marginBottom: '20px' }}>Скачать файл</Button>
            <div style={{ display: 'flex', flexWrap: 'wrap', height: '70vh' }}>
                {steps.map((step, index) => (
                    <Card
                        key={index}
                        shadow="sm"
                        style={{
                            width: 'calc(33.33% - 20px)',
                            margin: 10,
                            height: '600px',
                            border: '2px solid violet',
                            borderRadius: 8,
                            transform: selectedCard === index ? 'rotateY(180deg)' : 'rotateY(0deg)',
                            transition: 'transform 0.5s ease',
                            display: 'flex',
                            flexDirection: 'column',
                            justifyContent: 'center',
                            alignItems: 'center',
                            position: 'relative',
                        }}
                        onClick={() => handleCardClick(index)}
                    >
                        <div style={{ transform: selectedCard === index ? 'rotateY(180deg)' : 'rotateY(0deg)', textAlign: 'center' }}>
                            <Text style={{ marginBottom: "50px" }}>{step.text}</Text>
                            {index === 2 && selectedCard !== index && <div className="arrow down">➜➤</div>}
                            {index === 5 && selectedCard !== index && <div className="arrow left">➜➤</div>}
                            {index === 4 && selectedCard !== index && <div className="arrow left">➜➤</div>}
                            {selectedCard !== index && index !== 2 && index !== 3 && index !== 4 && index !== 5 && <div className="arrow right">➜➤</div>}
                            {selectedCard === index && <img src={step.gif} alt={`Step ${index + 1}`} style={{ width: '100%', height: '70%' }} />}
                        </div>
                    </Card>

                ))}

            </div>

        </div>
    );
};

export default GuidePage;
