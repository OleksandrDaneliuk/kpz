# Task 6: Pragmatic Analysis (DRY, KISS, YAGNI)

## 1. DRY: Knowledge Duplication Removed

| Task | Duplicated knowledge in legacy code | Single Source of Truth |
|---|---|---|
| 1 | The risk label `"Високий ризик"` was assigned as a string and re-compared as a string. Validation limits, the BMI formula and the risk thresholds were buried in one method. | `RiskCategory` enum holds the label and is compared by `==`. `PatientDataValidator`, `BmiCalculator` and `RiskClassifier` each own one rule set. |
| 2 | Rounding (`setScale`) and logging were tied to a branching block, and each category's rule was embedded in the `if/else` chain. | `DiscountCalculator` rounds and logs once for all categories. Each rule lives only in its `*DiscountStrategy`, and the `Map<CustomerCategory, DiscountStrategy>` is the only category-to-rule mapping. |
| 3 | `Document` and `ReadOnlyPdfDocument` each carried their own state, and `print()` was duplicated by inheritance. Blank/null checks were absent or scattered. | The records `DocumentTitle`, `FilePath` and `DocumentContent` hold the invariants once and are reused by both implementations. `ReadableDocument.print()` is one default method. |
| 4 | Seven `throw new UnsupportedOperationException(...)` stubs repeated the same fact: "this repository can't do X". | Each capability is declared once in one role interface (`IExportable`, `IPurgeable`, ...). A class either implements it or doesn't. |
| 5 | The manager hard-wired Redis, Postgres and SendGrid. A test variant would have needed a copy of the manager. | One `UserProfileManager` holds the update workflow. `PostgresUserRepository` and `MockUserRepository` are swapped via the `IUserRepository` contract in `CompositionRoot`. |

Small residue: the literals `0.05` and `0.10` still appear in several strategies. They are different business rules that happen to share a value, so merging them would be false DRY.

## 2. KISS: Flatter Control Flow

Approximate cyclomatic complexity (decision points + 1):

| Method | Before | After |
|---|---|---|
| `processPatientDiagnostics` (Task 1) | ~15 | `process()`: 3; largest helper `classify()`: 5 |
| `calculateDiscount` (Task 2) | ~9 | `calculateDiscount()`: 2; largest strategy: 3 |

- **Task 1:** `validate()` is a sequence of guard clauses that throw immediately, and `process()` then runs linearly. `classify()` uses early returns instead of a mutable `String riskCategory`. The `try/catch` around the SMS is gone, and storage errors are one typed `ReportStorageException`.
- **Task 2:** the `if/else if` ladder is replaced by one map lookup plus a single null guard.
- **Task 3:** record compact constructors and `DocumentEditor` guard clauses fail fast. Clients no longer need to catch `UnsupportedOperationException`.
- **Task 4:** no stub methods and no `instanceof` checks.
- **Task 5:** `updateProfile` has one guard clause and then three flat calls.

## 3. YAGNI: Justification of Abstractions

- **Task 1:** `PatientValidator`, `ReportFormatter`, `ReportRepository` and `DoctorNotifier` sit on real axes of change: format, storage and notification channel. `BmiCalculator` and `RiskClassifier` are deliberately concrete classes with no interface, because nothing in the domain calls for swapping them.
- **Task 2:** `PARTNER` and `PartnerDiscountStrategy` exist only to demonstrate OCP, as the task requires. They are not speculative.
- **Task 3:** both interfaces are used by real implementations (`EditableDocument` by `TextDocument`, `ReadableDocument` by both).
- **Task 5:** the three interfaces wrap I/O boundaries and are required by DIP. `IUserRepository` has two real implementations.
- **Stubs:** after refactoring, no `NotImplementedException`, `UnsupportedOperationException` or empty "for the future" method remains in Tasks 1–5.

## 4. Cross-Review Findings (fix before defense)

1. **Task 4, YAGNI violation:** `IBulkImportable<T>` is declared but implemented by no class. Remove it, or add a real implementer.
2. **Task 4:** `AnalyticsReportService.generateReport` assigns `data` but never uses it. Use it or delete it.
3. **Task 5:** `testManager` in `CompositionRoot` reuses the real `RedisCacheService` and `SendGridNotificationClient`, so only the repository is mocked. A truly isolated test setup needs mock cache and notification implementations too.
4. **Task 1:** there is a single implementation of each interface. Be ready to defend them as extension points, not over-engineering.
