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
        const response = await axios.get('http://localhost:8080/api/patients');
        setPatients(response.data);
        setError('');
      } catch (err) {
        setError('Failed to load patients. Please try again later.');
      }
    };

    fetchPatients();
  }, []);

  const handlePatientClick = (patientId) => {
    // Redirect to the PatientDetails page with the patient ID as a route parameter
    navigate(`/patient-details/${patientId}`);
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
              onClick={() => handlePatientClick(patient.id)} // Pass patient ID on click
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
