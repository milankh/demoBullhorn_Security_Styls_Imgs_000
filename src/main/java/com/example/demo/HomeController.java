package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    UserRepository userRepository;


    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String listMessages(Model model){
        model.addAttribute("users", userRepository.findAll());
        return "basepage";
    }

    @GetMapping("/add")
    public String inputMessage(Model model){
        model.addAttribute("user", new User());
        return "messagecenter";
    }

   /** @PostMapping("/post")
    public String postMessage(@Valid User user, BindingResult result){
        if(result.hasErrors()){
            return "messagecenter";
        }
        userRepository.save(user);
        return "redirect:/";
    }
 **/

  @PostMapping("/post")
    public String processUser(@ModelAttribute User user,
                               @RequestParam("file")MultipartFile file){
        if(file.isEmpty()){
            return "redirect:/add";
        }

        try{
            Map uploadResult = cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype", "auto"));
            user.setHeadshot(uploadResult.get("url").toString());
            userRepository.save(user);
        } catch(IOException e){
            e.printStackTrace();
            return "redirect:/post";
        }
        return "redirect:/";
    }


    @RequestMapping("/messagedetails/{id}")
    public String showMessage(@PathVariable("id") long id, Model model){
        model.addAttribute("user", userRepository.findOne(id));
        return "messagedetail";
    }

    @RequestMapping("/updatemessage/{id}")
    public String updateMessage(@PathVariable("id") long id, Model model){
        model.addAttribute("user", userRepository.findOne(id));
        return "messagecenter";
    }

    @RequestMapping("/deletemessage/{id}")
    public String deleteUser(@PathVariable("id") long id){
        userRepository.delete(id);
        return "redirect:/";
    }


}