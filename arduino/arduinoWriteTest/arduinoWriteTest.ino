void setup() {
  Serial.begin(4800);
}

//read an integer from the serial port
long serialReadJavaInteger() {
  return
    ((long(Serial.read()) << 24) |
     ((long(Serial.read()) & 255) << 16) |
     ((long(Serial.read()) & 255) <<  8) |
     ((long(Serial.read()) & 255)));
}

//write an integer to the serial port
void serialWriteJavaInteger(long value) {
  Serial.write(byte((((unsigned long)value) >> 24) & 255));
  Serial.write(byte((((unsigned long)value) >> 16) & 255));
  Serial.write(byte((((unsigned long)value) >>  8) & 255));
  Serial.write(byte((((unsigned long)value) >>  0) & 255));
}

void serialWriteMessageInteger(long value) {
  serialWriteJavaInteger(4); //write message length
  serialWriteJavaInteger(value); //write the value
}

void processReceived(long messageLength) {
  if (messageLength == 4) { //if the length is 4 then it sent one integer and for debugging im assuming its a joystick value
    serialWriteMessageInteger(serialReadJavaInteger());
  }
}

//flags for received message processing
boolean isMessage = false;
long messageLength = 0;

void loop() {
  if (isMessage && Serial.available() >= messageLength) {
    processReceived(messageLength);
    isMessage = false; //message was processed, reset for next
  } else if (!isMessage && Serial.available() >= 4) { //if there wasn't a message length received yet but there are 4 bytes...
    isMessage = true;
    messageLength = serialReadJavaInteger(); //read the length of the received message
  }
}

