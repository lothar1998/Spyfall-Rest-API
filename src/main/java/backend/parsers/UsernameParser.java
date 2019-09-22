
package backend.parsers;

/**
 * username parser from http request header token
 *
 * @author Piotr Kuglin
 */
public class UsernameParser {

    public static String getUsername(String header) {
        String token = header.split(" ")[1];
        return JWTParser.getContent(token).get("user_name").toString();
    }

    private UsernameParser() {
    }
}