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

import java.util.Map;

/**
 * Manage unicorns across the coolest place in the universe.
 */
@RestController
@RequestMapping(path = "/unicorns")
public class UnicornController {

    private Map<String, Unicorn> unicorns = Map.of("Poppy", new Unicorn("Poppy", "Poppy The Mighty"));

    /**
     * Retrieve unicorn by their name (try with Poppy)
     *
     * @param unicornName the unicorn name
     * @return the unicorn!
     */
    @GetMapping("/{unicornName}")
    public Unicorn findUnicorn(@PathVariable String unicornName) {
        return unicorns.get(unicornName);
    }

}
