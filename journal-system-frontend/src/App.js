// src/App.js

import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import './Register.css';
import Login from './components/login';
import Register from './components/register';
import StaffDashboard from './components/StaffDashboard';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <Router>
          <Routes>
            <Route path="/" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/staff-dashboard" element={<StaffDashboard />} />
          </Routes>
        </Router>
      </header>
    </div>
  );
}

export default App;
