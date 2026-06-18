/*
 * Employee validation rules executed by GroovyEmployeeValidationEngine.
 *
 * Bindings (injected by the engine):
 *   - request : EmployeeRequestDTO — raw REST input
 *   - today   : LocalDate          — reference date for age and date checks
 *
 * Return contract:
 *   - Failure: [errors: List<String>]
 *   - Success: Map with keys names, lastNames, typeDocument, documentNumber,
 *              dateOfBirth, dateAffiliationCompany, position, salary
 */
package validation

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle

def errors = []
def dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE.withResolverStyle(ResolverStyle.STRICT)

def validateNotBlank = { field, value ->
    if (value == null || value.trim().isEmpty()) {
        errors << "${field} must not be blank"
    }
}

validateNotBlank('Names', request.names)
validateNotBlank('Last Names', request.lastNames)
validateNotBlank('Type Document', request.typeDocument)
validateNotBlank('Document Number', request.documentNumber)
validateNotBlank('Date Of Birth', request.dateOfBirth)
validateNotBlank('Date Affiliation Company', request.dateAffiliationCompany)
validateNotBlank('Position', request.position)
validateNotBlank('Salary', request.salary)

LocalDate birthDate = null
LocalDate hireDate = null
Double salary = null

def parseDate = { field, value ->
    if (value == null || value.trim().isEmpty()) {
        return null
    }
    try {
        return LocalDate.parse(value.trim(), dateFormatter)
    } catch (DateTimeParseException ignored) {
        errors << "${field} must use format yyyy-MM-dd"
        return null
    }
}

def parseSalary = { value ->
    if (value == null || value.trim().isEmpty()) {
        return null
    }
    try {
        return Double.parseDouble(value.trim())
    } catch (NumberFormatException ignored) {
        errors << 'salary must be a valid number'
        return null
    }
}

birthDate = parseDate('Date Of Birth', request.dateOfBirth)
hireDate = parseDate('Date Affiliation Company', request.dateAffiliationCompany)
salary = parseSalary(request.salary)

if (!errors.isEmpty()) {
    return [errors: errors]
}

if (birthDate.isAfter(today)) {
    errors << 'Date Of Birth must not be a future date'
}
if (hireDate.isAfter(today)) {
    errors << 'Date Affiliation Company must not be a future date'
}
if (!birthDate.isBefore(hireDate)) {
    errors << 'Date Affiliation Company must be after dateOfBirth'
}
if (birthDate.plusYears(18).isAfter(today)) {
    errors << 'Employee must be of legal age (18 years or older)'
}
if (salary <= 0) {
    errors << 'Salary must be greater than zero'
}

if (!errors.isEmpty()) {
    return [errors: errors]
}

return [
    names: request.names.trim(),
    lastNames: request.lastNames.trim(),
    typeDocument: request.typeDocument.trim(),
    documentNumber: request.documentNumber.trim(),
    dateOfBirth: birthDate,
    dateAffiliationCompany: hireDate,
    position: request.position.trim(),
    salary: salary
]
