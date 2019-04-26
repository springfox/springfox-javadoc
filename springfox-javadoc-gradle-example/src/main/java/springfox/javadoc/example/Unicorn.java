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

/**
 * A beautiful unicorn
 */
@SuppressWarnings("unused")
public class Unicorn {
    /**
     * Unicorn name.
     */
    private String name;
    /**
     * Unicorn magic name (use with care!)
     */
    private String magicName;
    /**
     * A magical unicorn child, they can only have one, that's why they have disappeared
     */
    private UnicornChild unicornChild;

    Unicorn(String name, String magicName, UnicornChild unicornChild) {
        this.name = name;
        this.magicName = magicName;
        this.unicornChild = unicornChild;
    }

    public Unicorn() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMagicName() {
        return magicName;
    }

    public void setMagicName(String magicName) {
        this.magicName = magicName;
    }

    public UnicornChild getUnicornChild() {
        return unicornChild;
    }

    public void setUnicornChild(UnicornChild unicornChild) {
        this.unicornChild = unicornChild;
    }
}
