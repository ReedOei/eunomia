package com.reedoei.eunomia.data.caching;

import com.google.common.base.Preconditions;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public abstract class FileCache<T> extends Cache<T> {
    public abstract @NonNull Path path();
    protected abstract T load();
    protected abstract void save();

    /**
     * Returns true if the path exists and is a file, or is a non-empty directory.
     */
    protected boolean hasResult() {
        if (!Files.isDirectory(path()) && Files.exists(path())) {
            return true;
        }

        try {
            return Files.isDirectory(path()) && Files.walk(path()).count() > 0;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public T get() {
        boolean loaded = false;

        if (t == null) {
            if (hasResult()) {
                loaded = true;
                t = load();
            } else {
                t = generate();
            }
            save();
        }

        if (loaded) {
            return Objects.requireNonNull(t, "t is null after loading value from " + path());
        } else {
            return Objects.requireNonNull(t, "t is null after value generation");
        }
    }
}
