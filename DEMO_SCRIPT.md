# Demo Script - AI-Assisted Legacy System Modernization

## Demo Overview

This demo showcases how AI can assist with:
1. Understanding and analyzing legacy JSP + EJB code
2. Generating CRUD modules
3. Updating code when adding new fields
4. Generating test cases and automation scripts

**Duration:** 30-45 minutes  
**Audience:** Technical stakeholders, developers, project managers

---

## Pre-Demo Setup

### Prerequisites
- [ ] Application deployed and running on WildFly
- [ ] Database configured and accessible
- [ ] Browser ready (Chrome)
- [ ] Terminal/IDE ready for code viewing
- [ ] Test data prepared

### Environment Check
```bash
# Verify WildFly is running
curl http://localhost:8080/employee-demo/

# Verify database connection
mysql -u root -p employee_db -e "SELECT COUNT(*) FROM employees;"
```

---

## PART 1: Original CRUD Application (10 minutes)

### Step 1.1: Show Application Structure
**Action:** Open project in IDE/editor

**Narrate:**
> "This is a legacy Java EE application built with JSP, Servlet, and EJB. Let me show you the structure."

**Show:**
```
employee-demo/
├── ejb-module/     (EJB business logic)
├── web-module/     (JSP + Servlet)
└── database/      (Schema)
```

**Key Points:**
- Classic 3-tier architecture
- No modern frameworks (Spring, Hibernate)
- Java 6 compatible
- Plain JDBC for database access

---

### Step 1.2: Demonstrate Original CRUD
**Action:** Open browser to `http://localhost:8080/employee-demo/`

**Narrate:**
> "The application currently supports basic CRUD operations for Employee entities with four fields: ID, Name, Email, and Department."

**Demonstrate:**
1. **List View:**
   - Show employee list
   - Point out: ID, Name, Email, Department columns
   - Show "Add New Employee" button

2. **Create:**
   - Click "Add New Employee"
   - Fill form: Name, Email, Department
   - Submit
   - Show success message
   - Verify new employee in list

3. **View:**
   - Click "View" on an employee
   - Show detail page with all fields

4. **Edit:**
   - Click "Edit"
   - Modify name
   - Submit
   - Verify update in list

5. **Delete:**
   - Click "Delete"
   - Confirm deletion
   - Verify removal from list

**Key Points:**
- Application works correctly
- All CRUD operations functional
- Clean, simple UI

---

### Step 1.3: Show Code Architecture
**Action:** Open key files in IDE

**Narrate:**
> "Let me show you how the code is structured. This is a typical legacy pattern."

**Show Files:**

1. **Employee.java (Model)**
   ```java
   // Show: 4 fields (id, name, email, department)
   // Show: Getters/setters
   ```

2. **EmployeeDAO.java (Data Access)**
   ```java
   // Show: JDBC queries
   // Show: INSERT, SELECT, UPDATE, DELETE
   // Point out: No phoneNumber in queries
   ```

3. **EmployeeServiceBean.java (Business Logic)**
   ```java
   // Show: Validation logic
   // Show: Email uniqueness check
   ```

4. **EmployeeServlet.java (Controller)**
   ```java
   // Show: Request handling
   // Show: Parameter extraction
   ```

5. **employee-form.jsp (View)**
   ```jsp
   // Show: Form fields
   // Point out: Only 3 input fields
   ```

**Key Points:**
- Clear separation of concerns
- Standard JSP/EJB pattern
- All layers work together

---

## PART 2: AI Code Analysis (5 minutes)

### Step 2.1: AI Understanding the Codebase
**Action:** Use AI to analyze the project

**Narrate:**
> "Now, let's see how AI can understand this legacy codebase. I'll ask the AI to analyze the project structure."

**Demonstrate:**
1. Ask AI: "Analyze the Employee CRUD application structure"
2. Show AI response identifying:
   - Architecture layers
   - Data flow
   - Key components
   - Dependencies

**Key Points:**
- AI understands legacy patterns
- AI identifies all components
- AI maps relationships

---

### Step 2.2: AI Explaining Code Flow
**Action:** Ask AI to explain a specific flow

**Narrate:**
> "Let me ask the AI to explain how creating an employee works end-to-end."

**Demonstrate:**
- Ask: "Explain the flow from JSP form submission to database insert"
- Show AI response with:
  - JSP → Servlet → EJB → DAO → Database
  - Parameter flow
  - Validation steps
  - Transaction handling

**Key Points:**
- AI understands complete flow
- AI can explain complex interactions
- AI identifies all touchpoints

---

## PART 3: AI Generating Test Cases (5 minutes)

### Step 3.1: Show Test Case Generation
**Action:** Open test cases document

**Narrate:**
> "AI can generate comprehensive test cases for the application."

**Show:**
- Open `tests/functional/functional-test-cases.md`
- Point out:
  - 15 test cases covering all scenarios
  - Create, Read, Update, Delete
  - Validation scenarios
  - Edge cases

**Key Points:**
- Comprehensive coverage
- Well-structured format
- Ready for execution

---

### Step 3.2: Show Selenium Automation
**Action:** Show Selenium test code

**Narrate:**
> "AI also generated Selenium automation scripts with Page Object pattern."

**Show:**
1. **Page Objects:**
   - `EmployeeListPage.java`
   - `EmployeeFormPage.java`
   - `EmployeeDetailPage.java`

2. **Test Class:**
   - `EmployeeCRUDTest.java`
   - Show test methods

3. **Test Data:**
   - `employee-test-data.json`
   - `employee-test-data.csv`

**Key Points:**
- Page Object pattern (best practice)
- Reusable components
- Data-driven tests

---

## PART 4: Customer Change Request (10 minutes)

### Step 4.1: Present Change Request
**Action:** Announce the requirement

**Narrate:**
> "Now, the customer wants to add a new field: **Phone Number**. This is a common scenario in legacy maintenance. Let's see how AI can help update the entire stack."

**Requirement:**
- Add `phoneNumber` field to Employee
- Optional field (nullable)
- Max 20 characters
- Update entire application stack

**Key Points:**
- Real-world scenario
- Requires changes across all layers
- Must maintain backward compatibility

---

### Step 4.2: AI Analyzing Impact
**Action:** Ask AI to identify all changes needed

**Narrate:**
> "First, let's ask AI to identify all the places that need to be updated."

**Demonstrate:**
- Ask AI: "What files need to be updated to add phoneNumber field?"
- Show AI response listing:
  - Database schema
  - Model class
  - DAO (all SQL queries)
  - Service (validation)
  - Servlet (parameter extraction)
  - All JSP pages

**Key Points:**
- AI identifies all touchpoints
- No manual analysis needed
- Comprehensive impact assessment

---

### Step 4.3: AI Generating Updated Code
**Action:** Show AI-generated updates

**Narrate:**
> "Now AI will generate all the updated code files. Let me show you the changes."

**Show Updated Files:**

1. **Database:**
   ```sql
   ALTER TABLE employees ADD COLUMN phone_number VARCHAR(20) NULL;
   ```

2. **Employee.java:**
   ```java
   private String phoneNumber;
   // + getter/setter
   ```

3. **EmployeeDAO.java:**
   ```java
   // All SQL queries updated
   INSERT ... phone_number ...
   SELECT ... phone_number ...
   UPDATE ... phone_number ...
   ```

4. **EmployeeServiceBean.java:**
   ```java
   // Validation added
   if (phoneNumber.length() > 20) ...
   ```

5. **EmployeeServlet.java:**
   ```java
   employee.setPhoneNumber(request.getParameter("phoneNumber"));
   ```

6. **JSP Pages:**
   - Form: New input field
   - List: New column
   - Detail: New display field

**Key Points:**
- All files updated consistently
- No manual changes needed
- Code follows same patterns

---

### Step 4.4: Show Before/After Comparison
**Action:** Open comparison document

**Narrate:**
> "AI also generated a detailed comparison document showing exactly what changed."

**Show:**
- Open `BEFORE_AFTER_COMPARISON.md`
- Point out:
  - Side-by-side code comparisons
  - Change summary table
  - Impact analysis
  - Testing checklist

**Key Points:**
- Clear documentation
- Easy to review changes
- Audit trail

---

## PART 5: Deploy and Test Updated Application (10 minutes)

### Step 5.1: Apply Database Migration
**Action:** Run SQL script

**Narrate:**
> "First, we need to update the database schema."

**Execute:**
```sql
ALTER TABLE employees ADD COLUMN phone_number VARCHAR(20) NULL;
```

**Verify:**
```sql
DESCRIBE employees;
-- Show phone_number column
```

---

### Step 5.2: Rebuild and Redeploy
**Action:** Build and deploy updated application

**Narrate:**
> "Now we rebuild and redeploy the application with the updated code."

**Execute:**
```bash
# Rebuild
ant build

# Deploy
cp dist/employee-demo.ear $WILDFLY_HOME/standalone/deployments/
```

**Verify:**
- Check WildFly logs for successful deployment
- Verify no errors

---

### Step 5.3: Test Updated Application
**Action:** Test in browser

**Narrate:**
> "Let's test the updated application with the new phoneNumber field."

**Demonstrate:**

1. **List View:**
   - Show new "Phone Number" column
   - Show "N/A" for existing employees (no phone number)

2. **Create with Phone Number:**
   - Click "Add New Employee"
   - Fill all fields including Phone Number
   - Submit
   - Verify phone number appears in list

3. **Edit Phone Number:**
   - Edit an employee
   - Update phone number
   - Verify change saved

4. **Detail View:**
   - View employee
   - Show phone number displayed
   - Show "N/A" if null

5. **Validation:**
   - Try phone number > 20 characters
   - Show validation error

**Key Points:**
- All functionality works
- Backward compatible (existing data OK)
- Validation works correctly

---

### Step 5.4: Show Updated Test Cases
**Action:** Show updated test documentation

**Narrate:**
> "AI also updated the test cases to include phoneNumber scenarios."

**Show:**
- Updated test cases in comparison document
- New test scenarios:
  - Create with phone number
  - Create without phone number (optional)
  - Update phone number
  - Validation tests

**Key Points:**
- Tests updated automatically
- Coverage maintained
- Ready for execution

---

## PART 6: Run Automated Tests (5 minutes)

### Step 6.1: Run Selenium Tests
**Action:** Execute Selenium test suite

**Narrate:**
> "Let's run the Selenium automation tests to verify everything works."

**Execute:**
```bash
cd tests/selenium
java -cp ".:selenium-jar:junit.jar" selenium.TestRunner
```

**Show:**
- Test execution output
- Pass/fail results
- Test report

**Key Points:**
- Automated verification
- Regression testing
- Confidence in changes

---

### Step 6.2: Show Test Report
**Action:** Display test results

**Narrate:**
> "All tests pass, confirming the changes work correctly across the entire stack."

**Show:**
- Test summary
- All tests passing
- Coverage report

---

## PART 7: Summary and Q&A (5 minutes)

### Step 7.1: Recap Key Benefits
**Narrate:**
> "Let me summarize what we've demonstrated today."

**Key Benefits:**

1. **AI Code Understanding:**
   - Analyzes legacy code structure
   - Understands relationships
   - Explains complex flows

2. **AI Code Generation:**
   - Generates complete CRUD modules
   - Follows legacy patterns
   - No modern framework dependencies

3. **AI Code Updates:**
   - Updates entire stack consistently
   - Maintains code patterns
   - No manual changes needed

4. **AI Test Generation:**
   - Functional test cases
   - Selenium automation
   - Test data generation

5. **Time Savings:**
   - Manual: 4-6 hours for this change
   - With AI: 30-45 minutes
   - **80-90% time reduction**

---

### Step 7.2: Address Questions
**Action:** Open floor for questions

**Common Questions:**

**Q: Can AI handle more complex changes?**  
A: Yes, AI can handle:
- Multiple fields
- Complex validations
- New relationships
- Business rule changes

**Q: What about testing?**  
A: AI generates:
- Test cases
- Automation scripts
- Test data
- Regression tests

**Q: Is the code production-ready?**  
A: Yes, but always:
- Review AI-generated code
- Run tests
- Perform manual verification
- Follow your SDLC process

**Q: Can AI work with other legacy technologies?**  
A: Yes, AI can work with:
- Struts
- JSF
- Spring (older versions)
- Custom frameworks

---

## Demo Closing

**Final Message:**
> "This demo showed how AI can significantly accelerate legacy system maintenance while maintaining code quality and consistency. The same approach can be applied to any change request, from adding fields to implementing new features."

**Next Steps:**
- Review generated code
- Run full test suite
- Deploy to staging
- Gather feedback

---

## Demo Checklist

### Pre-Demo
- [ ] Application deployed and running
- [ ] Database accessible
- [ ] Test data prepared
- [ ] Browser ready
- [ ] IDE/editor ready
- [ ] All documentation ready

### During Demo
- [ ] Show original application
- [ ] Show code structure
- [ ] Demonstrate AI analysis
- [ ] Show test case generation
- [ ] Present change request
- [ ] Show AI-generated updates
- [ ] Apply changes
- [ ] Test updated application
- [ ] Run automated tests
- [ ] Answer questions

### Post-Demo
- [ ] Provide access to code repository
- [ ] Share documentation
- [ ] Schedule follow-up if needed

---

## Troubleshooting

**If application doesn't start:**
- Check WildFly logs
- Verify database connection
- Check JNDI names

**If tests fail:**
- Verify application URL
- Check ChromeDriver version
- Verify test data

**If database errors:**
- Verify schema migration applied
- Check column exists
- Verify permissions

---

## Demo Tips

1. **Practice the flow** before the demo
2. **Have backup plans** for each step
3. **Keep it interactive** - ask for input
4. **Show, don't just tell** - demonstrate everything
5. **Be honest** about limitations
6. **Focus on benefits** - time savings, quality
7. **Address concerns** proactively

---

**End of Demo Script**

