
1. Created Spring Boot app from the Spring Initalizr
    1. Split this pom.xml into two parts: 
        * Parent of the project: requires the Spring parent setup and modules section for the two modules, "backend" and "frontend"
            ```
            <modules>
              <module>frontend</module>
              <module>backend</module>
            </modules>
            ``` 
        * backend module: gets all the dependencies and plugins for Spring Boot
        * frontend module: a new pom.xml with just the basics, otherwise it's the Vue-created project
    
    