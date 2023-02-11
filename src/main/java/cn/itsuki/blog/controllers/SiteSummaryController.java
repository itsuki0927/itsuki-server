package cn.itsuki.blog.controllers;

import cn.itsuki.blog.entities.responses.SiteSummaryResponse;
import cn.itsuki.blog.services.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class SiteSummaryController {
    @Autowired
    private SummaryService summaryService;

    @QueryMapping
    public SiteSummaryResponse summary() {
        return this.summaryService.summary();
    }
}
