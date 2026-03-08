# MEDTRACK

```text
███╗   ███╗███████╗██████╗ ████████╗██████╗  █████╗  ██████╗██╗  ██╗
████╗ ████║██╔════╝██╔══██╗╚══██╔══╝██╔══██╗██╔══██╗██╔════╝██║ ██╔╝
██╔████╔██║█████╗  ██║  ██║   ██║   ██████╔╝███████║██║     █████╔╝ 
██║╚██╔╝██║██╔══╝  ██║  ██║   ██║   ██╔══██╗██╔══██║██║     ██╔═██╗ 
██║ ╚═╝ ██║███████╗██████╔╝   ██║   ██║  ██║██║  ██║╚██████╗██║  ██╗
╚═╝     ╚═╝╚══════╝╚═════╝    ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝╚═╝  ╚═╝
```

## PROJECT LINKS

Live Application  
https://medtrackcare.vercel.app

Frontend Repository  
https://github.com/dukee27/medtrack-frontend

Backend Repository  
https://github.com/dukee27/medtrack-backend

---

A full-stack medical adherence infrastructure designed to bridge the gap between **clinical prescriptions and real-world patient routines**.

MedTrack converts complex prescriptions into structured daily schedules, enabling **patients, caregivers, and medical professionals** to monitor treatment adherence in a unified system.

---

# SYSTEM PURPOSE

One of the largest breakdowns in healthcare occurs **after patient discharge**.  
Prescriptions are often complex, schedules overlap, and elderly patients may live far from caregivers.

MedTrack addresses this problem through a **dual-domain architecture**:

• **Clinical Domain** – Medical oversight and population monitoring  
• **Home Domain** – Patient routines and caregiver collaboration

The system transforms medical instructions into a **structured, verifiable medication workflow**.

---

# CORE ARCHITECTURE

MedTrack is built around two operational environments that communicate through a secured backend.

---

## 1. Clinical Domain

Designed for **hospitals, clinics, and medical professionals**.

Capabilities include:

- Managing groups of patients within an organization  
- Monitoring long-term medication adherence  
- Viewing intake history and missed dosage events  
- Tracking potential adverse reactions  
- Identifying declining adherence trends early  

This layer acts as the **medical oversight interface**.

---

## 2. Home Domain

Designed for **patients and their immediate caregivers**.

Capabilities include:

- Clear daily medication schedule  
- Dosage instructions and timing alerts  
- Medication inventory tracking  
- Logging completed or missed doses  
- Caregiver access for remote monitoring  

A family member can securely log in and verify medication adherence for an elderly relative.

---

# KEY FEATURES

### Medication Schedule Engine

Transforms prescriptions into structured daily schedules with dosage instructions and timing.

### Dynamic Intake Logging

Tracks:

- Completed doses  
- Missed medication windows  
- Inventory consumption  

### Caregiver Delegation System

Patients can securely grant **Read / Write / Admin access** to trusted caregivers.

This enables remote monitoring without exposing full system privileges.

### Patient Switcher

Authorized users can switch between multiple patient profiles they manage.

### Responsive Interface

Fully responsive UI supporting **light and dark mode** for accessibility.

---

# SECURITY MODEL

Healthcare data requires strict protection.  
MedTrack implements a **multi-layer security architecture**.

### Stateless Authentication

Authentication handled using **JSON Web Tokens (JWT)**.

### Role-Based Access Control

Every API endpoint verifies:

- Resource ownership  
or  
- Active `AccessControl` permission.

### Cryptographic Password Storage

User credentials are secured using **Bcrypt hashing with salting**.

### Spring Security Filter Chain

Custom filters validate tokens before requests reach application logic.

### CORS Restrictions

API endpoints only accept requests from authorized client domains.

---

# TECH STACK

## Frontend

Framework  
React 18 + Vite

Styling  
Tailwind CSS

State Management  
React Context API

Deployment  
Vercel

---

## Backend

Language  
Java 21

Framework  
Spring Boot 3

Data Layer  
Spring Data JPA + Hibernate

Security  
Spring Security + JWT

Deployment  
Render / Railway

---

## Database

Engine  
PostgreSQL

Deployment  
Managed cloud database instance

---

# SYSTEM FLOW

```
Hospital / Doctor
       │
       │ creates prescription
       ▼
MedTrack Backend (Spring Boot API)
       │
       │ transforms into structured schedule
       ▼
Patient Dashboard (React App)
       │
       │ logs intake / updates status
       ▼
Adherence Telemetry
       │
       ▼
Doctor / Caregiver Monitoring
```

---

# LIVE DEPLOYMENT

Frontend  
Hosted on Vercel

Backend  
Hosted on Render (free-tier cold start may cause initial latency)

Database  
Cloud PostgreSQL instance

---

# LOCAL DEVELOPMENT

## Backend Setup

Requirements

- Java 21  
- PostgreSQL  

```
cd medtrack-backend
```

Create a local database:

```
medtracker
```

Update:

```
src/main/resources/application-dev.properties
```

Then run:

```
./mvnw spring-boot:run
```

The development profile will automatically create the database schema.

---

## Frontend Setup

Requirements

- Node.js  
- npm  

```
cd medtrack-frontend
npm install
npm run dev
```

The development environment automatically connects to:

```
localhost:8080
```

---

# PROJECT STRUCTURE

```
medtrack

├── medtrack-backend
│   ├── controllers
│   ├── services
│   ├── repositories
│   ├── security
│   └── models
│
├── medtrack-frontend
│   ├── components
│   ├── context
│   ├── pages
│   └── services
│
└── database
```

---

# FUTURE DEVELOPMENT

MedTrack is designed as a scalable healthcare platform. Planned expansions include:

### Predictive Adherence Analytics

Machine learning models to predict medication non-adherence.

### Smart Device Integration

Logging medication events via wearable devices.

### Pharmacy Integration

Automatic refill requests triggered by low inventory.

### Push Notification System

Mobile alerts for medication reminders.

### Clinical Dashboard Expansion

Advanced reporting and patient risk analysis.

---

# WHY MEDTRACK

MedTrack is not just a reminder application.

It is a **medical adherence infrastructure** designed to:

- reduce missed medication  
- support remote caregiving  
- provide doctors visibility into treatment compliance  
- transform prescriptions into actionable daily routines  

The goal is simple:

**Ensure that medical treatment continues correctly outside the hospital.**

---

# VERSION

MedTrack  
System Version 1.0.0
