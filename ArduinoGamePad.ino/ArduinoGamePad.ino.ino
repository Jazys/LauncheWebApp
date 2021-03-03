#include <SoftwareSerial.h>

SoftwareSerial BT(D4, D2); //RX - TX
//***********************
// Fonctionne aussi pour le clavier KEYES AD Key
//***********************
#define TEST_ON //-- Comment ou Uncomment pour afficher valeurs
#define BRepos 1022 //-- Valeur lorqu'aucun bouton n'est appuyé
//-- Valeurs relevées lorsque TEST_ON
#define B1 11
#define B2Bas 100 //-- 90 et 91 pour R= 220k
#define B2Haut 180
#define B3Bas 300 //-- 179 et 180 pour R= 470k
#define B3Haut 350
#define B4Bas 500 //-- 317 et 318 pour R= 1kOhms
#define B4Haut 580
#define B5Bas 730 //-- 1001 et 1002 pour R= 100kOhms
#define B5Haut 800
//*********************
int sensorValue;
int oldkey=0, key=0;
char c=' ';
int i=0;
int sommeKey=0;
//*********************
void setup() {
 Serial.begin(9600); // por afficher dans la console
 pinMode(13, OUTPUT); // pour résultat sans console 
  BT.begin(9600);
 Serial.println("Bouton Prog ON");
   BT.write(1) ; 
}

void loop() {
 sensorValue = analogRead(A0);
 if (sensorValue<BRepos)
 {
 //key=buttonPushed(sensorValue,false);
 sommeKey=sommeKey+sensorValue;
 i=i+1;

   if(i==3)
   {
    //Serial.println(sommeKey);
    sommeKey=sommeKey / 3;
    //Serial.println(sommeKey);
    key=buttonPushed(sommeKey,true);
  
    i=0;
    sommeKey=0;
   }
 }
 else
 {
  if(oldkey!=0)
  {
    BT.write("key_off\r\n") ;
    oldkey=0;    
    Serial.println("Bouton off"); 
  }

  if(oldkey==0)
    sommeKey=0;
 //Serial.println("Bouton OFF");
 digitalWrite (13, LOW);} 
}

int buttonPushed(int val,bool treatData) {
 delay(15); //--short tempo
 if( val <= B1 ) 
 { 
   if (oldkey!=key)
   {
     if(treatData)
     {
       Serial.println("Bouton 1 ON"); 
       BT.write("left_down\r\n") ; 
     }
     oldkey=1;
     digitalWrite (13, HIGH);
    }
    return 1;
 }
 else if ( val >= B2Bas and val <= B2Haut) 
 { 
   if (oldkey!=key)
   {
    if(treatData)
     {
       Serial.println("Bouton 2 ON"); 
       BT.write("key_up\r\n") ;; 
       oldkey=2;
       digitalWrite (13, HIGH);
     }
   }
   return 2;
 }
 else if ( val >= B3Bas and val <= B3Haut) 
 { 
   if (oldkey!=key)
   {
    if(treatData)
    {
      Serial.println("Bouton 3 ON"); 
      BT.write("key_down\r\n") ;
    }
    oldkey=3;
    digitalWrite (13, HIGH);
   }
   return 3;
 }
 else if ( val >= B4Bas and val <= B4Haut ) 
 { 
   if (oldkey!=key)
   {
    if(treatData)
    {
      Serial.println("Bouton 4 ON");
      BT.write("right_down\r\n") ; 
    }
    oldkey=4;
    digitalWrite (13, HIGH);
   }
   return 4;
 }
 else if ( val >= B5Bas and val <= B5Haut) 
 { 
   if (oldkey!=key)
   {
    if(treatData)
    {
      Serial.println("Bouton 5 ON"); 
      BT.write("key_a\r\n") ; 
    }
    oldkey=5;
    digitalWrite (13, HIGH);
    }
   return 5;
 }
 else 
 { //--Pour toute autre valeur de résistance
   #ifdef TEST_ON
   Serial.println(val);
   #endif
   return 0;
 }
} 
