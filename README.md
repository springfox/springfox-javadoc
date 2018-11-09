# springfox-javadoc

[![CircleCI](https://circleci.com/gh/springfox/springfox-javadoc.svg?style=svg)](https://circleci.com/gh/springfox/springfox-javadoc) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/32f99b3650794b5eb1f7c155a57d5100)](https://app.codacy.com/app/dilip-krishnan-github/springfox-javadoc?utm_source=github.com&utm_medium=referral&utm_content=springfox/springfox-javadoc&utm_campaign=badger)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fspringfox%2Fspringfox-javadoc.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2Fspringfox%2Fspringfox-javadoc?ref=badge_shield)

Ability to use Javadoc for documentation for generating OpenAPI specifications

To use this, make sure that `JavadocPluginConfiguration` is found by your spring context and add the execution of the javadoc doclet to your build process.

Maven example:
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


## License
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fspringfox%2Fspringfox-javadoc.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2Fspringfox%2Fspringfox-javadoc?ref=badge_large)