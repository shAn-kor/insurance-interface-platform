package com.insurance.interfaceplatform.interfaces.web;

import com.insurance.interfaceplatform.application.facade.DashboardQueryFacade;
import com.insurance.interfaceplatform.application.facade.DashboardQueryFacade.DashboardSummary;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomePageController {

    private final DashboardQueryFacade dashboardQueryFacade;

    public HomePageController(final DashboardQueryFacade dashboardQueryFacade) {
        this.dashboardQueryFacade = dashboardQueryFacade;
    }

    @GetMapping("/")
    public String index(final Model model) {
        final DashboardSummary summary = dashboardQueryFacade.getSummary();
        model.addAttribute("interfaceCount", summary.interfaceCount());
        model.addAttribute("executionCount", summary.executionCount());
        return "index";
    }
}
