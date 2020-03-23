# Spyfall Rest API
![GitHub](https://img.shields.io/github/license/lothar1998/Project-X) ![GitHub](https://img.shields.io/github/languages/top/lothar1998/Project-X) ![GitHub](https://img.shields.io/github/v/tag/lothar1998/Project-X) ![GitHub](https://img.shields.io/github/languages/code-size/lothar1998/Project-X) ![GitHub](https://img.shields.io/github/last-commit/lothar1998/Project-X)

This is a Rest-API for Spyfall game, inspired by [Spyfall card board game](https://www.cryptozoic.com/spyfall).

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

Things you need to install before running:
```
Java 8
Gradle
Docker
```


## Running the tests

Run tests with *gradle* :
`gradle test`



## Deployment

To build a *jar* file:

`gradle clean build bootJar`

To deploy a whole system use docker-compose:

`docker-compose -f docker-compose.yml up`

## Built With

* [Spring](https://spring.io/) – Main framework used to build app
* [Gradle](https://gradle.org/) – Dependency Management
* [MongoDB](https://www.mongodb.com/) – Database
* [Docker](docker.com) – used to provide an easy way to deploy
* [JWT Token](https://jwt.io/) – used to authenticate users
* [OAuth2](https://oauth.net/2/) – custom authorization server

## Authors

* **Kamil Kaliś** – [kamkali](https://github.com/kamkali)
* **Piotr Kuglin** – [lothar1998](https://github.com/lothar1998)

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Docs

[Swagger docs](https://lothar1998.github.io/Spyfall-Rest-API/)
