package me.soyoungpark.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class WebClientRunner implements ApplicationRunner {

    @Autowired
    WebClient.Builder builder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        WebClient webClient = builder.build();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Mono<String> helloMono = webClient.get().uri("http://localhost:8765/hello")
                .retrieve()
                .bodyToMono(String.class);

        helloMono.subscribe(string -> {
            System.out.println(string);

            if (stopWatch.isRunning()) {
                stopWatch.stop();

                System.out.println(stopWatch.prettyPrint());
                stopWatch.start();
            }
        });
        // 이코드를 실행하는 순간, 요청을 보낸 뒤 아무 일도 발생하지 않고 다음 line으로 넘어간다.
        // 결과가 오면 subscribe가 호출된다!

        Mono<String> worldMono = webClient.get().uri("http://localhost:8765/world")
                .retrieve()
                .bodyToMono(String.class);

        worldMono.subscribe(string -> {
            System.out.println(string);

            if (stopWatch.isRunning()) {
                stopWatch.stop();

                System.out.println(stopWatch.prettyPrint());
                stopWatch.start();
            }
        });
    }
}
