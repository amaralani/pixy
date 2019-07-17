[![Build Status](https://travis-ci.org/amaralani/pixy.svg?branch=master)](https://travis-ci.org/amaralani/pixy)
[![codecov](https://codecov.io/gh/amaralani/pixy/branch/master/graph/badge.svg)](https://codecov.io/gh/amaralani/pixy)

# Pixy
A Simple Wire Protocol Analyzer

### What is pixy?
Pixy is a simple Spring boot application that exposes 3 REST endpoints:


- POST /session: It receives the client and server file content in a JSON encoded request body. Pixy will parse the request, extract the client and server streams and will analyze the content for the number of times each instruction has occurred in each stream. Data will be persisted in a JPA compliant RDBMS for later querying. You are free to choose any database that you are comfortable with. each session should be identified with a generated UUID.

- GET /sessions: This will return a list of sessions that are stored in the datastore. entity identifier will be the same UUID that is generated previously.

- GET /session/{id}: This will generate a simple JSON structure that contains the exact number of occurrences for each instruction.

### Features:

Uses Spring Data for JPA interactions.

All HTTP endpoints are protected with HTTP Basic authentication.

Use Liquibase for initial schema generation.

Unit and integration tests are present for all non-framework parts of the code.

### Next Steps

- Add docker-compose support
- More advanced configuration of Liquibase
- Better implementation of custom authentication provider
- Secured web page that uses current (or more detailed) services to visualise data
- Add travis CI (or a substitute)
