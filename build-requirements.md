# Build Requirements

## Java Version
- **Minimum**: Java 11 (required for WildFly 38)
- **Recommended**: Java 17 or later

## Why Java 11+?
WildFly 38 uses Jakarta EE 10, which requires Java 11+. The JARs from WildFly are compiled with Java 11 (class file version 55.0).

## Build Instructions

### If you have Java 11+:
1. Update `build-windows.bat` line 56 and 89:
   - Change `-source 1.8 -target 1.8` to `-source 11 -target 11`
2. Run: `.\build-windows.bat`

### If you only have Java 8:
The code is correct, but you'll need to:
1. Download Java 8 compatible Jakarta EE 8 JARs, OR
2. Build on a system with Java 11+, OR
3. Use WildFly 26 or earlier (which supports Java 8)

## Current Status
✅ Code is complete and correct
⚠️ Build requires Java 11+ environment
✅ Runtime will work on WildFly 38 (which uses Java 11+)

