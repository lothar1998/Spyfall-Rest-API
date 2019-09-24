package backend.parsers;

/**
 * basic schema of Parser.class decorator
 *
 * @param <T>
 * @author Piotr Kuglin
 */
public abstract class ParserDecorator<T> implements Parser<T> {

    /**
     * parser to wrap
     */
    protected Parser<T> parser;

    /**
     * parser wrapping constructor
     *
     * @param parser parser to wrap
     */
    public ParserDecorator(Parser<T> parser) {
        this.parser = parser;
    }

    /**
     * default parse method
     *
     * @param objectToParse object to parse
     * @return parsed object
     */
    @Override
    public T parse(T objectToParse) {
        return parser.parse(objectToParse);
    }
}
