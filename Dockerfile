FROM selenium/hub:3.14 AS base

FROM gradle:5.4.1-jdk8 AS gradle

RUN apt update && apt install dos2unix
COPY . .
RUN dos2unix gradlew
RUN ./gradlew clean jar

FROM base AS selenium

RUN sudo sed -i 's/\-jar \/opt\/selenium\/selenium-server-standalone\.jar'\
'/ -cp \/opt\/selenium\/selenium-server-standalone\.jar:\/opt\/selenium\/selenium-api.jar org.openqa.grid.selenium.GridLauncherV3 -servlets com.xing.qa.selenium.grid.hub.Console/'\
    /opt/bin/start-selenium-hub.sh
COPY --from=gradle /home/gradle/build/libs/*.jar /opt/selenium/selenium-api.jar