package cn.itsuki.blog.instrumentation;

import graphql.ExecutionResult;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

/**
 * @author: itsuki
 * @create: 2022-05-10 14:40
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class RequestLoggingInstrumentation extends SimpleInstrumentation {
    private final Clock clock = Clock.systemDefaultZone();

    @NotNull
    @Override
    public InstrumentationContext<ExecutionResult> beginExecution(InstrumentationExecutionParameters parameters) {
        var start = Instant.now(clock);
        log.info("query: {} with variables: {}", parameters.getQuery(), parameters.getVariables());
        return SimpleInstrumentationContext.whenCompleted(((executionResult, throwable) -> {
            var duration = Duration.between(start, Instant.now(clock));
            if (throwable == null) {
                log.info("{}: completed successfully in: {}", parameters.getQuery(), duration);
            } else {
                log.warn("{}: failed in : {}", parameters.getQuery(), duration, throwable);
            }
        }));
    }
}
