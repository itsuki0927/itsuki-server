package cn.itsuki.blog.configs;

import cn.itsuki.blog.entities.FileUpload;
import graphql.language.StringValue;
import graphql.schema.*;
import jakarta.servlet.http.Part;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@Configuration
public class GraphQLConfig {
    @Bean
    public GraphQLScalarType dateScalar() {
        return GraphQLScalarType.newScalar()
                .name("LocalDateTime")
                .description("Java 8 LocalDateTime as scalar.")
                .coercing(new Coercing<LocalDateTime, String>() {
                    @Override
                    public String serialize(@NotNull final Object dataFetcherResult) {
                        if (dataFetcherResult instanceof LocalDateTime) {
                            return ((LocalDateTime) dataFetcherResult).toString();
                        } else {
                            throw new CoercingSerializeException("Expected a LocalDate object.");
                        }
                    }

                    @NotNull
                    @Override
                    public LocalDateTime parseValue(@NotNull final Object input) {
                        try {
                            if (input instanceof String) {
                                return LocalDateTime.parse((String) input);
                            } else {
                                throw new CoercingParseValueException("Expected a String");
                            }
                        } catch (DateTimeParseException e) {
                            throw new CoercingParseValueException(String.format("Not a valid date: '%s'.", input), e
                            );
                        }
                    }

                    @NotNull
                    @Override
                    public LocalDateTime parseLiteral(@NotNull final Object input) {
                        if (input instanceof StringValue) {
                            try {
                                return LocalDateTime.parse(((StringValue) input).getValue());
                            } catch (DateTimeParseException e) {
                                throw new CoercingParseLiteralException(e);
                            }
                        } else {
                            throw new CoercingParseLiteralException("Expected a StringValue.");
                        }
                    }
                }).build();
    }

    @Bean
    public GraphQLScalarType fileScalar() {
        return GraphQLScalarType.newScalar()
                .name("FileUpload")
                .description("A file part in a multipart request")
                .coercing(new Coercing<FileUpload, Void>() {
                    @Override
                    public Void serialize(@NotNull Object dataFetcherResult) {
                        throw new CoercingSerializeException("Upload is an input-only type");
                    }

                    @NotNull
                    @Override
                    public FileUpload parseValue(@NotNull Object input) {
                        if (input instanceof Part) {
                            Part part = (Part) input;
                            try {
                                String name = part.getSubmittedFileName();
                                String contentType = part.getContentType();
                                byte[] content = part.getInputStream().readAllBytes();
                                part.delete();
                                return new FileUpload(name, contentType, content);
                            } catch (IOException e) {
                                throw new CoercingParseValueException("Couldn't read content of the uploaded file");
                            }
                        } else {
                            throw new CoercingParseValueException(
                                    "Expected type " + Part.class.getName() + " but was " + input.getClass().getName());
                        }
                    }

                    @NotNull
                    @Override
                    public FileUpload parseLiteral(@NotNull Object input) {
                        throw new CoercingParseLiteralException(
                                "Must use variables to specify Upload values");
                    }
                }).build();
    }

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(dateScalar())
                .scalar(fileScalar());
    }
}
