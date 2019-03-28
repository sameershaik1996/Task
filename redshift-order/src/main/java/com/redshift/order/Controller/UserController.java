package com.redshift.order.Controller;


import com.netflix.discovery.converters.Auto;
import com.redshift.order.Data.UserRepository;
import com.redshift.order.Domain.Users;
import javassist.compiler.MemberResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;
    @RequestMapping(value="/signup",method= RequestMethod.POST)
    public ResponseEntity CreateUser(@RequestBody Users user){

        System.out.println(user.getUsername()+" "+user.getPassword());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("user created");

    }


}
