import { combineReducers, configureStore } from '@reduxjs/toolkit';
import fileSlice from './reducers/counterReducer';

const rootReducer = combineReducers({
  file: fileSlice.reducer,
});

export type RootState = ReturnType<typeof rootReducer>;

export const store = configureStore({
  reducer: rootReducer,
});

export default store;