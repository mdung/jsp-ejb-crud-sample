# JSF Ternary Operator Fix

## Problem
JSF Expression Language (EL) does not allow ternary operators directly in the `action` attribute of `h:commandLink` or `h:commandButton`.

**Error:**
```
Not a Valid Method Expression: #{employee.active ? employeeBean.deactivateEmployee : employeeBean.activateEmployee}
```

## Solution
Created a single method `toggleEmployeeStatus()` that handles the conditional logic internally.

### Changes Made:

1. **EmployeeBean.java** - Added new method:
```java
public String toggleEmployeeStatus() {
    Long id = getEmployeeIdFromRequest();
    // ... gets employee, checks status, calls activate or deactivate
}
```

2. **employee-list.xhtml** - Changed from:
```xhtml
action="#{employee.active ? employeeBean.deactivateEmployee : employeeBean.activateEmployee}"
```
To:
```xhtml
action="#{employeeBean.toggleEmployeeStatus}"
```

3. **employee-detail.xhtml** - Same fix applied

## Why This Works
- JSF requires a single method reference in `action` attribute
- The conditional logic is moved to Java code (better practice)
- Ternary operators can still be used in `value` and `style` attributes (for display)

## Status
✅ Fixed in code
⚠️ Build requires Java 11+ (separate issue)

