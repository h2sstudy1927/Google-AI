# H2S STUDY - Odisha State Competitive Exams Platform (Android)

Native Jetpack Compose Android application for Odisha state government competitive examinations (OPSC, OSSC, OSSSC, OUSSSC, and Teaching Exams), featuring official commission negative marking, digital whiteboard classroom, AI mock mentor, and local Room persistence.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> The following architectural and UX choices have been confirmed based on your preferences:
> - **Architecture**: Full native Android application built with Jetpack Compose, Material 3, and Kotlin Coroutines/Flow.
> - **Data & Storage**: Offline-first Room Database with pre-populated Odisha examination question banks, test attempt history, and custom test creation.
> - **Feature Scope**: Complete implementation of all core modules — 3D Colorful Exam Category Folders, Live Exam Hall (+1.00 / -0.25 marking), Digital Whiteboard Studio with touch canvas, AI Performance Diagnostic Mentor, Teacher Authoring Studio, and Academic Merit Certificate generator.

---

### 1. Overview & Core Concept

**What It Does**:
H2S STUDY is a dedicated mobile test-preparation and classroom app for aspirants appearing for Odisha state examinations (OPSC OCS/ASO, OSSC CGL/SI, OSSSC RI/ARI/Amin/PEO, OUSSSC, and Teaching Exams like OTET, OSSTET, and RHT). Candidates can attempt timed mock tests under strict commission rules (+1.00 for correct, -0.25 penalty for incorrect), review detailed Odia & English explanations, receive AI-driven negative-marking diagnostics, attend live whiteboard lectures with resolution controls, and generate verified merit certificates. Teachers can author custom mock tests and manage student approvals.

**Target Audience**:
- Odisha government job aspirants preparing for civil services, revenue, police, and teaching posts.
- Educators and mentors (founded by lead faculty Hemanand Hans) conducting live conceptual coaching.

**Key Value**:
- Realistic simulation of Odisha exam scoring with negative-mark containment strategies.
- Offline-ready access to curated Odisha GK, Odia Byakarana (ବ୍ୟାକରଣ), and General Studies question sets.
- Built-in interactive touch whiteboard studio and live doubt resolution.

---

### 2. User Experience & Visual Design

#### Key User Flows
1. **Onboarding & Instant Auth Gate**:
   - Quick sign-in / registration with name, mobile number, Odisha district (e.g., Kalahandi, Khordha, Cuttack), and account type (Candidate or Teacher/Developer).
   - Instant persistent session caching so users never have to sign in repeatedly.
2. **Exam Category Dashboard (Home)**:
   - High-contrast, vibrant 3D gradient cards matching the official web platform:
     - **OPSC Exams** (OCS/OAS • ASO • Civil Services) in Deep Indigo & Royal Blue with gold accents.
     - **OSSC Exams** (CGL • SI • Junior Clerk) in Deep Oceanic Cyan & Navy.
     - **OSSSC Exams** (RI • ARI • Amin • PEO • JA) in Emerald Green & Forest Jade.
     - **OUSSSC Exams** (State & University Cadre) in Violet & Deep Purple.
     - **Teaching Exams** (OTET • OSSTET • RHT • JT • OAVS) in Crimson Rose.
     - **Live Classroom & Whiteboard** in Electric Violet & Indigo.
     - **AI Mock Mentor** in Tech Neon Indigo.
     - **WhatsApp Batch Community** in Emerald.
3. **Exam Hall Execution**:
   - Live 15-minute countdown timer and dynamic question progress bar.
   - Question card displaying topic badge (e.g., "Odisha History & Heritage", "Odia Grammar / ଓଡ଼ିଆ ବ୍ୟାକରଣ"), point weights (+1.00 / -0.25), and 4 multiple-choice options.
   - Instant feedback mode with color-coded right/wrong highlights and in-depth conceptual rationale.
   - Option to "Skip" with 0 penalty to train negative-marking discipline.
4. **Scorecard & Certificate Generation**:
   - Summary breakdown: Correct (+1.00 each), Incorrect, Negative Penalty (-0.25 each), and Net Score.
   - One-tap navigation to "Ask AI Mentor" or view the formal **Certificate of Academic Merit** with candidate name, score, issue date, and verification ID (H2S-OD-XXXX).
5. **Interactive Whiteboard Studio**:
   - Touch-enabled canvas with Pen, Highlighter, Eraser, Line, and Box tools.
   - Color picker (White, Yellow, Cyan, Red, Green) and stroke width slider.
   - Resolution toggle (144p to 1080p Full HD), stream pause/resume, and "Raise Hand" alert.
   - Class Q&A chat with toggle between "All Discussion" and "Doubts Only".
6. **AI Mock Mentor**:
   - Performance analytics: Accuracy ratio percentage, marks lost to penalties, and net score.
   - Topic mistake breakdown detailing specific Odisha GK/Grammar pitfalls.
   - Interactive chat coach providing targeted revision advice for upcoming tests.
7. **Teacher Authoring Studio**:
   - Full form to create custom tests with title, category, questions, 4 options, correct answer index, and explanations.

#### Visual Identity & Theme
- **Color Palette**:
  - Primary: Royal Purple (`#3B0764` / `#581C87`) & Deep Indigo (`#1E1B4B`)
  - Accents: Amber Gold (`#F59E0B`), Cyan (`#06B6D4`), Emerald Green (`#10B981`), Rose (`#E11D48`)
  - Backgrounds: Dark slate/navy headers with crisp light slate (`#F8FAFC`) surfaces.
- **Typography**: Bold display headings with clear sans-serif body typography and monospaced timers/scores.
- **Edge-to-Edge**: Full edge-to-edge support with `WindowInsets.safeDrawing` and navigation bar padding.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Full Native Jetpack Compose Architecture**:
  - *Chosen*: Native Compose UI components with smooth springs and staggered animations.
  - *Why*: Instant touch responsiveness, native Canvas drawing for the whiteboard without web latency, and offline speed on budget Android devices.
- **Decision 2: Local Persistence via Room Database**:
  - *Chosen*: Room Database with DAO for preloaded tests, user-created tests, and attempt history.
  - *Why*: Zero cloud latency, completely works without internet connectivity in rural Odisha districts, instant start.
- **Decision 3: Canvas-based Interactive Whiteboard**:
  - *Chosen*: Native Compose `Canvas` with gesture detection (`detectDragGestures`).
  - *Why*: Smooth 60fps drawing, zero lag, support for multiple tool types, colors, and line widths.

---

### 4. Technical Architecture & Data Strategy *(Technical Reference)*

```
┌──────────────────────────────────────────────────────────────┐
│                      H2S STUDY (Compose UI)                  │
├──────────────────────┬───────────────────────┬───────────────┤
│  Navigation & Shell  │   Exam Hall Engine    │  Whiteboard   │
│  - TopAppBar & Bell  │   - 15m Timer Flow    │  - Canvas     │
│  - BottomNavBar      │   - +1.00 / -0.25 Mark│  - Tools/Cols │
│  - Folder Grid       │   - Odia Rationale    │  - Doubts Chat│
├──────────────────────┴───────────────────────┴───────────────┤
│                     H2sViewModel (StateFlow)                 │
│  - userState: CurrentUser? (Active Session)                  │
│  - currentScreen: Screen (Home, Folder, Exam, Live, AI, ...) │
│  - examState: ActiveExamSession                              │
│  - whiteboardState: DrawingPaths, CurrentTool, Color, Size   │
│  - chatState: List<ChatMessage>                              │
│  - aiMentorState: Accuracy, Mistakes, DialogueHistory        │
├──────────────────────────────────────────────────────────────┤
│                   Room Database Layer                        │
│  - AppDatabase (version 1)                                   │
│  - TestEntity & QuestionEntity (Prepopulated + Custom Tests) │
│  - AttemptRecordEntity (Score, Correct, Wrong, Penalty, Date)│
│  - UserDao & AttemptDao & TestDao                            │
└──────────────────────────────────────────────────────────────┘
```

#### Data Entities & Models:
- **`UserSession`**: Name, mobile number, district, role (`student` or `teacher`), approved status.
- **`ExamTest`**: ID, title, category (`OPSC`, `OSSC`, `OSSSC`, `OUSSSC`, `TEACHING`), duration (mins), questions list.
- **`Question`**: ID, topic, question text, 4 options, correctIndex, explanation text.
- **`ExamAttemptResult`**: Test ID, test title, correctCount, wrongCount, penaltyDeducted, netScore, timestamp, accuracyPercent.
- **`DrawingPath`**: Offset points, color, strokeWidth, tool type.
- **`ClassChatMessage`**: ID, sender, role, text, isDoubt, timestamp.

#### Key Interactive Component & State Transitions:
1. `HomeFoldersScreen`: Displays 8 cards; clicking a card routes to `FolderDetailScreen` filtered by commission category.
2. `FolderDetailScreen`: Lists tests for the selected exam category; clicking "Attempt Mock Test" initializes `ExamSessionState` and transitions to `ExamHallScreen`.
3. `ExamHallScreen`: Executes timer countdown; option selection updates UI; submit verifies answer, updates live score (+1.00 or -0.25), and reveals explanation; concluding test transitions to `ScorecardScreen` with certificate access.
4. `LiveWhiteboardScreen`: Listens to touch drags on `Canvas`; adds path segments; supports color/tool switching, handout download, and live doubts chat.
5. `AiMentorScreen`: Dynamically computes weakness areas from `ExamAttemptResult` and provides conversational study guidance.
6. `CreateMockScreen`: Allows teachers to input questions and publishes directly to the Room database.
