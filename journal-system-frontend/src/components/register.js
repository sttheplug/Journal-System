// src/components/Register.js
import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { Container, Box, TextField, Button, Typography, MenuItem, Select, InputLabel, FormControl } from '@mui/material';

const Register = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const [roles, setRoles] = useState([]);
  const [selectedRole, setSelectedRole] = useState('');
  const navigate = useNavigate();

  const roleOptions = [
    'PATIENT',
    'DOCTOR',
    'STAFF',
    'NURSE',
    'PHYSIOTHERAPIST',
    'LAB_TECHNICIAN',
  ];

  const handleRegister = async (e) => {
    e.preventDefault();

    if (selectedRole && !roles.includes(selectedRole)) {
      setRoles([selectedRole]);
    }

    try {
      const response = await axios.post('http://localhost:8080/api/register', {
        username,
        password,
        roles,
      });
      if (response.status === 201) {
        setMessage('User registered successfully');
        setTimeout(() => navigate('/'), 2000);
      }
    } catch (error) {
      setMessage('Error during registration');
    }
  };

  return (
    <Container maxWidth="xs">
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'center',
          marginTop: 4,
          padding: 2,
          boxShadow: 3,
          borderRadius: 2,
          backgroundColor: '#fff',
        }}
      >
        <Typography variant="h4" gutterBottom>
          Register
        </Typography>

        <form onSubmit={handleRegister}>
          <TextField
            label="Username"
            variant="outlined"
            fullWidth
            margin="normal"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
          <TextField
            label="Password"
            type="password"
            variant="outlined"
            fullWidth
            margin="normal"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          
          <Box sx={{ marginTop: 2 }}>
            <Typography variant="h6">Select Role</Typography>

            <FormControl fullWidth margin="normal">
              <InputLabel>Role</InputLabel>
              <Select
                value={selectedRole}
                onChange={(e) => setSelectedRole(e.target.value)}
                label="Role"
              >
                {roleOptions.map((role) => (
                  <MenuItem key={role} value={role}>
                    {role}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Box>

          <Button
            type="submit"
            variant="contained"
            color="primary"
            fullWidth
            sx={{ marginTop: 2 }}
          >
            Register
          </Button>
        </form>

        {message && (
          <Typography variant="body1" sx={{ marginTop: 2, color: 'green' }}>
            {message}
          </Typography>
        )}
      </Box>
    </Container>
  );
};

export default Register;
