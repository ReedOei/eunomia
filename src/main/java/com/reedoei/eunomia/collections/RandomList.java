package com.reedoei.eunomia.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// TODO: Current does nothing. Add random sampling, shuffling, selecting an item, etc.
public class RandomList<T> extends ArrayList<T> {
    public RandomList() {
        super();
    }

    public RandomList(final Collection<T> ts) {
        super(ts);
    }

    public T sample() {
        return get(new Random().nextInt(size()));
    }

    public T sampleNoReplacement() {
        return remove(new Random().nextInt(size()));
    }

    public RandomList<T> shuffleThis() {
        Collections.shuffle(this);

        return this;
    }

    public RandomList<T> shuffled() {
        final RandomList<T> ts = new RandomList<>(this);
        Collections.shuffle(ts);
        return ts;
    }

    public List<T> sample(int n) {
        return new RandomList<>(this).sampleNoReplacement(n);
    }

    public List<T> sampleNoReplacement(int n) {
        final List<T> result = Collections.synchronizedList(new ArrayList<>());

        final Random random = new Random();

        for (int i = 0; i < n; i++) {
            result.add(remove(random.nextInt(size())));
        }

        return result;
    }
}
