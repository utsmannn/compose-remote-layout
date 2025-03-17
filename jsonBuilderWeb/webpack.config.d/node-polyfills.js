config.resolve = {
    ...config.resolve,
    fallback: {
        "path": require.resolve("path-browserify")
    }
};