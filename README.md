# MindMate AI — Java Spring Boot Edition

A student well-being tracker and empathetic companion migrated to **Java Spring Boot**, **Thymeleaf (MVC)**, **SQLite Database**, and **Google Gemini AI**.

---

## 🚀 Tech Stack

- **Backend Framework**: Java 21+ Spring Boot 3.3.x (Spring MVC)
- **Templating Engine**: Thymeleaf (HTML5 / Server-Side Rendering)
- **Database**: SQLite 3 (`mindmate.db`) via Hibernate Community Dialect & Xerial JDBC
- **AI Engine**: Google Gemini API (`gemini-1.5-flash` / `gemini-2.0-flash`)
- **Security / Auth**: Flexible session-based user authentication with BCrypt hashing (with full guest mode support)
- **Styling**: Modern responsive CSS with warm earthy aesthetic, glassmorphism, and responsive SVG charts

---

## 🌟 Key Features

1. **Daily Check-in (MVC Flow)**:
   - Rate Stress, Sleep, Study Workload, Energy, and Connection on intuitive sliders.
   - Optional notes field for deadlines, exams, or reflections.
   - Real-time Gemini AI reflection generating a gentle validation summary, support signal, and practical micro-steps.
   - All check-ins are saved permanently to `mindmate.db` in the `check_ins` table.

2. **Full Reflection View & Export**:
   - Dedicated reflection page (`/reflection/{id}`) showing metric chips and actionable advice.
   - One-click JSON export (`/reflection/{id}/download`).

3. **SQLite Dashboard & AI Pattern Analysis**:
   - Live dashboard (`/dashboard`) displaying total check-in count, average stress, and average sleep.
   - Dynamic SVG stress trend chart calculated directly from SQLite records.
   - **Gemini AI Pattern Analysis**: Gemini reviews historical trends to spot correlations (e.g. sleep dips vs study stress) and provides encouraging insights.
   - Tabular history log with individual deletion and inspection options.

4. **MindMate AI Companion**:
   - Conversational AI chat (`/companion`) tailored specifically for students.
   - Preserves conversation history in SQLite (`chat_messages` table).
   - Instant quick starter prompts (*"I'm feeling stressed about college"*, *"I can't focus today"*, *"I feel lonely"*).
   - **Student Safety & Crisis Detection**: Flags distress or self-harm keywords immediately and outputs emergency helplines (Tele-MANAS, KIRAN, FindAHelpline.com).

5. **Dynamic Gemini Configuration**:
   - Manage and update your API key anytime via the `/settings` page or environment variable `GEMINI_API_KEY`.
   - Automatic graceful fallback: If no API key is provided, the system seamlessly runs on local empathetic heuristics without breaking!

---

## 🛠️ How to Run

### Option 1: Using the included Maven wrapper (Recommended)
Open a terminal in this project root:
```bash
.\mvnw.cmd spring-boot:run
```

### Option 2: Using standard Maven (if installed)
```bash
mvn spring-boot:run
```

Once started, open your browser and navigate to:
👉 **[http://localhost:8080](http://localhost:8080)**

---

## 🔑 Setting your Google Gemini API Key

You can configure your Gemini API key in **any** of the following ways:

1. **Through the Web UI**:
   - Open [http://localhost:8080/settings](http://localhost:8080/settings)
   - Paste your key in the **Gemini API Key** field and click **Save AI Configuration**.

2. **Via Environment Variable**:
   ```powershell
   $env:GEMINI_API_KEY="your-api-key-here"
   .\mvnw.cmd spring-boot:run
   ```

3. **Via `application.properties`**:
   Edit `src/main/resources/application.properties`:
   ```properties
   gemini.api.key=your-api-key-here
   ```

---

## 📂 Project Architecture

```
MindMate_AI_No_Python/
├── pom.xml                               # Maven project configuration
├── mvnw.cmd                              # Maven wrapper execution script
├── mindmate.db                           # Local SQLite database
├── src/main/java/com/mindmate/
│   ├── MindMateApplication.java          # Spring Boot main entrypoint
│   ├── config/
│   │   └── GeminiConfig.java             # Dynamic Gemini key & model config
│   ├── entity/
│   │   ├── CheckIn.java                  # SQLite entity for check-ins
│   │   ├── ChatMessage.java              # SQLite entity for companion chat
│   │   └── User.java                     # SQLite entity for user accounts
│   ├── repository/
│   │   ├── CheckInRepository.java        # Spring Data JPA SQLite repo
│   │   ├── ChatMessageRepository.java    # Chat message repository
│   │   └── UserRepository.java           # User auth repository
│   ├── dto/
│   │   ├── CheckInForm.java              # Form backing bean for check-in
│   │   ├── DashboardStats.java           # Dashboard metrics & chart points
│   │   ├── ChatRequest.java / ChatResponse.java # Companion DTOs
│   │   └── UserRegisterForm.java / UserLoginForm.java
│   ├── service/
│   │   ├── GeminiService.java            # Google Gemini REST API integration & safety
│   │   ├── CheckInService.java           # Business logic & SQLite persistence
│   │   ├── CompanionService.java         # AI chat conversation manager
│   │   └── UserService.java              # Session auth & BCrypt hashing
│   └── controller/
│       ├── HomeController.java           # MVC Home and landing routes
│       ├── CheckInController.java        # Form POST, reflection view, JSON export
│       ├── DashboardController.java     # SQLite metrics, SVG chart, AI insights
│       ├── CompanionController.java     # Interactive chat MVC & AJAX endpoints
│       ├── AuthController.java          # Login, Register, Logout
│       └── SettingsController.java      # Dynamic Gemini & DB settings
└── src/main/resources/
    ├── application.properties            # SQLite, Thymeleaf, and Gemini properties
    ├── static/
    │   ├── css/style.css                 # Polished responsive design system
    │   └── js/main.js                    # Slider binding & real-time chat AJAX
    └── templates/
        ├── fragments/layout.html         # Common header, alerts, footer
        ├── index.html                    # Main landing page & daily pause form
        ├── reflection.html               # Full reflection view & JSON download
        ├── dashboard.html                # SQLite history table, SVG chart, trends
        ├── companion.html                # Full-screen conversational companion
        ├── support.html                  # Mental health resources & helpline directory
        ├── settings.html                 # Dynamic Gemini key configuration
        ├── login.html                    # User sign in
        └── register.html                 # User account creation
```
