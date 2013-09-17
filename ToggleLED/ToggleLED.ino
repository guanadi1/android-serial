#include <SoftwareSerial.h>

#define RxD 10
#define TxD 11
#define RST 5
#define KEY 4

#define LED 13

SoftwareSerial BTSerial(RxD, TxD);
byte pinEstado = 0;

void setup()
{
  
  pinMode(LED, OUTPUT);
  pinMode(RST, OUTPUT);
  pinMode(KEY, OUTPUT);
  
  digitalWrite(LED, LOW);
  digitalWrite(RST, LOW);
  digitalWrite(KEY, LOW); // Modo Comunicacion
  digitalWrite(RST, HIGH);
   
  // Configuracion del puerto serie por software
  // para comunicar con el modulo HC-05
  BTSerial.begin(9600);
  BTSerial.flush();
  delay(500);
  
  // Configuramos el puerto serie de Arduino para Debug
  Serial.begin(9600);
  Serial.println("Ready");

}

void loop()
{

  // Esperamos ha recibir datos.
  if (BTSerial.available()){
    
    // La funcion read() devuelve un caracter 
    char command = BTSerial.read();
    BTSerial.flush();
    Serial.println(command);
    
    // En caso de que el caracter recibido sea "L" cambiamos
    // El estado del LED
    if (command == 'L'){
      Serial.println("Toggle LED");
      toggle(LED); 
    }
    
  }
}


void toggle(int pinNum) {
  // Establece el pin del LED usando la variable pinEstado:
  digitalWrite(pinNum, pinEstado); 
  // si el pinEstado = 0, lo establece a 1, y vice versa:
  pinEstado = !pinEstado;
}
