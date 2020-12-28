import java.util.Base64;
import java.util.UUID;

public class AdvanciusTest {

    public static void main(String[] arguments) {
        for (int i = 0; i < 100; i++) {
            String apikey = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
            System.out.println(apikey);
        }
    }
}
