# SMIS — Student Management Information System
**Stack:** Java 21 · JavaFX 21 · SQLite (no install needed) · Maven

---

## Opening in NetBeans

1. Open **Apache NetBeans 25**
2. **File → Open Project**
3. Navigate to and select this **SMIS** folder
4. NetBeans will auto-detect it as a Maven project ✅
5. Wait for it to download dependencies (needs internet, first time only)

---

## Running from NetBeans

### Option A — Right-click Run (Recommended)
1. In the Projects panel, expand **SMIS**
2. Expand **Source Packages → com.smis**
3. Right-click **Main.java** → **Run File**

### Option B — Project Properties
1. Right-click the SMIS project → **Properties**
2. Click **Run** on the left
3. Set Main Class to: `com.smis.Main`
4. Click OK → press **F6** or the ▶ Play button

### Option C — Terminal (PowerShell inside SMIS folder)
```
mvn clean package
java --module-path target/dependency --add-modules javafx.controls,javafx.fxml -jar target/StudentMIS-1.0.jar
```

### Option D — mvn javafx:run (Terminal)
```
mvn javafx:run
```

---

## Default Login
| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | Admin |

The database (`smis.db`) is created automatically on first run.

---

## Project Structure
```
SMIS/
├── pom.xml                          ← Maven build config
├── src/main/java/com/smis/
│   ├── Main.java                    ← Entry point (START HERE)
│   ├── database/
│   │   ├── DatabaseConnection.java  ← SQLite connection
│   │   └── DBInitializer.java       ← Creates tables on first run
│   ├── models/                      ← Data classes (User, Student, etc.)
│   │   ├── User.java
│   │   ├── Student.java
│   │   ├── Faculty.java
│   │   ├── Subject.java
│   │   ├── Section.java
│   │   ├── FacultyAssignment.java
│   │   ├── Enrollment.java
│   │   ├── Grade.java
│   │   ├── Attendance.java
│   │   ├── Announcement.java
│   │   └── ActivityLog.java
│   ├── services/                    ← Database operations (SQL queries)
│   │   ├── AuthService.java         ← Login & password hashing
│   │   ├── UserService.java
│   │   ├── StudentService.java
│   │   ├── FacultyService.java
│   │   ├── SubjectService.java
│   │   ├── SectionService.java
│   │   ├── AssignmentService.java
│   │   ├── EnrollmentService.java
│   │   ├── GradeService.java
│   │   ├── AttendanceService.java
│   │   ├── AnnouncementService.java
│   │   └── ActivityLogService.java
│   ├── utils/
│   │   ├── UIUtils.java             ← Reusable UI components & colors
│   │   ├── CourseList.java          ← Master list of courses
│   │   └── SessionManager.java      ← Holds the logged-in user
│   └── views/
│       ├── common/LoginView.java    ← Login screen
│       ├── admin/AdminDashboard.java
│       ├── faculty/FacultyDashboard.java
│       └── student/StudentDashboard.java
└── src/main/resources/
    └── (CSS and config files)
```

---

## How to Use (Admin Workflow)
1. Login as admin / admin123
2. **Sections** → Create a section (e.g. BSIT-1A)
3. **Subjects** → Create subjects (e.g. IT101 - Programming)
4. **Sections** → Click your section → "Assign Subject to Section" → pick subject + teacher
5. **Students** → Create student → select course + section → auto-enrolled!
6. **Assignments** → Select section → see subjects → update teacher/schedule

---

## Learning Tips
- Start reading from `Main.java`
- Then `LoginView.java` to see how login works
- Then `AdminDashboard.java` for the main UI
- Each `Service` class handles one type of database operation
- `UIUtils.java` contains all the reusable buttons, cards, and colors
