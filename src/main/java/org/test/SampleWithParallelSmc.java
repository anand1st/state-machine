package org.test;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;

import static java.util.Objects.requireNonNull;

@Configuration(proxyBeanMethods = false)
@EnableStateMachine(contextEvents = false, name = "parallelSm")
class SampleWithParallelSmc extends StateMachineConfigurerAdapter<String, String> {

    @Override
    public void configure(StateMachineStateConfigurer<String, String> states) throws Exception {
        var c = states.withStates();
        c.initial("Ready");
        c.fork("Fork");
        c.state("Tasks");
        c.and().withStates().parent("Tasks").initial("S1").end("E1");
        c.and().withStates().parent("Tasks").initial("S2").end("E2");
        c.join("Join");
        c.choice("Choice");
        c.end("End1");
        c.end("End2");
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<String, String> transitions) throws Exception {
        transitions.withExternal().source("Ready").target("Fork").event("run");
        transitions.withFork().source("Fork").target("Tasks");
        transitions.withExternal().source("S1").target("E1");
        transitions.withExternal().source("S2").target("E2");
        transitions.withJoin().source("Tasks").target("Join");
        transitions.withExternal().source("Join").target("Choice");
        transitions.withChoice().source("Choice")
                .first("End1", guard())
                .last("End2");
    }

    private Guard<String, String> guard() {
        return context -> requireNonNull(context.getMessageHeaders().get("guard", Boolean.class),
                "No value detected!");
    }
}
