package bsu.rpact.medionefrontend.webparser;

public interface WebParser<T> {
    T parse(String searchQuery);
}
