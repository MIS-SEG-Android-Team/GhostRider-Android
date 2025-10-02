package org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Model;


import java.util.Map;

public class Violation {
    private String name;
    private Map<String, String> actions;

    public Violation(String name, Map<String, String> actions) {
        this.name = name;
        this.actions = actions;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getActions() {
        return actions;
    }
}
