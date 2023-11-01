import { SET_RULE_SET, UPLOAD_FILE, VIEW_FILE, RESET_VARIABLE, SET_CURRENT_PAGE, SET_CURRENT_FILE_NAME,SET_CURRENT_LINE, GET_RULE_VIOLATIONS,SET_SELECTED_ITEM,SET_CURRENT_PRESET} from '../actions/actionTypes';

const initialState = {
  apiUrl:"http://localhost:8081/api",
  ruleSet:[],
  files: [],
  currentFile:null,
  currentPage:null,
  currentLine:0,
  currentFileName:null,
  ruleViolations:[],
  selectedItem:null,
  currentPreset:[],
};

const fileReducer = (state = initialState, action) => {
  switch (action.type) {
    case UPLOAD_FILE:
      return {
        ...state,
        files: [...state.files, action.payload],
      };
    case VIEW_FILE:
      return {
        ...state,
        currentFile: action.payload,
      };
    case RESET_VARIABLE:
        return {
          ...state,
          currentFile: null,
        };
        case SET_CURRENT_PAGE:
          return{
            ...state,
            currentPage: action.payload.data,
          }
          case SET_CURRENT_FILE_NAME:
          return{
            ...state,
            currentFileName: action.payload.data,
          }
          case SET_SELECTED_ITEM:
            return{
              ...state,
              selectedItem: action.payload.data,
            }
          case SET_CURRENT_LINE:
          return{
            ...state,
            currentLine: action.payload.data,
          }
          case GET_RULE_VIOLATIONS:
            return {
              ...state,
              ruleViolations: [action.payload],
            };
            case SET_CURRENT_PRESET:
            return {
              ...state,
              currentPreset: [action.payload.data],
            };
            case SET_RULE_SET:
              return {
                ...state,
                ruleSet: [action.payload.data],
              };
            default:

      return state;
  }
};

export default fileReducer;