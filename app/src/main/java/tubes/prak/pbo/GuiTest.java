package tubes.prak.pbo;
import java.awt.GraphicsEnvironment;

import org.mindrot.jbcrypt.BCrypt;

public class GuiTest {
    public static void main(String[] args) {
        System.out.println("Headless: "
                + GraphicsEnvironment.isHeadless());
        System.out.println("DISPLAY: "
                + System.getenv("DISPLAY"));
        System.out.println("WAYLAND_DISPLAY: "
                + System.getenv("WAYLAND_DISPLAY"));
        System.out.println("Java: "
                + System.getProperty("java.home"));


        String has = BCrypt.hashpw("12345", BCrypt.gensalt(12));
        System.out.println(has);
    }
}
