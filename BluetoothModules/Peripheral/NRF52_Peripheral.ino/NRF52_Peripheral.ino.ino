#include <bluefruit.h>

// Pin Definitions
#define DRAW_BUTTON_PORT 7
#define RESYNC_BUTTON_PORT 16
#define USER_FUNCTIONALITY_BUTTON_PORT 15

// Draw Functionality Values
#define DRAW_START 1
#define DRAW_END 0

// Recallibration Functionality Values
#define RS_RECALLIBRATE 1

// User Functionality Values
#define UF_CHANGE_COLOR 0
#define UF_ERASE_TOGGLE 1
#define UF_CLEAR_BOARD 2

BLEUuid UUID16_PEN_SERVICE = BLEUuid(0xface);
BLEUuid UUID16_DRAWING_CHARACTERISTIC = BLEUuid(0xaaa0);
BLEUuid UUID16_RESYNC_CHARACTERISTIC = BLEUuid(0xaaa1);
BLEUuid UUID16_USER_FUNCTIONALITY_CHARACTERISTIC = BLEUuid(0xaaa2);

BLEService        pds = BLEService(UUID16_PEN_SERVICE);
BLECharacteristic pdc = BLECharacteristic(UUID16_DRAWING_CHARACTERISTIC);
BLECharacteristic prsc = BLECharacteristic(UUID16_RESYNC_CHARACTERISTIC);
BLECharacteristic pufc = BLECharacteristic(UUID16_USER_FUNCTIONALITY_CHARACTERISTIC);
//BLECharacteristic poc = BLECharacteristic(UUID16_OPTION_CHARACTERISTIC);

BLEDis bledis;    // DIS (Device Information Service) helper class instance
BLEBas blebas;    // BAS (Battery Service) helper class instance

// Advanced function prototypes
void startAdv(void);
void setupService(void);
void connect_callback(uint16_t conn_handle);
void disconnect_callback(uint16_t conn_handle, uint8_t reason);

boolean prevDrawVal;
boolean prevRSVal;
boolean prevUFVal;
unsigned long drawTime;
unsigned long rsTime;
unsigned long ufTime;

void setup() { 
  drawTime = millis();
  rsTime = millis();
  ufTime = millis();
  
  pinMode(DRAW_BUTTON_PORT, INPUT);
  pinMode(RESYNC_BUTTON_PORT, INPUT);
  pinMode(USER_FUNCTIONALITY_BUTTON_PORT, INPUT);

  prevDrawVal = digitalRead(DRAW_BUTTON_PORT);
  prevRSVal = digitalRead(RESYNC_BUTTON_PORT);
  prevUFVal = digitalRead(USER_FUNCTIONALITY_BUTTON_PORT);
  
  Serial.begin(115200);

  // Initialise the Bluefruit module
  Serial.println("Initialise the Bluefruit nRF52 module");
  Bluefruit.begin();

  // Set the advertised device name (keep it short!)
  Serial.println("Setting Device Name to 'Feather52 HRM'");
  Bluefruit.setName("Digital Glass");

  // Set the connect/disconnect callback handlers
  Bluefruit.setConnectCallback(connect_callback);
  Bluefruit.setDisconnectCallback(disconnect_callback);

  // Configure and Start the Device Information Service
  Serial.println("Configuring the Device Information Service");
  bledis.setManufacturer("Adafruit Industries");
  bledis.setModel("Bluefruit Feather52");
  bledis.begin();

  // Start the BLE Battery Service and set it to 100%
  Serial.println("Configuring the Battery Service");
  blebas.begin();
  blebas.write(100);

  // Setup the Heart Rate Monitor service using
  // BLEService and BLECharacteristic classes
  Serial.println("Configuring the Heart Rate Monitor Service");
  setupService();

  // Setup the advertising packet(s)
  Serial.println("Setting up the advertising payload(s)");
  startAdv();

  Serial.println("\nAdvertising, please connect your device.");
}

void startAdv(void) {
  // Advertising packet
  Bluefruit.Advertising.addFlags(BLE_GAP_ADV_FLAGS_LE_ONLY_GENERAL_DISC_MODE);
  Bluefruit.Advertising.addTxPower();

  Bluefruit.Advertising.addService(pds);

  // Include Name
  Bluefruit.Advertising.addName();
  
  /* Start Advertising
   * - Enable auto advertising if disconnected
   * - Interval:  fast mode = 20 ms, slow mode = 152.5 ms
   * - Timeout for fast mode is 30 seconds
   * - Start(timeout) with timeout = 0 will advertise forever (until connected)
   * 
   * For recommended advertising interval
   * https://developer.apple.com/library/content/qa/qa1931/_index.html   
   */
  Bluefruit.Advertising.restartOnDisconnect(true);
  Bluefruit.Advertising.setInterval(32, 244);    // in unit of 0.625 ms
  Bluefruit.Advertising.setFastTimeout(30);      // number of seconds in fast mode
  Bluefruit.Advertising.start(0);                // 0 = Don't stop advertising after n seconds  
}

void setupService(void) {
  // Name                           UUID    Properties
  // ----------------------------   ------  ----------
  // Pen Draw Service               0xface  N/A
  // Pen Draw Characteristic        0x0001  Notify/Read
  // Pen Options Characteristic     0x0002  Notify/Read
  pds.begin();

  pdc.setProperties(CHR_PROPS_NOTIFY);
  pdc.setPermission(SECMODE_OPEN, SECMODE_NO_ACCESS);
  pdc.setFixedLen(1);
  pdc.begin();
  
  prsc.setProperties(CHR_PROPS_NOTIFY);
  prsc.setPermission(SECMODE_OPEN, SECMODE_NO_ACCESS);
  prsc.setFixedLen(1);
  prsc.begin();
  
  pufc.setProperties(CHR_PROPS_NOTIFY);
  pufc.setPermission(SECMODE_OPEN, SECMODE_NO_ACCESS);
  pufc.setFixedLen(1);
  pufc.begin();
}

void connect_callback(uint16_t conn_handle) {
  char central_name[32] = { 0 };
  Bluefruit.Gap.getPeerName(conn_handle, central_name, sizeof(central_name));

  Serial.print("Connected to ");
  Serial.println(central_name);
}

void disconnect_callback(uint16_t conn_handle, uint8_t reason) {
  (void) conn_handle;
  (void) reason;

  Serial.println("Disconnected");
  Serial.println("Advertising!");
}

void loop() {
  if ( Bluefruit.connected() ) {
    boolean currDrawVal = digitalRead(DRAW_BUTTON_PORT);
    boolean currRSVal = digitalRead(RESYNC_BUTTON_PORT);
    boolean currUFVal = digitalRead(USER_FUNCTIONALITY_BUTTON_PORT);

    // DRAW BUTTON (FRONT BUTTON)
    if (prevDrawVal == false && currDrawVal == true) {
       // Button pressed
       pdc.notify8(DRAW_START);
       Serial.println("Drawing Started");
    }
    else if (prevDrawVal == true && currDrawVal == false) {
       Serial.println("Stop Drawing");
       pdc.notify8(DRAW_END);
    }
    else if (prevDrawVal == true && currDrawVal == true) {
      // Button is being held down
    }
    prevDrawVal = currDrawVal;

    // CHANGE COLOR/RESYNC BUTTON
    if (prevRSVal == false && currRSVal == true) {
       rsTime = millis();
       // Button Pressed
    }
    else if (prevRSVal == true && currRSVal == false) {
       unsigned long currentTime = millis();
       unsigned long elapsedTime = currentTime - rsTime;
       
       if (elapsedTime >= 2000) {
          // Request recallibration
          prsc.notify8(RS_RECALLIBRATE);
          Serial.println("Recallibration Requested");
       } else {
          pufc.notify8(UF_CHANGE_COLOR);
          Serial.println("Color Change Requested");
       }
    }
    else if (prevRSVal == true && currRSVal == true) {
      // Button is being held down
    }
    prevRSVal = currRSVal;

    // CHANGE COLOR/RESYNC BUTTON
    if (prevUFVal == false && currUFVal == true) {
       ufTime = millis();
       // Button Pressed
    }
    else if (prevUFVal == true && currUFVal == false) {
       unsigned long currentTime = millis();
       unsigned long elapsedTime = currentTime - ufTime;
       
       if (elapsedTime >= 2000) {
          // Request recallibration
          pufc.notify8(UF_CLEAR_BOARD);
          Serial.println("Clear Board Requested");
       } else {
          pufc.notify8(UF_ERASE_TOGGLE);
          Serial.println("Erase Toggle Requested");
       }
    }
    else if (prevUFVal == true && currUFVal == true) {
      // Button is being held down
    }
    prevUFVal = currUFVal;
  }
}

void rtos_idle_callback(void) {
  // Don't call any other FreeRTOS blocking API()
  // Perform background task(s) here
}


