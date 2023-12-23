import React, { useState } from 'react';
import { Button, Group } from '@mantine/core';
import { useDispatch, useSelector } from 'react-redux';
import { Textarea } from '@mantine/core';
import axios from 'axios';
import { RootState } from '../../store';

interface FeedbackFormProps {
}

const FeedbackForm: React.FC<FeedbackFormProps> = () => {
  const pdfName: string|null = useSelector((state: RootState) => state.file.currentFileName);
  const page: number = useSelector((state: RootState) => state.file.currentPage);
  const line: number = useSelector((state: RootState) => state.file.currentLine);
  const title: string = useSelector((state: RootState) => state.file.selectedItem);
  const apiUrl: string = useSelector((state: RootState) => state.file.apiUrl);
  const [comment, setComment] = useState<string>('');

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>): Promise<void> => {
    e.preventDefault();

    const formData = new FormData();
    formData.append('pdfName', pdfName+"");
    formData.append('page', page+"");
    formData.append('title', title);
    formData.append('line', line+"");
    formData.append('comment', comment);

    axios.post(apiUrl+'/submitFeedback', formData)
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
      <Group mt="md">
        <Button type="submit" color="violet" size="md">
          Save
        </Button>
      </Group>
    </form>
  );
};

export default FeedbackForm;