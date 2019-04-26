# springfox-javadoc

[![CircleCI](https://circleci.com/gh/springfox/springfox-javadoc.svg?style=svg)](https://circleci.com/gh/springfox/springfox-javadoc) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/32f99b3650794b5eb1f7c155a57d5100)](https://app.codacy.com/app/dilip-krishnan-github/springfox-javadoc?utm_source=github.com&utm_medium=referral&utm_content=springfox/springfox-javadoc&utm_campaign=badger)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fspringfox%2Fspringfox-javadoc.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2Fspringfox%2Fspringfox-javadoc?ref=badge_shield)

# Overview
Ability to use Javadoc for documentation for generating OpenAPI specifications.

Using Spring Boot the necessary plugins are automatically bootstrapped, so no configuration is needed.

Otherwise you have to manually instantiate them (see the list in JavadocPluginConfiguration class).

# Compatibility

Only Java9+ is supported.

Spring MVC is supported but not SpringWebFlux.

# How does it work?

The Javadoc is extracted using a custom Doclet which is passed to the javadoc tool.
The Javadoc is stored in a file which must be on the classpath of the application.

At runtime, this file is read by a bunch of Springfox plugins to customize the Documentation model.

# Howe to use?

## Gradle Spring Boot example
[See the example project](./springfox-javadoc-gradle-example).

## Maven example
```xml
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-javadoc-plugin</artifactId>
                <version>2.10.4</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>javadoc</goal>
                        </goals>
                        <phase>process-classes</phase>
                        <configuration>
                            <doclet>springfox.javadoc.doclet.SwaggerPropertiesDoclet</doclet>
                            <docletArtifact>
                                <groupId>io.springfox</groupId>
                                <artifactId>springfox-javadoc</artifactId>
                                <version>${springfox-javadoc.version}</version>
                            </docletArtifact>
                            <additionalparam>
                                -classdir ${project.build.outputDirectory}
                            </additionalparam>
                            <sourcepath>${project.build.sourceDirectory}</sourcepath>
                            <subpackages>your.rest.service.package</subpackages>
                            <useStandardDocletOptions>false</useStandardDocletOptions>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
```

## TODO

- Prefix generated properties with common prefix (like io.springfox.javadoc)  

## License
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fspringfox%2Fspringfox-javadoc.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2Fspringfox%2Fspringfox-javadoc?ref=badge_large)
