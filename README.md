# VDG Ticket Generator

## Wichtig

Nicht für Produktionsumgebungen geeignet! Nur für lokale Tests.

## How to

1. Docker Image erstellen
2. Keystore-Datei erstellen
3. Docker Container starten (Hostport: 8080)
   <br/>3.1 Das Programm läuft unter [http://localhost:8080/](http://localhost:8080/)
4. Keystore-Datei auswählen, Passwörter und Alias eingeben, und anschließend die digitale Signatur auswählen
   <br><img src="docs\imgs\easylogin_keystore.png" alt="Keystore Tab" width="400"/>
5. Ticket ausfüllen
   <br><img src="docs\imgs\easylogin_ticket.png" alt="Keystore Tab" width="400"/>
6. Public Key des Controllers eingeben
   <br><img src="docs\imgs\easylogin_public_key.png" alt="Keystore Tab" width="400"/>
7. Das verschlüsselte Ticket wird in 'Result' angezeigt
   <br><img src="docs\imgs\easylogin_result.png" alt="Keystore Tab" width="400"/>
   <br>7.1 Mit dem Button 'Refresh' wird das Ticket erneut vom Server generiert. Dies ist besonders hilfreich, wenn ein erstelltes Ticket bereits abgelaufen ist.
   <br>7.2 Das Ticket kann durch Eingabe einer URL und anschließendes Klicken auf 'Send' versendet werden. Wichtig: Die URL muss mit „https://“ beginnen.

Hinweis:

- "Accepted" bedeutet, dass die Eingabe vom Server erfolgreich akzeptiert wurde
- "Rejected" bedeutet, dass die Eingabe vom Server abgelehnt wurde
- Keystore und Public Key werden vom Server validiert
- Daten des Tickets werden vom Server **nicht** validiert

## Docker

### Docker Image erstellen

```sh
docker build -t <IMAGE_NAME> .
```

### Docker Container starten

```sh
docker run --name <CONTAINER_NAME> -p 8080:8080 -d <IMAGE_NAME>
```

Alternativ: Start über Docker/Rancher Desktop.<br/>
Hostport: 8080

## Keystore

Die Keystore-Datei, Keystore Alias und die Passwörter müssen beim Controller hinterlegt sein!

### Command

```sh
keytool -genkeypair -keystore KEYSTORENAME -storetype JKS -alias KEYSTOREALIAS -keyalg RSA -keysize 2048 -sigalg SHA256withRSA -validity 365
```

- genkeypair → Erstellt Schlüsselpaar (privater & öffentlicher Schlüssel) <br/>
- keystore → Name der Keystore-Datei <br/>
- storetype → Dateiformat (**JKS**) </br>
- alias → Name des Schlüsselpaars <br/>
- keyalg → Verschlüsselungsalgorithmus (nur **RSA**) <br/>
- keysize → Schlüsselgröße (mindestens **2048**) <br/>
- sigalg → Signatur Algorithmus (**SHA256withRSA**) <br/>
- validity → Gültigkeitszeitraum des Zertifikats in Tagen (Bspw. **365**) <br/>

### Passwort

Passwort der Keystore-Datei festelegen

```sh
Keystore-Kennwort eingeben:
Neues Kennwort erneut eingeben:
```

### Zertifikat

Anschließend muss das Zertifikat ausgefüllt werden. Die angegebenen Daten haben keinen Einfluss auf das Endergebnis.<br/>

```sh
Geben Sie den Distinguished Name ein. Geben Sie einen einzelnen Punkt (.) an, um eine Unterkomponente leer zu lassen, oder drücken Sie die Eingabetaste, um den Standardwert in Klammern zu verwenden.
- Wie lautet Ihr Vor- und Nachname?
  [Unknown]:  [OPTIONAL]
- Wie lautet der Name Ihrer organisatorischen Einheit?
  [Unknown]:  [OPTIONAL]
- Wie lautet der Name Ihrer Organisation?
  [Unknown]:  [OPTIONAL]
- Wie lautet der Name Ihrer Stadt oder Gemeinde?
  [Unknown]:  [OPTIONAL]
- Wie lautet der Name Ihres Bundeslands?
  [Unknown]:  [OPTIONAL]
- Wie lautet der Ländercode (zwei Buchstaben) für diese Einheit?
  [Unknown]:  [OPTIONAL]
 -Ist CN=XXX, OU=XXX, O=XXX, L=XXX, ST=XXX, C=XXX richtig?
  [Nein]: - Mit 'ja' bestätigen -
```

### Passwort vom Schlüsselpaar (Alias)

Passwort für das Schlüsselpaar (Alias) festlegen. Leer lassen, falls das Passwort für die Keystore-Datei und das Passwort für den Keystore-Alias identisch sein sollen.

```sh
Schlüsselkennwort für <ALIAS> eingeben
(RETURN, wenn identisch mit Keystore-Kennwort):
```

## Development

### Frontend

```sh
cd vdg-ticket-frontend
npm install
npm run dev
```

### Backend

```sh
cd vdg-ticket-backend
mvn quarkus:dev
```

### Test

```sh
cd vdg-ticket-backend
mvn quarkus:test
```

## Aubau eines Tickets

```sh
<?xml version="1.0" encoding="UTF-8" ?>
<Ticket>
	<TicketInfo>
		<TicketId>USER_INPUT</TicketId>
		<TargetId>USER_INPUT</TargetId>
		<TargetUserId>USER_INPUT</TargetUserId>
		<IssuerUserId>USER_INPUT</IssuerUserId>
		<UserIPAddress>USER_INPUT</UserIPAddress>
		<AuthLevel>USER_INPUT</AuthLevel>
		<AuthMethod>USER_INPUT</AuthMethod>
		<AuthTimestamp>SET_BY_SYSTEM</AuthTimestamp>
		<IssuerId>USER_INPUT</IssuerId>
		<IssueTimestamp>SET_BY_SYSTEM</IssueTimestamp>
	</TicketInfo>
	<Signature>
		<SignatureValue>HASHWERT_TICKETINFO</SignatureValue>
		<SignatureAlgorithm>SIGNATURE_ALGORITHM</SignatureAlgorithm>
		<KeyInfo>
			<KeyId>KEYSTORE_ALIAS</KeyId>
		</KeyInfo>
	</Signature>
</Ticket>
```
