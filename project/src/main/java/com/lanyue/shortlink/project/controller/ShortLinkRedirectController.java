package com.lanyue.shortlink.project.controller;

import com.lanyue.shortlink.project.service.ShortLinkService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ShortLinkRedirectController {

    private final ShortLinkService shortLinkService;

    @GetMapping("/{short-uri}")
    public void redirect(@PathVariable("short-uri") String shortUri, HttpServletResponse response) throws IOException {
        String originUrl = shortLinkService.restoreUrl(shortUri);
        response.sendRedirect(originUrl);
    }
}