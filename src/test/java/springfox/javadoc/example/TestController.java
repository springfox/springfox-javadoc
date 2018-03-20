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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * test controller class
 * 
 * @author MartinNeumannBeTSE
 */
@RequestMapping(path = "/test", method = RequestMethod.PUT)
public class TestController {

    /**
     * test method
     *
     * @param param
     *            dummy param
     * @return dummy value
     */
    @GetMapping("test")
    public String test(String param) {
        return "dummy " + param;
    }

    /**
     * without value or path
     * 
     * @param bar
     *            param
     * @return retval
     */
    @PostMapping
    public String bla(@RequestBody String bar) {
        return "foo" + bar;
    }
}
