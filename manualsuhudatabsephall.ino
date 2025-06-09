#include <Wire.h>
#include <LiquidCrystal_I2C.h>
#include <OneWire.h>
#include <DallasTemperature.h>
#include <WiFi.h>
#include <FirebaseESP32.h>

// === Konfigurasi LCD ===
LiquidCrystal_I2C lcd(0x27, 16, 2);  // LCD 16x2 alamat I2C

// === Sensor Suhu DS18B20 ===
#define ONE_WIRE_BUS 13  // Ganti sesuai pin data sensor suhu kamu
OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);

// === Firebase Config ===
#define FIREBASE_HOST "https://project-cupang-default-rtdb.asia-southeast1.firebasedatabase.app"
#define FIREBASE_API_KEY "AIzaSyC3ipdWF0yQE2lY54o5lo9lSCWnnHaLehU"
#define WIFI_SSID "waipai"
#define WIFI_PASSWORD "calistaaa"
#define USER_EMAIL "aureliacalista537@gmail.com"
#define USER_PASSWORD "cupang123"

FirebaseData firebaseData;
FirebaseJson json;
FirebaseConfig config;
FirebaseAuth auth;

const int ph_pin = 32;       // Pin ADC untuk pH sensor
const int aerator_pin = 25;  // Relay aerator (trigger HIGH)
float ph4 = 0.151;           // Tegangan saat pH 4
float PH7 = 0.147;           // Tegangan saat pH 7
float PH_step;
float po = 4.25;                // Nilai pH
double teganganPH;
int nilai_analog_ph;
bool aerator_nyala = false;

// === Variabel suhu ===
float suhu = 0;
float suhu_batas = 30.0;     // Batas suhu untuk indikasi kurang oksigen

bool autoState = true;
bool aeratorState = false;
const float batas_ph_rendah = 6.5;
const float batas_ph_tinggi = 7.0;

bool previousAutoState = true;
bool previousAeratorState = false;

void setup() {
  Serial.begin(115200);
  Serial.println("=== Monitoring pH & Suhu - Aerator Otomatis ===");

  // json.set("/AERATOR", false);
  // json.set("/AUTO", false);

  pinMode(ph_pin, INPUT);
  pinMode(aerator_pin, OUTPUT);
  digitalWrite(aerator_pin, LOW); // Aerator OFF awal

  // Inisialisasi LCD
  lcd.init();
  lcd.backlight();
  lcd.setCursor(0, 0);

  // Inisialisasi Sensor Suhu
  sensors.begin();

  // Koneksi WiFi
  Serial.println("Menghubungkan ke WiFi...");
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  
  int retry = 0;
  while (WiFi.status() != WL_CONNECTED && retry < 20) {
    delay(1000);
    Serial.print(".");
    retry++;
  }
  
  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("\nWiFi Connected!");
    Serial.print("IP Address: ");
    Serial.println(WiFi.localIP());
  } else {
    Serial.println("\nGagal terhubung ke WiFi!");
    return;
  }

  // Konfigurasi Firebase
  config.database_url = FIREBASE_HOST;
  config.api_key = FIREBASE_API_KEY;
  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;
  
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);
  Firebase.setDoubleDigits(5);

  Serial.println("=== ESP32 pH Sensor ===");

  // // Cek jika AUTO belum ada, buat default
  // if (!Firebase.getBool(firebaseData, "/Sensor/AUTO")) {
  //   if (firebaseData.errorReason().indexOf("path not exist") != -1) {
  //     Firebase.setBool(firebaseData, "/Sensor/AUTO", false);
  //     Serial.println("AUTO tidak ditemukan, set ke false");
  //     autoState = false;
  //   }
  // }

  // // Cek jika AERATOR belum ada, buat default
  // if (!Firebase.getBool(firebaseData, "/Sensor/AERATOR")) {
  //   if (firebaseData.errorReason().indexOf("path not exist") != -1) {
  //     Firebase.setBool(firebaseData, "/Sensor/AERATOR", false);
  //     Serial.println("AERATOR tidak ditemukan, set ke false");
  //     aeratorState = false;
  //   }
  // }

}

int getModeValue(int arr[], int size) {
  int mode = arr[0], maxCount = 0;
  for (int i = 0; i < size; i++) {
    int count = 0;
    for (int j = 0; j < size; j++) {
      if (arr[j] == arr[i]) count++;
    }
    if (count > maxCount) {
      maxCount = count;
      mode = arr[i];
    }
  }
  return mode;
}

void loop() {
  Serial.println("================================");

  // === Baca suhu ===
  sensors.requestTemperatures();
  suhu = sensors.getTempCByIndex(0); // Ambil suhu dari sensor pertama

  // === Baca pH ===
  int readings[10];
  for (int i = 0; i < 10; i++) {
    readings[i] = analogRead(ph_pin);
    delay(50);
  }

  nilai_analog_ph = getModeValue(readings, 10);

  Serial.print("Nilai ADC Ph (Mode): ");
  Serial.println(nilai_analog_ph);

  if (nilai_analog_ph == 0) {
    Serial.println("⚠ ADC membaca 0! Periksa koneksi sensor!");
  }

  teganganPH = 3.3 / 4095.0 * nilai_analog_ph;
  Serial.print("TeganganPh: ");
  Serial.println(teganganPH, 3);

  // PH_step = (ph4 - PH7) / 3;
  // po = 7 + ((PH7 - teganganPH) / PH_step);

  // Batasi nilai pH agar masuk akal (0 - 14)
  if (po < 0) po = 0;
  if (po > 14) po = 14;

  Serial.print("Nilai PH cairan: ");
  Serial.println(po, 2);

  // === Logika Aerator ===
  if (po < batas_ph_rendah && suhu > suhu_batas) {
    if (!aerator_nyala) {
      Serial.println("pH rendah & suhu tinggi! Menyalakan aerator...");
      digitalWrite(aerator_pin, HIGH);  // Aerator ON
      aerator_nyala = true;
    }
  } else {
    if (aerator_nyala) {
      Serial.println("pH/suhu normal. Mematikan aerator...");
      digitalWrite(aerator_pin, LOW);   // Aerator OFF
      aerator_nyala = false;
    }
  }

  json.clear();
  json.set("LSET_VOL", teganganPH);
  json.set("FARM_VOL", po);
  json.set("Temperature", suhu);
  json.set("AERATOR", aeratorState);
  json.set("AUTO", autoState);

  // Menulis hanya jika ada perubahan
  if (Firebase.ready()) {
    Serial.println("✅ Firebase siap, mengirim data...");
    
    if (autoState != previousAutoState || aeratorState != previousAeratorState) {
      if (Firebase.setJSON(firebaseData, "/Sensor", json)) {
        Serial.println("✅ Data berhasil dikirim ke Firebase");
        previousAutoState = autoState;  // Simpan status terbaru
        previousAeratorState = aeratorState;
      } else {
        Serial.print("❌ Gagal mengirim data! Error: ");
        Serial.println(firebaseData.errorReason());
      }
    }

    // Cek & Ambil status AERATOR dan AUTO jika ada perubahan
    if (Firebase.get(firebaseData, "/Sensor/AERATOR")) {
      if (firebaseData.dataType() == "boolean") {
        bool tempAeratorState = firebaseData.boolData();
        if (aeratorState != tempAeratorState) {
          aeratorState = tempAeratorState;
          Serial.print("🚨 AERATOR: ");
          Serial.println(aeratorState ? "ON" : "OFF");
        }
      } else {
        Serial.println("❌ AERATOR bukan boolean!");
      }
    }

    if (Firebase.get(firebaseData, "/Sensor/AUTO")) {
      if (firebaseData.dataType() == "boolean") {
        bool tempAutoState = firebaseData.boolData();
        if (autoState != tempAutoState) {
          autoState = tempAutoState;
          Serial.print("⚙ AUTO Mode: ");
          Serial.println(autoState ? "ENABLED" : "DISABLED");
        }
      } else {
        Serial.println("❌ AUTO bukan boolean!");
      }
    }
  } else {
    Serial.println("⚠ Firebase belum siap, cek koneksi!");
  }

  if(autoState){
    if (po < batas_ph_rendah){
      Serial.println("⚠️ pH rendah! Menyalakan aerator...");
      digitalWrite(aerator_pin, HIGH);  // **Relay aktif HIGH**
      Firebase.setBool(firebaseData, "/Sensor/AERATOR", true);
    }
    else if (po > batas_ph_tinggi) {
      Serial.println("✅ pH normal. Mematikan aerator...");
      digitalWrite(aerator_pin, LOW);  // **Relay aktif HIGH**
      Firebase.setBool(firebaseData, "/Sensor/AERATOR", false);
    }
  } else {
    if(aeratorState) {
      Serial.println("🟢 Kontrol manual: Menyalakan aerator...");
      digitalWrite(aerator_pin, HIGH);  // Relay aktif LOW
    }
    else {
      Serial.println("🔴 Kontrol manual: Mematikan aerator...");
      digitalWrite(aerator_pin, LOW);  // Relay aktif LOW
    }
  }

  // === Tampilkan ke LCD ===
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("pH:");
  lcd.print(po, 1);  // Tampilkan pH dengan 1 desimal

  lcd.print(" T:");
  lcd.print(suhu, 1); // Suhu dengan 1 desimal

  lcd.setCursor(0, 1);
  lcd.print("Aerator:");
  if ((autoState && po < batas_ph_rendah) || (!autoState && aeratorState)) {
    lcd.print("ON ");
  } else {
    lcd.print("OFF");
  }

  delay(3000);  
}
