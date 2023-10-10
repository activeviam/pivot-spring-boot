<p align="center">
  <img width="80" src="./activeviam.svg" />
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

### Running the fat jar
The project contains, out of the box, an extremely simple datastore schema and small `trades.csv` file.<br>
This file you can find in `src/main/resources/data`.<br>
NB: if running as a jar file then this file might not be found as it is in the classpath, you will need to explicitly point to it.<br>
You will probably see this stack trace:

```
Caused by: java.nio.file.FileSystemNotFoundException: null
	at jdk.zipfs/jdk.nio.zipfs.ZipFileSystemProvider.getFileSystem(ZipFileSystemProvider.java:169)
	at jdk.zipfs/jdk.nio.zipfs.ZipFileSystemProvider.getPath(ZipFileSystemProvider.java:155)
	at java.base/java.nio.file.Path.of(Path.java:208)
	at java.base/java.nio.file.Paths.get(Paths.java:97)
	...
```
	
This is related to this Spring Boot known issue: `https://github.com/spring-projects/spring-boot/issues/7161`<br>
In order to fix that override the `-Dfile.trades` property and pass it to the jvm:

```
java -Dfile.trades=<absolute path of trades.csv> -jar <fat jar path>
```
### Running on macos
Add the following argument `-DchunkAllocatorClass=com.qfs.chunk.direct.allocator.impl.MmapDirectChunkAllocator` to your jvm, so then it becomes:

```
java -DchunkAllocatorClass=com.qfs.chunk.direct.allocator.impl.MmapDirectChunkAllocator -Dfile.trades=<absolute path of trades.csv> -jar <fat jar path>
```

### Connecting to the ActivePivot

- Excel: you can connect to the cube from Excel, by connecting to an 'Analysis Services' source. The default URL to use when running locally is `http://localhost:9090/xmla`

- ActiveUI, ActiveViam's user interface for exploring the cube, will be available from `http://localhost:9090/ui`

The default security credentials are `admin:admin`, but can be modified in the `SecurityConfig` class (we use Spring Security).<br>
You should change this before going into production.<br>
You are also recommended to change the jwt key pair in `application.yaml` by running the class `JwtUtil` and generating new key pair.

### resource-server
https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html#_specifying_the_authorization_server