Setup Readme zur Installation der App.
 
 
Das Projekt verwendet folgende externe Libraries:
MPAndroidChart zur Darstellung der Charts (https://github.com/PhilJay/MPAndroidChart)
JodaTime Api zum Konvertieren von Datumsformaten https://github.com/dlew/joda-time-android
Google Play Services 

Installationsanleitung:
    
1. Installation des Android SDKs als Entwicklungsumgebung.
Für die Entwicklung haben wir das API Level 17 vorgesehen, dies kann über den SDK-Manager installiert werden.

2. Importieren des Projekts 
Der Quellcode steht als vollständiges Eclipse-Projekt zur Verfügung, welches mit Eclipse importiert werden kann.  

Hierbei ist darauf zu achten, dass die Google Play Services vorhanden sind, diese werden aber als referenziertes 
Projekt direkt mit importiert. Falls dies nicht erfolgt ist kann dies mittels dieser Anleitung nachgeholt werden. 
(http://developer.android.com/google/play-services/setup.html)

Für die Darstellung der in der Vergangenheit befahrenen Fahrten benötigen wir die Googel API Keys, diese sind 
notwendig da die Map Funktionen ansonsten nicht verwendet werden können.
Sie können auf diesem Weg installiert werden: Die mitgeschickte debug.keystore Datei muss in das Benutzerverzeichnis 
C:\Users\<Username>\.android (unter Windows) gelegt werden.

3. Ausführung
Für die Ausführung empfehlen wir entweder ein Android Smartphone mit der Version 4.2.2 oder höher. Für die Verwendung 
des Geräts müssen die OEM Treiber installiert werden, diese können abhängig vom Gerät hier gefunden werden: 
http://developer.android.com/tools/extras/oem-usb.html#Drivers. 

Es ist alternativ möglich einen Emulator wie z.B. GenyMotion (https://www.genymotion.com) zu verwenden.

