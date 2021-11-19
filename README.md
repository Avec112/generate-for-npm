# Generate for NPM

I need a simple compiled file with all the main NPM dependencies in a simple format for current vaadin version.

* Update pom with correct vaadin version
* Run `mvn clean package -Pproduction`
* Run `mvn exec:java`
  * Input: `pnpm-lock.yaml` 
  * Output: `dependencies.txt`


You might wonder why do this? I have a offline NPM proxy with scheduled updates. Those updates might sometime fail for some dependencies and I must update missing dependencies manually. I need to know what dependencies to update. Thats when `dependencies.txt` is useful.
