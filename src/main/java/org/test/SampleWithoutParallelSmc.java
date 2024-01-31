package org.test;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;

import static java.util.Objects.requireNonNull;

@Configuration(proxyBeanMethods = false)
@EnableStateMachine(contextEvents = false, name = "noParallelSm")
class SampleWithoutParallelSmc extends StateMachineConfigurerAdapter<String, String> {

    @Override
    public void configure(StateMachineStateConfigurer<String, String> states) throws Exception {
        var c = states.withStates();
        c.initial("Ready");
        c.state("Tasks");
        c.choice("Choice");
        c.end("End1");
        c.end("End2");
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<String, String> transitions) throws Exception {
        transitions.withExternal().source("Ready").target("Tasks").event("run");
        transitions.withExternal().source("Tasks").target("Choice");
        transitions.withChoice().source("Choice")
                .first("End1", guard())
                .last("End2");
    }

    private Guard<String, String> guard() {
        return context -> requireNonNull(context.getMessageHeaders().get("guard", Boolean.class),
                "No value detected!");
    }
}
