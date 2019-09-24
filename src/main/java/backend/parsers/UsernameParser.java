
package backend.parsers;

import com.google.gson.Gson;

import java.util.Map;

/**
 * Parser Decorator
 * parser to user_name property from JSON string
 *
 * @author Piotr Kuglin
 */
public class UsernameParser extends ParserDecorator<String> {

    private static final String PROPERTY_NAME = "user_name";
    private static Gson gson = new Gson();

    /**
     * initialization of decorator
     *
     * @param parser parser which decode JWT token
     */
    public UsernameParser(Parser<String> parser) {
        super(parser);
    }

    /**
     * parse decoded JWT token and get user_name property
     *
     * @param objectToParse authorization header with authorization type separated by a space
     * @return user_name property
     */
    @Override
    public String parse(String objectToParse) {
        objectToParse = objectToParse.split(" ")[1];
        return (String) gson.fromJson(super.parse(objectToParse), Map.class).get(PROPERTY_NAME);
    }
}