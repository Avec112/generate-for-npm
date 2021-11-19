# Generate for npm

We need a simple compiled file with all the main NPM dependencies in a simple format for current vaadin version.

* Update pom with correct vaadin version
* `mvn clean package -Pproduction`
* Run class `GenerateForNpm`
* File is named xxx
