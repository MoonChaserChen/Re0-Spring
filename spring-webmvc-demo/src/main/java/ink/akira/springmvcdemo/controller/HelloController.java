package ink.akira.springmvcdemo.controller;

import ink.akira.springmvcdemo.pojo.JsonResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @RequestMapping(value="/hello")
    public JsonResult hello(String name) {
        return JsonResult.success("你好, " + name);
    }
}
