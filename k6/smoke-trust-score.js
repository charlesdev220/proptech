import http from 'k6/http';
import { check } from 'k6';

export const options = {
  vus: 50,
  duration: '30s',
  thresholds: {
    http_req_duration: ['p(95)<800'],
    http_req_failed: ['rate<0.02'],
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export function setup() {
  const payload = JSON.stringify({
    email: 'juan@example.com',
    password: 'password123',
  });

  const res = http.post(`${BASE_URL}/api/v1/auth/login`, payload, {
    headers: { 'Content-Type': 'application/json' },
  });

  const token = JSON.parse(res.body).token;
  if (!token) {
    throw new Error('Setup failed: could not obtain JWT token');
  }
  return { token };
}

export default function (data) {
  const res = http.get(`${BASE_URL}/api/v1/profile/trust-score`, {
    headers: { Authorization: `Bearer ${data.token}` },
  });

  check(res, {
    'status is 200': (r) => r.status === 200,
    'has totalScore': (r) => JSON.parse(r.body).totalScore !== undefined,
  });
}
