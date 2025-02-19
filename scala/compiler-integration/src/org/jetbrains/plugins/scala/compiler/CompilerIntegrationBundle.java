package org.jetbrains.plugins.scala.compiler;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public final class CompilerIntegrationBundle extends DynamicBundle {
    @NonNls
    private static final String BUNDLE = "messages.CompilerIntegrationBundle";

    private static final CompilerIntegrationBundle INSTANCE = new CompilerIntegrationBundle();

    private CompilerIntegrationBundle() {
        super(BUNDLE);
    }

    @Nls
    public static String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, @NotNull Object... params) {
        return INSTANCE.getMessage(key, params);
    }
}
