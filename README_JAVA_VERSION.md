# Java Version Compatibility

## Important Note

This application uses **JSF (Jakarta Faces) 4.0** with **CDI (Contexts and Dependency Injection)**.

**WildFly 38** requires **Java 11 or later** to run.

## Build Options

### Option 1: Build with Java 11+ (Recommended)
- Install Java 11 or later
- Update `build-windows.bat` to use `-source 11 -target 11`
- This is the recommended approach for WildFly 38

### Option 2: Build with Java 8 (Current)
- The build script compiles with Java 8 (`-source 1.8 -target 1.8`)
- However, WildFly 38's JARs are compiled with Java 11+
- You may encounter class version mismatch warnings
- The application will still run on WildFly 38 (which uses Java 11+)

## JSF Technology

Yes, this application uses **JSF (Jakarta Server Faces)** technology:
- **JSF 4.0** (Jakarta Faces)
- **Facelets** (XHTML templates)
- **CDI** for dependency injection (`@Named`, `@RequestScoped`)
- **EJB** for business logic

The application was migrated from JSP to JSF for better component-based development.


