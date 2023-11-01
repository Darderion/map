import { combineReducers } from 'redux';
import fileReducer from './fileReducer';

const rootReducer = combineReducers({
  file: fileReducer,
});

export default rootReducer;