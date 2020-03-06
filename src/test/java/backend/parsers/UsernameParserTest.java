package backend.parsers;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsernameParserTest {

    private Parser<String> parser = new UsernameParser(new JwtDecoder());

    @Test
    public void should_parse_header_to_user_name_property_value() {
        String exampleToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NjUwMjM3MjEsInVzZXJfbmFtZSI6ImphbmtvMTIzIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImp0aSI6Ijc3YmQwYzJkLTViNGQtNGU0YS1hNmVjLTEyMjk4OWU5YTUwZCIsImNsaWVudF9pZCI6ImNsaWVudF9pZCIsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSJdfQ.C31mSjrCsinO-bKi_Ww6GoCSnbPmYyasTolkGp5Td-o";
        String parsedValue = parser.parse("Bearer " + exampleToken);
        String expectedValue = "janko123";
        assertEquals(expectedValue, parsedValue);
    }
}