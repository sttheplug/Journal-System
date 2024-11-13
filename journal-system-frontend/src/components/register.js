import React from 'react';
import bgImg from '../img1.jpg';
import { useForm } from 'react-hook-form';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import '../Register.css';

export default function RegisterForm() {
  const { register, handleSubmit, formState: { errors } } = useForm();
  const navigate = useNavigate();

  const onSubmit = async (data) => {
    const { username, password, confirmpwd, mobile, role } = data;  

    if (password !== confirmpwd) {
      alert("Passwords do not match.");
      return;
    }

    try {
      const response = await axios.post('http://localhost:8080/api/register', {
        username,
        password,
        role,
        mobile,
      });

      if (response.status === 201) {
        alert("User registered successfully");
        navigate('/'); // Redirect to login after registration
      }
    } catch (error) {
      console.error("Registration error:", error);
      alert("Registration failed. Please try again.");
    }
  };

  return (
    <section>
      <div className="register">
        <div className="col-1">
          <h2>Sign Up</h2>
          <span>Register and enjoy the service</span>

          <form id='form' className='flex flex-col' onSubmit={handleSubmit(onSubmit)}>
            <input type="text" {...register("username", { required: true })} placeholder='Username' />
            {errors.username && <p className="error">Username is required</p>}

            <input type="password" {...register("password", { required: true })} placeholder='Password' />
            {errors.password && <p className="error">Password is required</p>}

            <input type="password" {...register("confirmpwd", { required: true })} placeholder='Confirm Password' />
            {errors.confirmpwd && <p className="error">Password confirmation is required</p>}

            <input type="text" {...register("mobile", { required: true, maxLength: 10 })} placeholder='Mobile Number' />
            {errors.mobile?.type === "required" && <p className="error">Mobile Number is required</p>}
            {errors.mobile?.type === "maxLength" && <p className="error">Max Length Exceeded</p>}

            <select {...register("role", { required: true })}>
              <option value="">Select Role</option>
              <option value="PATIENT">Patient</option>
              <option value="DOCTOR">Doctor</option>
              <option value="STAFF">Staff</option>
              <option value="NURSE">Nurse</option>
              <option value="PHYSIOTHERAPIST">Physiotherapist</option>
              <option value="LAB_TECHNICIAN">Lab Technician</option>
            </select>
            {errors.role && <p className="error">Role is required</p>}

            <button className='btn' type="submit">Sign Up</button>
          </form>
        </div>

        <div className="col-2">
          <img src={bgImg} alt="Background" />
        </div>
      </div>
    </section>
  );
}
