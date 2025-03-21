package com.achiko.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sitemap.xml")
public class SitemapController {
    
    @GetMapping(produces = "application/xml")
    public String generateSitemap() {
        return """
        <?xml version="1.0" encoding="UTF-8"?>
        <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
            <url>
                <loc>https://achiko.site/</loc>
                <changefreq>daily</changefreq>
                <priority>1.0</priority>
            </url>
            <url>
                <loc>https://achiko.site/rooms</loc>
                <changefreq>daily</changefreq>
                <priority>0.8</priority>
            </url>
        </urlset>
        """;
    }
}
