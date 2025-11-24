# JSF Dependencies for Build

## Required JAR Files

Để compile JSF code, bạn cần tải các JAR files sau vào thư mục `lib/`:

### 1. Jakarta Faces API
- **File:** `jakarta.faces-api-4.0.jar`
- **Download:** https://repo1.maven.org/maven2/jakarta/faces/jakarta.faces-api/4.0/jakarta.faces-api-4.0.jar

### 2. Jakarta CDI API
- **File:** `jakarta.enterprise.cdi-api-4.0.1.jar`
- **Download:** https://repo1.maven.org/maven2/jakarta/enterprise/jakarta.enterprise.cdi-api/4.0.1/jakarta.enterprise.cdi-api-4.0.1.jar

### 3. Jakarta Inject API
- **File:** `jakarta.inject-api-2.0.1.jar`
- **Download:** https://repo1.maven.org/maven2/jakarta/inject/jakarta.inject-api/2.0.1/jakarta.inject-api-2.0.1.jar

## Quick Download (PowerShell)

Chạy lệnh sau để tải tự động:

```powershell
New-Item -ItemType Directory -Path "lib" -Force | Out-Null
Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/jakarta/faces/jakarta.faces-api/4.0/jakarta.faces-api-4.0.jar" -OutFile "lib\jakarta.faces-api-4.0.jar"
Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/jakarta/enterprise/jakarta.enterprise.cdi-api/4.0.1/jakarta.enterprise.cdi-api-4.0.1.jar" -OutFile "lib\jakarta.enterprise.cdi-api-4.0.1.jar"
Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/jakarta/inject/jakarta.inject-api/2.0.1/jakarta.inject-api-2.0.1.jar" -OutFile "lib\jakarta.inject-api-2.0.1.jar"
```

## Note

Các JAR files này chỉ cần cho **compilation**. WildFly 38 đã có JSF built-in ở runtime, nên không cần thêm vào WAR file.

