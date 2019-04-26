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

import org.springframework.web.bind.annotation.*;

import javax.naming.InvalidNameException;
import java.io.IOException;

/**
 * test controller class
 * 
 * @author Matthieu Ghilain
 */
@RequestMapping(path = "/other", method = RequestMethod.PUT)
public class OtherController {

    /**
     * other test method
     *
     * @param param
     *            dummy param
     * @return dummy value
     * @throws IOException some exception
     * @throws InvalidNameException when parameter smaller than 1
     */
    @GetMapping("test")
    public String test(String param) throws IOException, InvalidNameException {
        if (param.length() > 5) {
            throw new IOException("Just to test :)");
        }
        if (param.length() < 1) {
            throw new InvalidNameException();
        }
        return "dummy " + param;
    }

    /**
     * other without value or path
     * 
     * @param bar
     *            param
     * @return retval
     */
    @PostMapping
    public String bla(@RequestBody String bar) {
        return "foo" + bar;
    }

    /**
     * other without value or path
     * other line in delete mapping
     *
     */
    @DeleteMapping
    public void delete() {

    }
}
