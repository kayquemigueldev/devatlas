FROM eclipse-temurin:21-jdk AS build

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

COPY src/ src/

RUN ./mvnw clean package -DskipTests -B
RUN cp target/*.jar application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted


FROM eclipse-temurin:21-jre AS runtime

WORKDIR /application

RUN groupadd --system devatlas
RUN useradd --system --gid devatlas devatlas
RUN mkdir -p /application/data
RUN chown -R devatlas:devatlas /application

COPY --from=build --chown=devatlas:devatlas /workspace/extracted/dependencies/ ./
COPY --from=build --chown=devatlas:devatlas /workspace/extracted/spring-boot-loader/ ./
COPY --from=build --chown=devatlas:devatlas /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build --chown=devatlas:devatlas /workspace/extracted/application/ ./

USER devatlas

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "application.jar"]
