// src/components/Register.js
import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import '../App.css';

const Register = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const [roles, setRoles] = useState([]); // Array for storing roles
  const navigate = useNavigate();

  // Handle the registration form submission
  const handleRegister = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:8080/api/register', {
        username,
        password,
        roles,
      });
      if (response.status === 201) {
        setMessage('User registered successfully');
        // Redirect to the login page after registration
        setTimeout(() => navigate('/'), 2000);
      }
    } catch (error) {
      setMessage('Error during registration');
    }
  };

  // Add or remove roles
  const handleRoleChange = (role) => {
    setRoles((prevRoles) =>
      prevRoles.includes(role)
        ? prevRoles.filter((r) => r !== role)
        : [...prevRoles, role]
    );
  };

  return (
    <div className="box">
      <h2>Register</h2>
      <form onSubmit={handleRegister}>
        <div className="inputBox">
          <input
            type="text"
            name="username"
            required
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
          <label htmlFor="username">Username</label>
        </div>
        <div className="inputBox">
          <input
            type="password"
            name="password"
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <label htmlFor="password">Password</label>
        </div>

        <div className="rolesBox">
          <h4>Select Roles</h4>
          <label>
            <input
              type="checkbox"
              value="ROLE_USER"
              onChange={() => handleRoleChange('ROLE_USER')}
            />
            User
          </label>
          <label>
            <input
              type="checkbox"
              value="ROLE_ADMIN"
              onChange={() => handleRoleChange('ROLE_ADMIN')}
            />
            Admin
          </label>
        </div>

        <input type="submit" name="submit" value="Register" />
      </form>
      {message && <p>{message}</p>}
    </div>
  );
};

export default Register;
