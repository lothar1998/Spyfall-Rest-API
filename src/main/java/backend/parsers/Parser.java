package backend.parsers;

/**
 * interface to parsers objects
 *
 * @param <T> type of parsed value
 * @author Piotr Kuglin
 */
public interface Parser<T> {

    /**
     * parse an object
     *
     * @param objectToParse object to parse
     * @return parsed object
     */
    T parse(T objectToParse);
}
