<p align="center">
  <img width="80" src="https://github.com/activeviam/ps-pivot-springboot/blob/5.8-jdk8/activeviam.svg" />
</p>
<h1 align="center">Pivot Spring Boot</h1>
<p align="center">A minimalist ActivePivot project built with Spring Boot for you to edit, customise and use as a base for you ActivePivot projects</p>

---

## 📋 Details
This project aims to be an example of how to run ActivePivot as a Spring Boot application. ActivePivot was already a Spring application, but with the power of Spring Boot we can simplify our dependency management, deployment model and many other goodies that come with Spring Boot.

This project is a starting point for your own projects and implementations. You should be able to take this, customise it and get a cube up and running in a few minutes.

## 📦 Installation
#### Requirements
- Java 11
- Maven 3
- ActivePivot jar files (commercial software)
- Running the application requires a license for the ActivePivot software.

Clone or download this repository and run `mvn clean install`.
This will generate a jar file, which can be run using standard java commands.

## 💻 Usage
The project contains, out of the box, an extremely simple datastore schema and small Trades.csv file. This file you can find in `src/main/resources/data`. NB: if running as a jar file then this file might not be found, you will need to explicitly point to it.

- Excel: you can connect to the cube from Excel, by connecting to an 'Analysis Services' source. The default URL to use when running locally is `http://localhost:9090/xmla`
- ActiveUI, ActiveViam's user interface for exploring the cube, will be available from `http://localhost:9090/ui`

The default security credentials are `admin:admin`, but can be modified in the `SecurityConfig` class (we use Spring Security). You should change this before going into production. You are also recommended to change the jwt key pair in `application.yaml`.

