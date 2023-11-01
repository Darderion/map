import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import axios from 'axios';
import { Provider } from "react-redux";
import store from "./store";
import { BrowserRouter as Router, Route } from 'react-router-dom';
import About from './pages/about';
import Home from './pages/home';
import Menu from "./components/Menu/Menu";
import Download from "./pages/download";
import Presets from "./pages/presets"
import ProcessFile from "./pages/processfile";
import { setRuleSet } from './actions/fileAction';
function App() {
  const replaceLinks = (text) => {
    const linkRegex = /(https?:\/\/[^\s]+)/gi;
    let counter = 1;
    const replacedText = text.replace(linkRegex, (match, url) => {
      return `<a href="${url}">${counter++}</a>`;
    });
    return replacedText;
  };

  const dispatch = useDispatch();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDocuments = async () => {
      try {
        const response = await axios.get('http://localhost:8081/api/viewRULE_SET');
        const rules = response.data.rules;
        const modifiedRules = rules.map((rule) => ({
          ...rule,
          description: replaceLinks(rule.description),
        }));
        dispatch(setRuleSet(modifiedRules));
        setLoading(false);
      } catch (error) {
        console.error('Error fetching documents:', error);
        setLoading(false);
      }
    };
    fetchDocuments();
  }, []);

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <Provider store={store}>
      <div className="App">
        <Router>
          <Menu />
          <Route path="/" exact component={Home} />
          <Route path="/Home" exact component={Home} />
          <Route path="/Download" component={Download} />
          <Route path="/About" component={About} />
          <Route path="/Presets" component={Presets} />
          <Route path="/ProcessFile" component={ProcessFile} />
        </Router>
      </div>
    </Provider>
  );
}

export default App;