import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import store from './store';
import App from './App';
import { MantineProvider } from '@mantine/core';
ReactDOM.render(

  <React.StrictMode>
      <MantineProvider>
    <Provider store={store}>
      <App />
    </Provider>
    </MantineProvider>
  </React.StrictMode>,
  
  document.getElementById('root'),
);
