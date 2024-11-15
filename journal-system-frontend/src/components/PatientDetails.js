import React, { useState } from 'react';
import axios from 'axios';

const PatientDetails = () => {
  // State to store patient details, error, and form input data
  const [patientDetails, setPatientDetails] = useState({});
  const [error, setError] = useState(null);
  const [username, setUsername] = useState('');
  const [name, setName] = useState('');
  const [address, setAddress] = useState('');
  const [dateOfBirth, setDateOfBirth] = useState('');

  // Handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault();

    // Prepare the data to send to the backend
    const requestData = {
      username,
      name,
      address,
      dateOfBirth,
    };

    try {
      // Send a POST request to the backend with the full patient data
      const response = await axios.post('http://localhost:8080/api/patient/details', requestData, {
        headers: {
          'Content-Type': 'application/json',
        },
      });

      // Handle success response and update the patient details state
      setPatientDetails(response.data);
    } catch (error) {
      // Handle error response and display the error message
      if (error.response) {
        setError(error.response.data);
      } else {
        setError('An error occurred. Please try again later.');
      }
    }
  };

  return (
    <div>
      <h2>Get Patient Details</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="username">Username:</label>
          <input
            type="text"
            id="username"
            value={username}
            onChange={(e) => setUsername(e.target.value)} // Set username state
            required
          />
        </div>

        <div>
          <label htmlFor="name">Name:</label>
          <input
            type="text"
            id="name"
            value={name}
            onChange={(e) => setName(e.target.value)} // Set name state
            required
          />
        </div>

        <div>
          <label htmlFor="address">Address:</label>
          <input
            type="text"
            id="address"
            value={address}
            onChange={(e) => setAddress(e.target.value)} // Set address state
            required
          />
        </div>

        <div>
          <label htmlFor="dateOfBirth">Date of Birth:</label>
          <input
            type="date"
            id="dateOfBirth"
            value={dateOfBirth}
            onChange={(e) => setDateOfBirth(e.target.value)} // Set date of birth state
            required
          />
        </div>

        <button type="submit">Get Patient Details</button>
      </form>

      {/* Display error message */}
      {error && <p className="error">{error}</p>}

      {/* Display patient details */}
      {patientDetails && (
        <div>
          <h3>Patient Details</h3>
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
