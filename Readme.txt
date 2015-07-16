Setup Readme zur Installation der App.
 
 
Das Projekt verwendet folgende externe Libraries:
MPAndroidChart zur Darstellung der Charts (https://github.com/PhilJay/MPAndroidChart)
JodaTime Api zum Konvertieren von Datumsformaten https://github.com/dlew/joda-time-android
Google Play Services 

Installationsanleitung:
    
1. Installation des Android SDKs als Entwicklungsumgebung.
F�r die Entwicklung haben wir das API Level 17 vorgesehen, dies kann �ber den SDK-Manager installiert werden.

2. Importieren des Projekts 
Der Quellcode steht als vollst�ndiges Eclipse-Projekt zur Verf�gung, welches mit Eclipse importiert werden kann.  

Hierbei ist darauf zu achten, dass die Google Play Services vorhanden sind, diese werden aber als referenziertes 
Projekt direkt mit importiert. Falls dies nicht erfolgt ist kann dies mittels dieser Anleitung nachgeholt werden. 
(http://developer.android.com/google/play-services/setup.html)

F�r die Darstellung der in der Vergangenheit befahrenen Fahrten ben�tigen wir die Googel API Keys, diese sind 
notwendig da die Map Funktionen ansonsten nicht verwendet werden k�nnen.
Sie k�nnen auf diesem Weg installiert werden: Die mitgeschickte debug.keystore Datei muss in das Benutzerverzeichnis 
C:\Users\<Username>\.android (unter Windows) gelegt werden.

3. Ausf�hrung
F�r die Ausf�hrung empfehlen wir entweder ein Android Smartphone mit der Version 4.2.2 oder h�her. F�r die Verwendung 
des Ger�ts m�ssen die OEM Treiber installiert werden, diese k�nnen abh�ngig vom Ger�t hier gefunden werden: 
http://developer.android.com/tools/extras/oem-usb.html#Drivers. 

Es ist alternativ m�glich einen Emulator wie z.B. GenyMotion (https://www.genymotion.com) zu verwenden.

