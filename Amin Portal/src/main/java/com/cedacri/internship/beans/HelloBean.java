package com.cedacri.internship.beans;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component("helloBean")
@ViewScoped                     // jakarta.faces.view.ViewScoped
public class HelloBean implements Serializable {

    private String name;

    public void greet() {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Hello " + name));
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}