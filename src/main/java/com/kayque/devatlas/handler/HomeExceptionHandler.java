package com.kayque.devatlas.handler;

import com.kayque.devatlas.controller.HomeController;
import com.kayque.devatlas.exception.GitHubApiUnavailableException;
import com.kayque.devatlas.exception.GitHubUserNotFoundException;
import com.kayque.devatlas.exception.InvalidGitHubUsernameException;
import com.kayque.devatlas.exception.AnalysisRateLimitExceededException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

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

    @ExceptionHandler(InvalidGitHubUsernameException.class)
    public String handleInvalidUsername(
            InvalidGitHubUsernameException exception,
            Model model
    ) {
        model.addAttribute(
                "username",
                exception.getUsername()
        );

        model.addAttribute(
                "errorTitle",
                "Nome de usuário inválido"
        );

        model.addAttribute(
                "errorMessage",
                "Digite um usuário do GitHub com até "
                        + "39 caracteres, usando apenas "
                        + "letras, números e hífens."
        );

        return "index";
    }

    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ExceptionHandler(AnalysisRateLimitExceededException.class)
    public String handleRateLimitExceeded(
            AnalysisRateLimitExceededException exception,
            Model model
    ) {
        model.addAttribute(
                "username",
                exception.getUsername()
        );

        model.addAttribute(
                "errorTitle",
                "Limite de análises atingido"
        );

        model.addAttribute(
                "errorMessage",
                "Você realizou muitas análises em pouco tempo. "
                        + "Aguarde alguns minutos e tente novamente."
        );

        return "index";
    }

}