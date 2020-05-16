package com.example.super_movie.controller;


import com.example.super_movie.entity.Person;
import com.example.super_movie.service.IPersonService;
import com.example.super_movie.vo.PersonMovie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author earun
 * @since 2020-01-13
 */
@Controller
public class PersonController {
    @Autowired
    private IPersonService personService;
    @RequestMapping("/person")
    public String toPerson(HttpServletRequest request, Model model, Integer personId){
        Person person=personService.getPersonById(personId);
        List<PersonMovie> personMovieList=personService.getMovieByPerson(personId);
        model.addAttribute("person",person);
        model.addAttribute("personMovieList",personMovieList);
        model.addAttribute("jobList",personService.getJobByPersonId(personId));
        model.addAttribute("loginId",request.getSession().getAttribute("userId"));
        return "person";
    }

}
