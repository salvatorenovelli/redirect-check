package com.github.salvatorenovelli.redirectcheck.io;

import com.github.salvatorenovelli.redirectcheck.model.RedirectSpecification;

import java.io.IOException;
import java.util.function.Consumer;

public interface RedirectSpecificationParser {
    void parse(Consumer<RedirectSpecification> consumer) throws IOException;

    int getNumSpecs();
}
