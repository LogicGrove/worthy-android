# Worthy

Worthy is an open-source Android application for saving money toward products the user wants to buy.

Users can add a product they want, track its price, and progressively add saved money until they reach the target amount.

The primary language of the application is English, but the app must be designed for localization.

## Product philosophy

Worthy should make saving for something feel simple, motivating, visual, and satisfying.

It is not a traditional budgeting app and should not feel like a bank or finance dashboard.

The product should focus on:

- saving toward specific things the user wants
- visual progress
- simplicity
- motivation
- clear price tracking
- a polished native Android experience

Avoid turning Worthy into a general-purpose budgeting, banking, investment, or expense-tracking application unless explicitly requested.

## Current project

The project is currently a single-module Android application.

Application package:

app.worthy.android

Current Android configuration:

- minSdk: 24
- targetSdk: 37
- compileSdk: 37

Current main technologies:

- Kotlin
- Jetpack Compose
- Material 3
- Gradle Kotlin DSL
- AndroidX
- Kotlin Coroutines when asynchronous work is needed
- Flow / StateFlow for observable application state

The project currently uses Material 3 1.4.0 through the Compose BOM and supports stable Material 3 Expressive functionality.

Do not change the applicationId, namespace, minSdk, targetSdk, or compileSdk unless explicitly requested.

## UI technology

Use Jetpack Compose for application UI.

Do not introduce XML layouts unless there is a strong technical reason and the change is explicitly justified.

Prefer official Material 3 components.

Use Material 3 Expressive APIs where they improve the user experience.

Do not use Expressive APIs simply for novelty.

The app should feel like a high-quality modern Android application, with particular inspiration from native Material 3 and Pixel-style interaction patterns.

## Material 3 Expressive

Worthy should actively adopt Material 3 Expressive where appropriate.

Important areas include:

- expressive shapes
- expressive typography
- motion
- progress visualization
- responsive component transformations
- clear hierarchy
- tactile interaction feedback

Use stable Material 3 APIs whenever possible.

Experimental Material 3 Expressive APIs may be used when they provide meaningful value, but:

- keep experimental usage localized
- use the appropriate OptIn annotations
- do not make the entire architecture depend on unstable APIs
- avoid experimental APIs when a stable equivalent is sufficient

## Visual design

The visual direction should be:

- minimal
- expressive
- playful without being childish
- modern
- clean
- spacious
- highly legible
- emotionally motivating

Prefer:

- generous spacing
- strong hierarchy
- large titles when appropriate
- expressive but coherent shapes
- carefully chosen motion
- clear progress indicators
- strong primary actions
- uncluttered surfaces

Avoid:

- glassmorphism
- excessive gradients
- generic fintech dashboards
- unnecessary borders
- excessive cards
- putting every piece of content inside a container
- arbitrary shadows
- overly dense screens
- decorative animations with no functional purpose
- random custom colors when Material theme colors are sufficient

## Theme

Worthy must support:

- light theme
- dark theme
- dynamic color when appropriate

Dynamic color should not prevent Worthy from having a recognizable identity.

If brand identity becomes important, use a deliberate fallback color scheme while still supporting dynamic color.

Do not hardcode visual colors directly inside screens.

Use MaterialTheme.colorScheme or the project's design system.

Typography and shapes should eventually be defined deliberately instead of relying entirely on Material defaults.

## Compose conventions

Follow idiomatic Jetpack Compose practices.

Prefer:

- small focused composables
- stateless reusable composables
- state hoisting
- immutable parameters
- unidirectional data flow
- lifecycle-aware collection of Flow and StateFlow
- previews for important components and screens

Do not:

- put business logic inside composables
- perform networking directly from composables
- access persistence directly from composables
- create giant screen composables
- use remember for application-level state
- create unnecessary mutable state

Use remember only for UI-local state.

Use ViewModel for screen-level application state.

## Screen state

Prefer immutable screen state.

A typical pattern is:

data class HomeUiState(
val goals: List<SavingsGoal> = emptyList(),
val isLoading: Boolean = false
)

class HomeViewModel : ViewModel() {
private val _uiState = MutableStateFlow(HomeUiState())
val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
}

Keep mutable flows private.

Expose immutable StateFlow.

Represent loading, empty, error, and content states explicitly when they matter.

## Architecture

Do not over-engineer the project while it is small.

Prefer a feature-oriented architecture that can grow gradually.

A possible future structure is:

app.worthy.android
├── core
│   ├── designsystem
│   ├── model
│   └── util
├── data
│   ├── local
│   ├── remote
│   └── repository
├── domain
│   └── model
├── feature
│   ├── home
│   ├── goal
│   ├── addgoal
│   └── settings
└── navigation

Do not create all of these packages in advance.

Create architecture only when a feature requires it.

Prefer simple code over theoretical architectural purity.

Do not introduce interfaces, use cases, repositories, mapping layers, or abstractions unless they solve a real problem.

## Main product model

The central concept is a savings goal tied to a product.

A savings goal may eventually contain:

- id
- product name
- product URL
- product image
- current product price
- original product price
- currency
- amount saved
- target amount
- creation date
- last price check
- optional notes

This model is not final.

Do not treat these fields as fixed requirements unless the current feature needs them.

## Money

Money must never use Float or Double.

Use a precise representation.

Prefer either:

- integer minor units such as cents
- a precise decimal type when necessary

All calculations involving:

- prices
- savings
- percentages
- remaining amounts

must avoid floating-point rounding errors.

Currency must not be assumed to always be USD or EUR.

## Product URLs and price tracking

Worthy is expected to accept product links and retrieve product information such as price.

Do not assume arbitrary websites can always be scraped reliably.

Price extraction must be separated from UI code.

Do not put HTML parsing, scraping logic, or network calls directly inside:

- composables
- activities
- ViewModels

Keep product metadata retrieval behind an abstraction.

The application must handle:

- unsupported websites
- missing prices
- changed page structures
- unavailable products
- temporary network failures
- price changes
- invalid URLs

gracefully.

Do not silently overwrite important user information.

If the tracked price changes, the UI should make the change understandable to the user.

Do not implement fragile site-specific scraping unless explicitly requested.

If a backend becomes necessary in the future, keep the Android client independent of backend implementation details.

## Persistence

For structured local application data, prefer Room.

For small preferences and settings, prefer DataStore.

Do not introduce SharedPreferences for new functionality.

Do not add persistence before a feature actually requires it.

## Networking

Use one networking stack.

Do not introduce multiple HTTP clients.

Before adding a dependency, first check whether:

- the project already includes a suitable library
- AndroidX provides the functionality
- Kotlin or Java standard APIs provide the functionality

Prefer established, actively maintained libraries.

Explain significant third-party dependencies before adding them.

## Navigation

Use Compose Navigation when multiple screens are introduced.

Prefer type-safe navigation APIs when supported by the project's current dependency versions.

Do not pass large application objects through navigation arguments.

Pass identifiers and load state through the appropriate data layer.

## Localization

English is the primary language.

All production user-visible text must be localizable.

Prefer string resources.

Do not permanently hardcode UI strings inside composables.

Prototype text may temporarily be hardcoded during experimentation, but should be moved to resources before considering a feature complete.

Layouts must tolerate longer translated strings.

Do not assume:

- US date formats
- US currency
- decimal point formatting
- fixed text widths

Use locale-aware formatting for money, dates, and numbers.

## Accessibility

Accessibility is a requirement, not an optional polish step.

Interactive elements should:

- have appropriate semantics
- have sufficient touch targets
- work with font scaling
- maintain readable contrast
- not communicate important state only through color

Use content descriptions when they provide useful information.

Decorative images should not receive unnecessary content descriptions.

## Kotlin style

Write idiomatic Kotlin.

Prefer:

- val over var
- immutable data
- small functions
- descriptive names
- clear control flow
- expression syntax when it improves readability
- named arguments when calls would otherwise be unclear

Avoid:

- Java-style Kotlin
- global mutable state
- huge utility objects
- magic values
- unnecessary nullable values
- premature abstractions
- deeply nested code
- excessive extension functions

## Dependencies

Prefer official AndroidX and Kotlin libraries.

Before adding a dependency:

1. Check whether the project already has equivalent functionality.
2. Check whether AndroidX or Kotlin already provides a reasonable solution.
3. Prefer the smallest appropriate dependency.
4. Avoid abandoned or obscure libraries.
5. Avoid dependencies that create significant lock-in.
6. Explain major new dependencies.

Do not upgrade unrelated dependencies as part of a small feature unless the upgrade is necessary.

## Dependency modernization

The current project contains some AndroidX dependencies that are significantly older than the Compose, AGP, and SDK configuration.

Do not blindly upgrade all dependencies.

When modernization is requested:

- inspect current stable versions
- check compatibility with AGP, Kotlin, Compose, and minSdk
- update deliberately
- build after changes
- avoid unrelated dependency churn

## Gradle

Use Gradle Kotlin DSL.

Use the existing version catalog in:

gradle/libs.versions.toml

for dependency and plugin versions where appropriate.

Avoid duplicating version numbers across Gradle files.

Do not modify the Gradle wrapper, AGP version, Kotlin version, JVM configuration, or Android SDK levels casually.

Build configuration changes must have a clear reason.

## JVM configuration

The current Gradle daemon uses Java 25 while Java source and target compatibility are configured for Java 11.

Do not change this automatically.

If JVM alignment is requested:

- inspect AGP requirements
- inspect Gradle requirements
- inspect Android compatibility
- choose a deliberate toolchain
- explain the tradeoffs before changing it

Do not assume that using the newest JDK is automatically the best project configuration.

## Release configuration

Release optimization is currently disabled.

Do not enable R8, shrinking, obfuscation, or release optimization unless explicitly requested or when preparing a production release.

When production release configuration is introduced, review:

- shrinking
- resource shrinking
- ProGuard/R8 rules
- signing
- reproducibility
- secrets handling

carefully.

## Testing

Add tests when they provide meaningful protection.

Prioritize tests for:

- money calculations
- savings progress
- repository behavior
- URL normalization
- product metadata parsing
- price updates
- ViewModel state transitions
- persistence behavior

Use Compose UI tests for important user flows when useful.

Do not add trivial tests solely to increase test count.

## Compose previews

Important visual components should have previews.

Add previews for:

- reusable components
- major screens
- important empty states
- loading states
- representative goal states

Use light and dark previews where useful.

When possible, render and inspect previews after significant UI changes.

Do not claim a UI looks correct if it has not been rendered or tested.

## Open source

Worthy is an open-source project.

Keep the repository clean and reproducible.

Never commit:

- API keys
- tokens
- passwords
- local machine paths
- private certificates
- signing keys
- service credentials
- local.properties
- generated secrets

Do not weaken .gitignore rules to expose local files.

Avoid requiring proprietary services unless explicitly requested.

## Git

Keep changes focused.

Do not mix unrelated refactors into feature work.

Do not rewrite Git history.

Do not force push.

Do not create commits unless explicitly requested.

Before broad changes, inspect git status.

Never discard user changes without explicit permission.

## Working rules for Codex

Before modifying files:

1. Inspect the files relevant to the task.
2. Understand existing project conventions.
3. Check whether the requested functionality already partially exists.
4. Make a focused implementation plan for non-trivial work.
5. Avoid unrelated changes.

For significant tasks:

1. Explain the intended approach briefly.
2. Modify only the necessary files.
3. Build or test the affected code.
4. Fix errors introduced by the change.
5. Summarize the changes and verification performed.

For small and obvious changes, avoid unnecessary planning overhead.

## UI tasks

Before modifying UI:

1. Inspect the current theme.
2. Inspect existing typography.
3. Inspect existing shapes.
4. Inspect reusable components.
5. Reuse the design system where possible.

Do not invent unrelated styling independently inside each screen.

If a design system grows, centralize reusable:

- colors
- typography
- shapes
- motion
- spacing patterns
- reusable components

appropriately.

## Verification

After significant code changes, run the most relevant checks.

On Windows, prefer:

.\gradlew.bat assembleDebug

For relevant unit tests:

.\gradlew.bat test

Run narrower tasks when they are sufficient.

Do not run expensive unrelated checks without reason.

If Gradle execution may modify caches or download dependencies, that is expected during normal development and does not count as modifying project source files.

If verification cannot be completed, report the exact reason.

Never claim that:

- the project builds
- tests pass
- a preview renders
- a feature works

unless it was actually verified.

## Scope discipline

Do not change the following unless explicitly requested:

- applicationId
- namespace
- package name
- minSdk
- targetSdk
- compileSdk
- project name

Do not introduce the following unless explicitly requested:

- authentication
- analytics
- advertising
- cloud sync
- backend services
- account systems
- telemetry
- crash reporting
- dependency injection frameworks

Keep implementation proportional to the current stage of the project.

## Decision making

When multiple solutions are reasonable:

- prefer the simplest
- prefer official Android APIs
- prefer maintainable code
- prefer solutions that are easy to change later
- avoid unnecessary lock-in

If a decision has meaningful long-term consequences, explain it before implementing it.

## Final principle

Worthy should remain simple.

Do not add complexity because it might be useful someday.

Build the product that is needed now while keeping the codebase easy to evolve.