# user-registration

Small **Spring Boot** REST API that simulates user registration: validates input, looks up an IP with [ip-api.com](http://ip-api.com), and only allows **Canadian** IPs. Returns a user id and a welcome message with the resolved city.

**Stack:** Java 21, Spring Boot 3, Gradle (`build.gradle.kts`).

**Run:** `./gradlew bootRun`.

**Main endpoint:** `POST /api/register` — JSON body: `username`, `password`, `ipAddress`.

**Tests:** `./gradlew test`
