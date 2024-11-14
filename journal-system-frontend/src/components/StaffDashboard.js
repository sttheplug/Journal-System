import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './StaffDashboard.css'; // Import the CSS file for styling
import { useNavigate } from 'react-router-dom';

const StaffDashboard = () => {
  const [patients, setPatients] = useState([]);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchPatients = async () => {
      try {
        const token = localStorage.getItem('authToken');
        const response = await axios.get('http://localhost:8080/api/patients', {
          headers: {
            Authorization: `Bearer ${token}`, // Ensure the token is sent if required by the backend
          },
        });
        setPatients(response.data);
      } catch (err) {
        setError('Failed to load patients. Please try again later.');
      }
    };

    fetchPatients();
  }, []);

  const handlePatientClick = (patient) => {
    // Add logic here for what happens when a patient row is clicked
    // For example, redirect to a patient detail page or display more info
    navigate('/PatientDetails');
  };

  return (
    <div className="dashboard-container">
      <h1>Staff Dashboard - List of Patients</h1>
      {error && <p className="error">{error}</p>}
      <div className="patient-list">
        {patients.length > 0 ? (
          patients.map((patient) => (
            <div
              key={patient.id}
              className="patient-item"
              onClick={() => handlePatientClick(patient)}
            >
              <span className="patient-name">{patient.username}</span>
              <span className="patient-role">{patient.role}</span>
            </div>
          ))
        ) : (
          <p>No patients found.</p>
        )}
      </div>
    </div>
  );
};

export default StaffDashboard;
