# SubsTracked

Subscription-Tracker Webapplikation — entwickelt im Rahmen des WEBEC-Moduls FS2026 bei Silvan Zurbrügg, FHNW.

## Über das Projekt

SubsTracked hilft dir den Überblick über deine monatlichen Abonnements zu behalten — von Netflix bis Gym-Abo. Erfasse Subscriptions, markiere sie als bezahlt und importiere wiederkehrende Zahlungen direkt aus deinem E-Banking.

---

## Funktionen

### Dashboard
Übersicht über monatliche und jährliche Kosten, aktive Subscriptions und bevorstehende Renewals. Ein Banner warnt dich wenn eine Zahlung in den nächsten 7 Tagen fällig ist.

### Subscription-Verwaltung
Erstelle, bearbeite und lösche Abonnements. Erfasse Name, Betrag, Währung, Kategorie und Erneuerungsdatum. Markiere eine Subscription als bezahlt — das Renewal-Datum wird automatisch vorgerückt.

### CSV-Import
Lade einen CSV-Export deiner UBS E-Banking-Transaktionen hoch. SubsTracked erkennt automatisch wiederkehrende Zahlungen und schlägt sie als Subscriptions vor. Du kannst jeden Vorschlag bestätigen oder ablehnen.

### Profil & Einstellungen
Bearbeite deinen Namen und deine E-Mail-Adresse. Wähle zwischen Light und Dark Mode. Das Theme wird im Browser gespeichert.

### Zusatzthema: Auth & Authorization
Registrierung und Login mit Session-basierter Authentifizierung für den Web-Layer. Zusätzlich eine vollständige JWT-Implementierung mit Access- und Refresh-Token für die REST API — geeignet für externe Clients wie mobile Apps. Passwörter werden mit BCrypt gehasht. Jede Ressource ist durch Ownership-Checks geschützt.

---

## User Journey

1. **Account erstellen** — Registriere dich mit Name, E-Mail und Passwort und melde dich .
2. **Subscriptions erfassen** — Füge deine Abos manuell hinzu oder importiere sie per CSV aus dem E-Banking.
3. **Zahlungen verfolgen** — Markiere Subscriptions als bezahlt. Das Dashboard zeigt dir bevorstehende Renewals und deine Gesamtkosten.
4. **Überblick behalten** — SubsTracked erinnert dich täglich an fällige Zahlungen und zeigt dir genau was du monatlich ausgibst.

---

## Technologie-Stack

| Bereich | Technologie |
|---|---|
| Sprache | Java 25 |
| Framework | Spring Boot 4.0.6 |
| Datenbank | PostgreSQL 17 |
| Migrations | Flyway |
| Templating | Pebble (Server-Side Rendering) |
| Security | Spring Security, JWT (jjwt 0.12.6), BCrypt |
| Frontend | HTML, CSS, Vanilla JS |
| API-Docs | Springdoc / Swagger UI |

---

## Architektur

Die Applikation folgt einer klassischen mehrschichtigen Architektur:

```
Browser
  ↓ HTTP
Controllers (Web + REST)
  ↓
Services (Business Logic)
  ↓
Repositories (Spring Data JPA)
  ↓
PostgreSQL
```

### URL-Struktur

| Pfad                    | Typ | Beschreibung                   |
|-------------------------|---|--------------------------------|
| `/auth/**`              | Web | Login, Register, Logout        |
| `/dashboard`            | Web | Übersicht                      |
| `/subscriptions/**`     | Web | CRUD Subscriptions             |
| `/import`               | Web | CSV-Import                     |
| `/profile`              | Web | Benutzereinstellungen          |
| `/info`                 | Web | Info-Seite                     |
| `/api/auth/**`          | REST | JWT Login / Register / Refresh |
| `/api/subscriptions/**` | REST | Subscription API               |
| `/api/users/**`         | REST | User API                       |
| `/api/import/**`        | REST | import API                     |
| `/api/catalogue/**`     | REST | Catalogue API                  |



### Rendering-Strategie

Web-Seiten werden Server-Side mit Pebble gerendert. Die REST API gibt JSON zurück und ist für externe Clients vorgesehen. Beide Strategien koexistieren — Web-Controller nutzen Session-Auth, REST-Controller nutzen JWT.

---

## Setup

### Voraussetzungen

- Java 21+
- Maven
- PostgreSQL 17 (Port 5433)
- Docker (optional)

### Datenbank starten

```bash
docker-compose up -d
```

### Umgebungsvariablen

Erstelle eine `.env` Datei im Projektroot:

```
DB_URL=jdbc:postgresql://localhost:5433/subtracked-db
DB_USERNAME=dein_user
DB_PASSWORD=dein_passwort
JWT_SECRET=dein_base64_secret
JWT_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=86400000
```

JWT Secret generieren:

```bash
openssl rand -base64 48 | tr '+/' '-_' | tr -d '=\n'
```

### Applikation starten

```bash
./mvnw spring-boot:run
```

Die App ist erreichbar unter `http://localhost:8080`.  
Flyway führt alle Migrations automatisch aus beim ersten Start.

### API-Dokumentation

Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## Tests

```bash
./mvnw test
```

Die Tests verwenden eine H2 In-Memory Datenbank — keine laufende PostgreSQL-Instanz nötig.

| Typ  | Klassen |
|---|---|
| Unit | `JwtServiceTest`, `SubscriptionServiceTest`, `RecurrenceDetectorTest` |
| E2E | `AuthWebControllerTest`, `SubscriptionWebControllerTest` |


---

## Projektstruktur

```
src/main/java/ch/yannick/subtracked/
├── app/
│   ├── auth/           # JWT, Security, AuthService
│   ├── config/         # SecurityConfig, OpenApiConfig
│   ├── exception/      # GlobalExceptionHandler
│   ├── importing/      # CsvParser, RecurrenceDetector, CsvImportService
│   ├── subscription/   # SubscriptionService, Converter, DTOs
│   └── user/           # UserService, DTOs
├── controller/         # Web- und REST-Controller
└── domain/
    ├── category/
    ├── catalogue/
    ├── importing/
    ├── subscription/
    └── user/

src/main/resources/
├── templates/          # Pebble Templates
│   └── macros/
├── static/             # CSS, JS, Logo
└── db/migration/      
```

---

## Autor

Yannick Kurz — FHNW WEBEC FS2026