#include <bluefruit.h>

#define DRAW_BUTTON_PORT 7
#define OPTION_BUTTON_PORT 11

/* HRM Service Definitions
 * Heart Rate Monitor Service:  0x180D
 * Heart Rate Measurement Char: 0x2A37
 */
BLEUuid UUID16_PEN_SERVICE = BLEUuid(0xface);
BLEUuid UUID16_DRAWING_CHARACTERISTIC = BLEUuid(0xdada);
 
BLEService        pds = BLEService(UUID16_PEN_SERVICE);
BLECharacteristic pdc = BLECharacteristic(UUID16_DRAWING_CHARACTERISTIC);
//BLECharacteristic poc = BLECharacteristic(UUID16_OPTION_CHARACTERISTIC);

BLEDis bledis;    // DIS (Device Information Service) helper class instance
BLEBas blebas;    // BAS (Battery Service) helper class instance

// Advanced function prototypes
void startAdv(void);
void setupService(void);
void connect_callback(uint16_t conn_handle);
void disconnect_callback(uint16_t conn_handle, uint8_t reason);

boolean prevDrawVal;
boolean prevOptionVal;
uint8_t drawData;
    
void setup() { 
  drawData = 0;
  
  pinMode(DRAW_BUTTON_PORT, INPUT);
  pinMode(OPTION_BUTTON_PORT, INPUT);

  prevDrawVal = digitalRead(DRAW_BUTTON_PORT);
  prevOptionVal = digitalRead(OPTION_BUTTON_PORT);
  
  Serial.begin(115200);

  // Initialise the Bluefruit module
  Serial.println("Initialise the Bluefruit nRF52 module");
  Bluefruit.begin();

  // Set the advertised device name (keep it short!)
  Serial.println("Setting Device Name to 'Feather52 HRM'");
  Bluefruit.setName("Bluefruit52 HRM");

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
  
    // On Press
    if (prevDrawVal == false && currDrawVal == true) {
       drawData = currDrawVal;
       pdc.notify8(drawData);
       Serial.println("Button has been pressed");
    }
    else if (prevDrawVal == true && currDrawVal == false) {
       drawData = currDrawVal;
       pdc.notify8(drawData);
      Serial.println("Button has been released");
    }
    else if (prevDrawVal == true && currDrawVal == true) {
      // Button is being held down
    }
    prevDrawVal = currDrawVal;
  }
  
}

void rtos_idle_callback(void) {
  // Don't call any other FreeRTOS blocking API()
  // Perform background task(s) here
}

