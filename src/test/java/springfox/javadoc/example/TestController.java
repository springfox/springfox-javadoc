package springfox.javadoc.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * test controller class
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
}
