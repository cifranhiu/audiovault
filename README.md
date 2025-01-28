## Running The Application

To run the application, run the following command

```bash
  docker-compose build && docker-compose up
```

## API Reference

#### Upload Audio File

```http
  POST /audio/user/{userId}/phrase/{phraseId}
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `userId` | `string` | **Required**. ID string representing a user. |
| `phraseId` | `string` | **Required**. ID string representing a phrase. |
| `audio_file` | `file` | **Required**. Audio File, only accept m4a format. |

#### Retrieve Audio File

```http
  GET /audio/user/{userId}/phrase/{phraseId}/{format}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `userId` | `string` | **Required**. ID string representing a user. |
| `phraseId` | `string` | **Required**. ID string representing a phrase. |
| `format` | `string` | **Required**. Requested format, available: wav, m4a. |

#### Inject User & Phrase (TEST only)

```http
  POST /dataset
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `userId` | `string` | **Required**. ID string representing a user. |
| `phraseId` | `string` | **Required**. ID string representing a phrase. |

## Running Tests

To run tests, run the following command

```bash
  ./gradlew clean test jacocoTestReport
```
Coverage report can be seen in
```bash
  build/reports/jacoco/test/html/index.html
```