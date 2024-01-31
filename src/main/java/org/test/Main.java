package org.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import reactor.core.publisher.Mono;

import java.util.Map;

@SpringBootApplication(proxyBeanMethods = false)
public class Main implements CommandLineRunner {

    @Autowired
    private StateMachine<String, String> parallelSm;
    @Autowired
    private StateMachine<String, String> noParallelSm;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Guard works as expected
        noParallelSm.startReactively().subscribe();
        assert "Ready".equals(noParallelSm.getState().getId());
        var m1 = MessageBuilder.createMessage("run", new MessageHeaders(Map.of("guard", true)));
        noParallelSm.sendEvent(Mono.just(m1)).subscribe();
        assert "End1".equals(noParallelSm.getState().getId());

        // Throws NPE in guard
        parallelSm.startReactively().subscribe();
        assert "Ready".equals(parallelSm.getState().getId());
        var m2 = MessageBuilder.createMessage("run", new MessageHeaders(Map.of("guard", true)));
        parallelSm.sendEvent(Mono.just(m2)).subscribe();
        assert "End1".equals(parallelSm.getState().getId());
    }
}