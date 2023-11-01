import React, { useState } from 'react';
import { Button, Group } from '@mantine/core';
import { useDispatch, useSelector } from 'react-redux';
import { Textarea } from '@mantine/core';
import axios from 'axios';

const FeedbackFrom = () => {
  const pdfName = useSelector((state) => state.file.currentFileName);
  const page = useSelector((state) => state.file.currentPage);
  const line = useSelector((state) => state.file.currentLine);
  const title = useSelector((state) => state.file.selectedItem);
  const [comment, setComment] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();

    const formData = new FormData();
    formData.append('pdfName', pdfName);
    formData.append('page', page);
    formData.append('title', title);
    formData.append('line', line);
    formData.append('comment', comment);

    axios.post('http://localhost:8081/api/submitFeedback', formData)
      .then(response => {
        console.log(response.data);
      })
      .catch(error => {
        console.error('Error submitting feedback:', error);
      });
  };

  return (
    <form onSubmit={handleSubmit}>
      <Textarea
        size="xl"
        radius="md"
        label="Почему вам кажется что правило сработало неверно?"
        description="Comment"
        placeholder=""
        value={comment}
        onChange={(e) => setComment(e.target.value)}
      />
      <Group justify="flex-end" mt="md">
        <Button type="submit" color="violet" size="md">
          Save
        </Button>
      </Group>
    </form>
  );
};

export default FeedbackFrom;