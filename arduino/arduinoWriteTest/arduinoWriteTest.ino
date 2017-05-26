void setup() {
  Serial.begin(9600);
}

void loop() {
    if(Serial.available() >= 4) {
      //get the length of the incoming message because why not
      //TODO need to convert java integer to arduino long value correctly
      long length = ((Serial.read() & 255) << 24) | ((Serial.read() & 255) << 16) | ((Serial.read() & 255) << 8) | ((Serial.read() & 255) << 0);
      //send an acknowledgment response for testing
      char response[] = "received data\n";
      long responseSize = sizeof(response);
      Serial.write(byte(responseSize >> 24));
      Serial.write(byte(responseSize >> 16));
      Serial.write(byte(responseSize >> 8));
      Serial.write(byte(responseSize >> 0));
      Serial.write(response);
      //ignore message contents
      for(int i = 0; i < length; i++) {
        Serial.read();
      }
    }
}

