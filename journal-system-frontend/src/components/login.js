import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import '../App.css';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:8080/api/login', {
        username,
        password,
      });
      if (response.status === 200) {
        setMessage(response.data);
        navigate('/register'); 
      }
    } catch (error) {
      if (error.response) {
        setMessage(error.response.status === 401 ? 'Invalid credentials' : `Unexpected error: ${error.response.status}`);
      } else {
        setMessage('An error occurred. Please check the network or server status.');
      }
    }
  };

  const navigateToRegister = () => {
    navigate('/register');
  };

  return (
    <div className="box">
      <h2>Login</h2>
      <form onSubmit={handleLogin}>
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
        <input type="submit" name="submit" value="Submit" />
      </form>
      {message && <p className="errorMessage">{message}</p>}
      <p className="registerPrompt">
        Don't have an account?{' '}
        <span className="registerLink" onClick={navigateToRegister}>
          Register here
        </span>
      </p>
    </div>
  );
};

export default Login;