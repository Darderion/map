import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import RuleViolation from "../classes/RuleViolation";
import Rule from "../classes/Rule"
import DocumentReport from '../classes/DocumentReport';
interface FileState {
  selectedItem: any;
  apiUrl: string;
  ruleSet: Rule[];
  files: File[];
  fullPerort: DocumentReport[];
  currentFile: File | null;
  currentPage: number | 0;
  currentLine: number;
  currentFileName: string | null;
  ruleViolations: RuleViolation[];
  currentPreset: string[];
}

const initialState: FileState = {
  apiUrl: "http://localhost:8081/api",
  ruleSet: [],
  files: [],
  fullPerort: [],
  currentFile: null,
  currentPage: -2,
  currentLine: 0,
  currentFileName: null,
  ruleViolations: [],
  currentPreset: [],
  selectedItem: undefined
};

const fileSlice = createSlice({
  name: 'file',
  initialState,
  reducers: {
    uploadFile(state, action: PayloadAction<File>) {
      state.files.push(action.payload);
    },
    viewFile(state, action: PayloadAction<File>) {
      state.currentFile = action.payload;
    },
    resetVariable(state) {
      state.currentFile = null;
    },
    addReport(state, action: PayloadAction<any>) {
      state.fullPerort.push(action.payload);
    },
    setCurrentPage(state, action: PayloadAction<number | 0>) {
      state.currentPage = action.payload;
    },
    setCurrentLine(state, action: PayloadAction<number | 0>) {
      state.currentLine = action.payload;
    },
    setCurrentFileName(state, action: PayloadAction<string | null>) {
      state.currentFileName = action.payload;
    },
    setRuleViolations(state, action: PayloadAction<any[]>) {
      state.ruleViolations = action.payload;
    },
    setCurrentPreset(state, action: PayloadAction<any[]>) {
      state.currentPreset = action.payload;
    },
    setRuleSet(state, action: PayloadAction<any[]>) {
      state.ruleSet = action.payload;
    },
    setCurrentItem(state, action: PayloadAction<any>){
      state.selectedItem = action.payload
    }
  }
});

export const {
  uploadFile,
  viewFile,
  resetVariable,
  setCurrentPage,
  setCurrentLine,
  setCurrentFileName,
  setRuleViolations,
  setCurrentPreset,
  setRuleSet,
  setCurrentItem,
  addReport
} = fileSlice.actions;

export default fileSlice;