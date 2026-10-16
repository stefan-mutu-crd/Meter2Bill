package com.cedacri.internship.beans;

import jakarta.faces.view.ViewScoped;
import org.springframework.stereotype.Component;

@Component("homeBean")
@ViewScoped
public class HomeBean {

    public String getMessage(){
        return "Home page";
    }

}
