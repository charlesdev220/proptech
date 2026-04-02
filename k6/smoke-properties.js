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

export default function () {
  const res = http.get(
    `${BASE_URL}/api/v1/properties?lat=40.4168&lng=-3.7038&radius=5000&page=0&size=20`
  );

  check(res, {
    'status is 200': (r) => r.status === 200,
    'has content array': (r) => Array.isArray(JSON.parse(r.body).content),
  });
}
