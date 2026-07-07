"use client"

import api from '@/lib/api';
import Link from 'next/link';
import { useState } from 'react';

export default function Login() {

  const [error, setError] = useState<string>('');
  const [successMessage, setSuccessMessage] = useState<string>('');
  const [username, setUsername] = useState<string>('');
  const [password, setPassword] = useState<string>('');

  async function handleSubmit(event: React.SubmitEvent<HTMLFormElement>) {
    event.preventDefault();

    const params = new URLSearchParams();
    params.append("username", username);
    params.append("password", password);

    try {
      const response = await api.post(
        '/api/login', params, {
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
          },
        }
      );
      setSuccessMessage('Login successful!');
      setError('');
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed');
      setSuccessMessage('');
    }
  }

  return <div>
    <head>
      <title>Login</title>
    </head>
    <body>

      <Link href="/">Home</Link>

      <form onSubmit={handleSubmit}>

        <h1>Sign in</h1>

        {error && <p className="error">{error}</p>}

        {successMessage && <p className="success">{successMessage}</p>}
        <div>
          <input type="text" name="username" id="username" value={username} placeholder="Username" onChange={(e) => setUsername(e.target.value)} />
        </div>

        <div>
          <input type="password" name="password" id="password" value={password} placeholder="Password" onChange={(e) => setPassword(e.target.value)} />
        </div>

        <button type="submit">Login</button>

      </form>
    </body>
  </div>;
}