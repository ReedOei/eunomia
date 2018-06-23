# Eunomia

Eunomia is a collection of Java classes that I've found useful in the past and are likely to be reusable.
Note: this package is very opinionated. May or may not suit you.

# Features

- Many functional style methods. Includes curried versions of your favorite methods (e.g., `map`, `filter`).
- Easy string searching 
    - Find the line **after** the matching line of text":
```
new StringSearch(str).searchFirst(Searcher.contains("Test result:"))    
```