---
name: verify-indicators
description: Run the full regression suite for the Compose dots indicators (logic + behaviour + golden screenshots) and relay any screenshot diffs to the agent for a visual verdict. Use after any change to the library's Compose indicators (Shift, Balloon, Spring, Worm), their positioning math, or RTL handling.
---

# Verify the Compose dots indicators

This is the automated replacement for the manual regression pass (swipe every page and
eyeball each of the 4 indicator types, add/remove pages in edge situations, tap dots to
navigate). It runs three deterministic test tiers on the JVM (Robolectric, no emulator)
and, when screenshots change, **looks at the diff images itself** and gives a verdict.

The tests live in `viewpagerdotsindicator/src/test/kotlin/com/tbuonomo/viewpagerdotsindicator/compose/`:
- `ComputationsTest` — pure positioning/sizing math.
- `DotsIndicatorBehaviorTest` — dot-tap navigation, dot counts, add/remove page edge cases, LTR+RTL no-crash.
- `DotsIndicatorScreenshotTest` — golden screenshots, 4 types × {LTR, RTL} × 4 states, committed under `src/test/screenshots/`.

## Steps

### 1. Run logic + behaviour tests
```
./gradlew :viewpagerdotsindicator:testDebugUnitTest --tests "com.tbuonomo.viewpagerdotsindicator.compose.ComputationsTest" --tests "com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicatorBehaviorTest"
```
If anything fails, read the report at
`viewpagerdotsindicator/build/reports/tests/testDebugUnitTest/index.html` and the stack
trace, then report the failing test and likely cause. Do not continue to step 2 until the
user decides.

### 2. Verify the golden screenshots
```
./gradlew :viewpagerdotsindicator:verifyRoborazziDebug
```
- **Passes** → all dots are pixel-identical to the committed goldens. Report success.
- **Fails** → one or more renders drifted. Go to step 3.

> ⚠️ **Platform note:** the committed goldens are recorded on **Linux** (to match CI).
> Native-graphics rendering is OS-specific, so `verifyRoborazziDebug` run on macOS/Windows
> will report pixel diffs that are **not** real regressions. On a non-Linux machine, skip the
> exact verify and instead do step 3 against freshly **recorded** images: run
> `./gradlew :viewpagerdotsindicator:recordRoborazziDebug` and visually judge the regenerated
> `src/test/screenshots/*.png` with the checklist below (do **not** commit those — they would
> break CI). The authoritative exact check happens in CI on Linux.

### 3. AI relay — judge the diffs
On failure, Roborazzi writes comparison images to
`viewpagerdotsindicator/build/outputs/roborazzi/`. The relevant files end in `_compare.png`
(side-by-side: expected | actual | diff). List them:
```
ls viewpagerdotsindicator/build/outputs/roborazzi/*_compare.png
```
**Open each failing `_compare.png` with the Read tool** (it renders the image) and judge the
*actual* (middle/right) panel against this checklist:

- **Page 0**: selected dot at the **left** edge in LTR, at the **right** edge in RTL.
- **Last page**: selected dot at the **right** edge in LTR, at the **left** edge in RTL.
- **Mid page**: selected dot on the **center** dot.
- **Selected dot advances one step per page**, in the reading direction (left→right LTR,
  right→left RTL).
- **Worm** mid-transition (the `*_o05` frames): the worm is **elongated**, spanning the two
  dots it is moving between — toward the right in LTR, toward the left in RTL.
- **Shift / Balloon**: the selected dot is the **largest**; others are base size.
- No dot is clipped, off-screen, or missing; there are always 5 dots.

Then classify each diff:
- **Intended improvement** (the new render is correct and better): tell the user to
  re-record and review, then commit the updated goldens:
  ```
  ./gradlew :viewpagerdotsindicator:recordRoborazziDebug
  ```
- **Regression** (the new render violates the checklist): report exactly which
  type / direction / state broke (the golden name, e.g. `worm_rtl_p0_o05`) and what is
  visually wrong, so it can be fixed before merging.

### 4. Summary
Print a concise per-type PASS/FAIL table (Shift, Balloon, Spring, Worm), note any goldens
that need re-recording, and state the overall verdict.

## (Re)recording the committed goldens

The committed goldens **must be recorded on Linux** so they match CI. Don't commit goldens
recorded on macOS/Windows — they will fail CI's exact verify. To regenerate the trusted set:

1. Let CI record them: push a branch with the CI test step temporarily set to
   `recordRoborazziDebug` and an artifact upload of
   `viewpagerdotsindicator/src/test/screenshots/**`, download the artifact, commit those PNGs,
   then revert the CI step to `verifyRoborazziDebug`. (Or record inside a Linux container.)
2. Before committing, open a representative sample (at minimum the LTR + RTL `*_p0_o0` and
   `worm_*_p0_o05` frames) with the Read tool and confirm against the checklist above —
   goldens are trusted only after a human/AI has eyeballed them once.

## Notes
- Everything runs on the JVM via Robolectric — no emulator or device is required.
- Requires JDK 17+ (the module toolchain is 21).
- Goldens are recorded at `@Config(sdk = [34])`; they will not match if the SDK level in
  `DotsIndicatorScreenshotTest` changes.
- To add a new scenario, add a row to the `states`/`types` matrix in
  `DotsIndicatorScreenshotTest`, then `recordRoborazziDebug` and review the new golden.
