package backend.endpoints;

import com.google.gson.Gson;
import org.springframework.security.jwt.JwtHelper;

import java.util.Map;

public class JWTParser {

    private static Gson gson=new Gson();

    public static Map getContent(String jwtToken){
        String decoded_string = JwtHelper.decode(jwtToken).getClaims();
        return gson.fromJson(decoded_string, Map.class);
    }
}
