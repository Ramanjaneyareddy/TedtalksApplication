
# TedTalks API

A Spring Boot application providing RESTful services to manage and analyze TedTalks data.  
It supports CRUD operations, CSV import, and simple speaker influence analysis.

---

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Speaker Analysis](#speaker-analysis)
- [CSV Import](#csv-import)
- [Testing](#testing)

---

## Features

- Add, retrieve, update, and delete TedTalks
- Retrieve TedTalks by title or by year
- Compute influence scores for talks based on views and likes
- Retrieve top N influential talks
- Import TedTalk data from a CSV file

---

## Technology Stack

- Java 11+
- Spring Boot 3.x
- Maven
- JUnit 5 for unit testing
- Apache Commons CSV
- Apache Commons Lang

---

## Getting Started

1. **Clone the repository**

```bash
git clone <repository-url>
cd tedtalks-api
Build the project

bash
Copy code
mvn clean install
Run the application

bash
Copy code
mvn spring-boot:run
The API will start on http://localhost:8080/tedTalks.

API Endpoints
1. Get all TedTalks

GET /tedTalks
Response:

json:

[
   {
        "title": "The tragic myth of the Sun God's son",
        "author": "Iseult Gillespie",
        "date": "Jan-22",
        "views": 998000,
        "likes": 29000,
        "link": "https://ted.com/talks/iseult_gillespie_the_tragic_myth_of_the_sun_god_s_son"
    },
    {
        "title": "The unexpected, underwater plant fighting climate change",
        "author": "Carlos M. Duarte",
        "date": "Aug-21",
        "views": 786000,
        "likes": 23000,
        "link": "https://ted.com/talks/carlos_m_duarte_the_unexpected_underwater_plant_fighting_climate_change"
    }
]
2. Get TedTalk by title
GET /tedTalks/getByTitle?title={title}
Response:
Returns the TedTalk object or null if not found.

3. Add a new TedTalk
POST /tedTalks
Request Body:

json
{
  "title": "Talk1",
  "author": "Author1",
  "date": "2020-01-01",
  "views": 1000,
  "likes": 50,
  "link": "http://link1"
}
Response:
Returns the added TedTalk object.

4. Update a TedTalk
PUT /tedTalks/{title}
Request Body:

json
{
  "author": "NewAuthor",
  "date": "2021-01-01",
  "views": 2000,
  "likes": 100,
  "link": "http://newlink"
}
Response:

"Updated" if successful

"Not found" if no talk matches the title

5. Delete a TedTalk
DELETE /tedTalks/{title}
Response:

"Deleted" if successful

"Not found" if no talk matches the title

6. Get top N influential talks
GET /tedTalks/analysis?topNumber={N}
Response:
A list of maps showing top N talks based on influence score:

json

[
  {
    "Top-1 Ted Talk is: ": "Talk1",
    "Top-2 Ted Talk is: ": "Talk2"
  }
]
7. Get top talk for a year

GET /tedTalks/tedTalkPerYear?year={year}
Response:
Returns the top TedTalk for the given year.

Speaker Analysis
Influence score is calculated as:

ini
Copy code
score = likes + (views / 1000.0)
The SpeakerAnalysisService computes scores and sorts them in descending order to identify top influential talks.

CSV Import
You can import TedTalks from a CSV file using TedTalkService.importFromCsv(String csvPath).

CSV Columns:
title, author, date, views, likes, link

Example:

csv
title,author,date,views,likes,link
Talk1,Author1,2020-01-01,1000,50,http://link1
Talk2,Author2,2021-02-01,2000,30,http://link2
Returns the number of successfully imported records.

Testing
Unit tests are provided for both services:

TedTalkServiceTest – tests CRUD operations, find by title/year, CSV import

SpeakerAnalysisServiceTest – tests influence score calculation and top N selection

Run tests with:

bash
mvn test
Notes
Thread safety: TedTalkService uses CopyOnWriteArrayList to allow concurrent reads and writes.

Influence analysis is a simple metric; it can be extended with more sophisticated algorithms.

All endpoints return JSON responses.


