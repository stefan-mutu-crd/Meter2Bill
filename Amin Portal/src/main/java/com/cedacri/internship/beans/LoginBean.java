package com.cedacri.internship.beans;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Map;

@Component("loginBean")
@RequestScope
public class LoginBean {

    public void checkMessages() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        Map<String, String> params = ctx.getExternalContext().getRequestParameterMap();

        if (params.containsKey("error")) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Login failed", "Invalid username or password."));
        } else if (params.containsKey("denied")) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Not authenticated", "Please log in to access this page."));
        } else if (params.containsKey("logout")) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Logged out", "You have been logged out."));
        }
    }

}
