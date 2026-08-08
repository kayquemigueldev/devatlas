package com.kayque.devatlas.handler;

import com.kayque.devatlas.controller.HomeController;
import com.kayque.devatlas.exception.GitHubApiUnavailableException;
import com.kayque.devatlas.exception.GitHubUserNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = HomeController.class)
public class HomeExceptionHandler {

    @ExceptionHandler(GitHubUserNotFoundException.class)
    public String handleUserNotFound(
            GitHubUserNotFoundException exception,
            Model model
    ) {
        model.addAttribute(
                "username",
                exception.getUsername()
        );

        model.addAttribute(
                "errorTitle",
                "Perfil não encontrado"
        );

        model.addAttribute(
                "errorMessage",
                "Não encontramos o usuário @"
                        + exception.getUsername()
                        + " no GitHub."
        );

        return "index";
    }

    @ExceptionHandler(GitHubApiUnavailableException.class)
    public String handleApiUnavailable(
            GitHubApiUnavailableException exception,
            Model model
    ) {
        model.addAttribute(
                "username",
                exception.getUsername()
        );

        model.addAttribute(
                "errorTitle",
                "GitHub indisponível"
        );

        model.addAttribute(
                "errorMessage",
                "Não foi possível analisar o perfil agora. "
                        + "Tente novamente em alguns instantes."
        );

        return "index";
    }
}