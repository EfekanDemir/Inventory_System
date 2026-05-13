import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '2m', target: 100 }, // Rampa yukarı: 2 dakikada 100 kullanıcıya ulaş
    { duration: '3m', target: 250 }, // Rampa yukarı: 3 dakikada 250 kullanıcıya ulaş
    { duration: '3m', target: 500 }, // Pik yük: 3 dakika boyunca 500 kullanıcıya ulaş
    { duration: '2m', target: 0 },   // Rampa aşağı: 2 dakikada 0 kullanıcıya in (sistemi rahatlat)
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'], // İsteklerin %95'i 500ms'nin altında tamamlanmalı
    http_req_failed: ['rate<0.05'],   // Hata oranı %5'in altında olmalı
  },
};

const BASE_URL = 'http://localhost:8000';
const GLOBAL_HEADERS = {
  'Content-Type': 'application/json',
  'api-key': 'envanter-api-key-2026'
};

export default function () {
  // --- 1. REGISTER (Kayıt Olma) ---
  // Benzersiz kullanıcı oluşturmak için __VU (Virtual User ID) ve __ITER (Iteration Number) kullanılır.
  const username = `user_${__VU}_${__ITER}_${Date.now()}`;
  const email = `${username}@test.com`;
  const password = 'Password123!';

  const registerPayload = JSON.stringify({
    username: username,
    email: email,
    password: password,
    firstName: `LoadTest_${__VU}`,
    lastName: `User_${__ITER}`
  });

  let registerRes = http.post(`${BASE_URL}/api/users/register`, registerPayload, {
    headers: GLOBAL_HEADERS,
  });

  check(registerRes, {
    'Register status is 200 or 201': (r) => r.status === 200 || r.status === 201,
    'Register response time < 500ms': (r) => r.timings.duration < 500,
  });
  sleep(1);

  // --- 2. LOGIN (Giriş Yapma) ---
  const loginPayload = JSON.stringify({
    username: username,
    password: password,
  });

  let loginRes = http.post(`${BASE_URL}/api/users/login`, loginPayload, {
    headers: GLOBAL_HEADERS,
  });

  check(loginRes, {
    'Login status is 200': (r) => r.status === 200,
    'Login response has token': (r) => {
      try {
        return r.json('data.token') !== undefined && r.json('data.token') !== null;
      } catch (e) {
        return false;
      }
    },
  });

  let token = null;
  try {
    token = loginRes.json('data.token');
  } catch (e) {
    // JSON parse hatası
  }
  
  sleep(1);

  // --- 3. GET ITEMS & 4. CREATE ITEM (Sadece token varsa) ---
  if (token) {
    const authHeaders = Object.assign({}, GLOBAL_HEADERS, {
      'Authorization': `Bearer ${token}`
    });

    // 3. GET ITEMS (Envanter Listesi)
    let getItemsRes = http.get(`${BASE_URL}/api/inventory/items`, {
      headers: authHeaders,
    });

    check(getItemsRes, {
      'Get items status is 200': (r) => r.status === 200,
      'Get items response time < 500ms': (r) => r.timings.duration < 500,
    });
    sleep(1);

    // 4. CREATE ITEM (Yeni Ürün Ekleme)
    const itemPayload = JSON.stringify({
      itemCode: `ITM_${__VU}_${__ITER}_${Date.now()}`,
      name: `Performans Test Ürünü ${__VU}-${__ITER}`,
      quantity: 150,
      categoryId: 1 // Örnek kategori ID
    });

    let createItemRes = http.post(`${BASE_URL}/api/inventory/items`, itemPayload, {
      headers: authHeaders,
    });

    check(createItemRes, {
      'Create item status is 200 or 201': (r) => r.status === 200 || r.status === 201,
      'Create item response time < 500ms': (r) => r.timings.duration < 500,
    });
    sleep(1);
  }
}
