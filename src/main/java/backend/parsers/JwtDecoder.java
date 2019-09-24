package backend.parsers;

import org.springframework.security.jwt.JwtHelper;

/**
 * implementation of JWT parser to decode JWT token
 *
 * @author Piotr Kuglin
 */
public class JwtDecoder implements Parser<String> {

    /**
     * decode JWT Token
     *
     * @param objectToParse token as String
     * @return decoded token
     */
    @Override
    public String parse(String objectToParse) {
        return JwtHelper.decode(objectToParse).getClaims();
    }
}
