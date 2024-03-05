%JAVA_HOME%\bin\java ^
--add-opens=java.base/java.util.concurrent=ALL-UNNAMED ^
--add-opens=java.base/java.nio=ALL-UNNAMED ^
-Dloader.path="lib" ^
-Dspring.application.name=atoti-palantir-demo ^
-jar pivot-spring-boot-6.0.11.jar > atoti-palantir-demo.log 2>&1