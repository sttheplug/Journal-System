import React, { useEffect, useState } from 'react';
import axios from 'axios';
import '../App.css';
import { useParams } from 'react-router-dom'; // Import useParams to extract route parameters

const PatientDetails = () => {
  const { patientId } = useParams(); // Extract patientId from the route parameter
  const [patientDetails, setPatientDetails] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchPatientDetails = async () => {
      setLoading(true);
      try {
        const response = await axios.get(
          `http://localhost:8080/api/patient/details/${patientId}` // Use the patient ID in the URL
        );

        setPatientDetails(response.data);
        setError('');
      } catch (err) {
        setError('Failed to load patient details. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    fetchPatientDetails();
  }, [patientId]); // Re-fetch details if patientId changes

  return (
    <div className="box">
      <h2>Patient Details</h2>

      {loading && <p>Loading...</p>}
      {error && <p className="error">{error}</p>}

      {patientDetails && (
        <div>
          <h3>Patient Information</h3>
          <p><strong>Username:</strong> {patientDetails.username}</p>
          <p><strong>Name:</strong> {patientDetails.name}</p>
          <p><strong>Address:</strong> {patientDetails.address}</p>
          <p><strong>Phone Number:</strong> {patientDetails.phoneNumber}</p>
          <p><strong>Date of Birth:</strong> {patientDetails.dateOfBirth}</p>
        </div>
      )}
    </div>
  );
};

export default PatientDetails;
