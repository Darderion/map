import { UPLOAD_FILE, VIEW_FILE,RESET_VARIABLE,SET_CURRENT_PAGE, SET_CURRENT_LINE, SET_CURRENT_FILE_NAME,GET_RULE_VIOLATIONS,SET_SELECTED_ITEM,SET_CURRENT_PRESET, SET_RULE_SET} from './actionTypes';

export const uploadFile = (file) => (dispatch) => {
  const reader = new FileReader();

  reader.onload = (e) => {
    dispatch({
      type: UPLOAD_FILE,
      payload: {
        name: file.name,
        data: e.target.result,
      },
    });
  };

  reader.readAsArrayBuffer(file);
};

export const viewFile = (file) => (dispatch) => {
  dispatch({
    type: VIEW_FILE,
    payload: {
    },
  });
};
export const setCurrentPage = (currentPage) => (dispatch) => {
  dispatch({
    type: SET_CURRENT_PAGE,
    payload: {
      data: currentPage,
    },
  });
};
export const setCurrentLine = (currentLine) => (dispatch) => {
  dispatch({
    type: SET_CURRENT_LINE,
    payload: {
      data: currentLine,
    },
  });
};
export const setCurrentFileName = (currentFileName) => (dispatch) => {
  dispatch({
    type: SET_CURRENT_FILE_NAME,
    payload: {
      data: currentFileName,
    },
  });
};
export const setRuleViolations = (ruleViolations) => (dispatch)=>{
  dispatch({
    type:GET_RULE_VIOLATIONS,
    payload:{
      data:ruleViolations
    }
  })
}
export const setCurrentPreset = (currentPreset) => (dispatch)=>{
  dispatch({
    type:SET_CURRENT_PRESET,
    payload:{
      data:currentPreset
    }
  })
}
export const setRuleSet = (ruleSet) => (dispatch)=>{
  dispatch({
    type:SET_RULE_SET,
    payload:{
      data:ruleSet
    }
  })
}
export const setSelectedItem = (selectedItem) => (dispatch) =>{
  dispatch({
    type:SET_SELECTED_ITEM,
    payload:{
      data:selectedItem
    }
  })
}
export const resetVariable = () => ({
  type: RESET_VARIABLE,
});