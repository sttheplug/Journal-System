// src/App.js

import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';                       
import './Register.css';
import Login from './components/login';
import Register from './components/register';
import StaffDashboard from './components/StaffDashboard';
import PatientDetails from './components/PatientDetails';
import Message from './components/Message'; 
import PatientID from './components/PatientID';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <Router>
          <Routes>
            <Route path="/" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/staff-dashboard" element={<StaffDashboard />} />
            <Route path="/patient-details" element={<PatientDetails />} />
            <Route path="/message" element={<Message />} /> {/* Add the route for the Message component */}
            <Route path="/patient-details/:patientId" element={<PatientID />} />
          </Routes>
        </Router>
      </header>
    </div>
  );
}

export default App;
