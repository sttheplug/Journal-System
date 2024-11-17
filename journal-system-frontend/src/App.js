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
import ViewMessagePatient from './components/ViewMessagePatient'; 
import Respond from './components/Respond'; // Import Respond component

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
            <Route path="/message" element={<Message />} />
            <Route path="/patient-details/:patientId" element={<PatientID />} />
            <Route path="/view-message" element={<ViewMessagePatient />} />
            <Route path="/view-respond/:patientId" element={<Respond />} /> {/* Add route for Respond */}
          </Routes>
        </Router>
      </header>
    </div>
  );
}

export default App;
