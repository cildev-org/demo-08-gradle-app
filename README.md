# demo-08-gradle-app

Blue-Oryx demo target: a Gradle (Kotlin DSL) application dependency PR (vulnerable on purpose).

A tiny Java web app whose `/render` runs user input through Apache Commons
Text's `StringSubstitutor`. `build.gradle.kts` declares
`org.apache.commons:commons-text:1.9` and `gradle.lockfile` locks it; 1.9 has
CVE-2022-42889 (Text4Shell): the `url` interpolation lookup is on by default,
so `${url:...}` makes the app fetch a URL. The fix is `commons-text 1.10.0`.

Blue-Oryx opens a pull request that moves the coordinate in
`build.gradle.kts` to 1.10.0 and rebuilds `gradle.lockfile`; after a person
merges it and the image is deployed, the same exploit is blocked. Blue-Oryx
never merges and never deploys.

| Branch | Purpose |
| --- | --- |
| `main` | What Blue-Oryx patches |
| `vulnerable-baseline` | The vulnerable state, for resetting `main` between demo runs |
