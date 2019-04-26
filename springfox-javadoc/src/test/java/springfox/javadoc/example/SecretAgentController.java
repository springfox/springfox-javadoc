/*
 *
 *  Copyright 2018-2019 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package springfox.javadoc.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Service to retrieve secret agents (could replace M in the future).
 * 
 * @author Matthieu Ghilain
 */
@RestController
@RequestMapping(path = "/agent", method = RequestMethod.PUT)
public class SecretAgentController {


    /**
     * Retrieve James Bond...
     * @return James Bond of course :)
     */
    @GetMapping("jamesBond")
    public SecretAgent getJamesBond() {
        SecretAgent secretAgent = new SecretAgent();
        secretAgent.setSecretAgentName("James Bond");
        return secretAgent;
    }

}
