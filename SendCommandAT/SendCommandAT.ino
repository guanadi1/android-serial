#include <SoftwareSerial.h>

#define RxD 10
#define TxD 11
#define RST 5
#define KEY 4

SoftwareSerial BTSerial(RxD, TxD);

void setup()
{
  
  pinMode(RST, OUTPUT);
  pinMode(KEY, OUTPUT);
  digitalWrite(RST, LOW);
  digitalWrite(KEY, HIGH);
  digitalWrite(RST, HIGH);
  
  delay(500);
  
  BTSerial.flush();
  delay(500);
  BTSerial.begin(38400);
  Serial.begin(9600);
  Serial.println("Enter AT commands:");

  BTSerial.print("AT\r\n");
  delay(100);

}

void loop()
{

  if (BTSerial.available())
    Serial.write(BTSerial.read());

  if (Serial.available())
    BTSerial.write(Serial.read());

}
