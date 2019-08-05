package backend.endpoints;

import com.google.gson.Gson;
import org.springframework.security.jwt.JwtHelper;

import java.util.Map;

/**
 * implementation of JWT parser to decode JWT token
 *
 * @author Piotr Kuglin
 */
public class JWTParser {

    private static Gson gson=new Gson();

    private JWTParser() {
    }

    /**
     * decode JWT Token
     * @param jwtToken token as String
     * @return decoded token as map
     */
    public static Map getContent(String jwtToken){
        String decodedString = JwtHelper.decode(jwtToken).getClaims();
        return gson.fromJson(decodedString, Map.class);
    }
}
