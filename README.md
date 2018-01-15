# springfox-javadoc
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
