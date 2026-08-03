package com.personalexpense.bdd;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/e2e/resources/features",
    glue = "com.personalexpense.bdd",
    plugin = {"pretty", "html:target/cucumber-reports.html"}
)
public class CucumberTestRunner {
}
