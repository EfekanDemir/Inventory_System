import http from 'k6/http';
import { check, sleep } from 'k6';

// Stress Test - Amaç: Sistemin nerede kırıldığını (Breaking Point) bulmak.
export const options = {
  stages: [
    { duration: '1m', target: 100 }, // Isınma
    { duration: '1m', target: 300 }, // Yüklenme
    { duration: '1m', target: 600 }, // Agresif yük (Kırılma noktasının beklendiği bölge)
    { duration: '1m', target: 900 }, // Maksimum kapasite zorlaması
    { duration: '1m', target: 0 },   // Hızlı geri dönüş
  ],
  thresholds: {
    // Stress testlerinde eşikler aşılsa da test durmaz, sistemin sınırları gözlemlenir.
    http_req_duration: ['p(95)<1000'],
  },
};

const BASE_URL = 'http://localhost:8000';
const GLOBAL_HEADERS = {
  'Content-Type': 'application/json',
  'api-key': 'envanter-api-key-2026'
};

export default function () {
  // 1. REGISTER
  const username = `stress_${__VU}_${__ITER}_${Date.now()}`;
  const registerPayload = JSON.stringify({
    username: username,
    email: `${username}@test.com`,
    password: 'Password123!',
    firstName: `Stress_${__VU}`,
    lastName: `User_${__ITER}`
  });

  let registerRes = http.post(`${BASE_URL}/api/users/register`, registerPayload, {
    headers: GLOBAL_HEADERS,
  });

  sleep(0.5); // Stres testinde bekleme süresi daha düşük tutulur

  // 2. LOGIN
  let loginRes = http.post(`${BASE_URL}/api/users/login`, JSON.stringify({
    username: username,
    password: 'Password123!',
  }), { headers: GLOBAL_HEADERS });

  let token = null;
  try {
    token = loginRes.json('data.token');
  } catch (e) {}

  sleep(0.5);

  // 3. GET ITEMS & 4. CREATE MOVEMENT (Ağır İşlem Yükü)
  if (token) {
    const authHeaders = Object.assign({}, GLOBAL_HEADERS, {
      'Authorization': `Bearer ${token}`
    });

    http.get(`${BASE_URL}/api/inventory/items`, { headers: authHeaders });
    sleep(0.5);

    // Envanter hareketliliği: eşzamanlı stok hareketi
    const movementPayload = JSON.stringify({
      itemCode: "ITM-001",
      quantity: Math.floor(Math.random() * 10) + 1,
      movementType: "IN",
      reason: "Stress Test Restock"
    });

    http.post(`${BASE_URL}/api/inventory/movements`, movementPayload, {
      headers: authHeaders,
    });
    sleep(0.5);
  }
}
