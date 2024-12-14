
import engine.Window;

public class App {
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        Window window = Window.get();
        window.run();
    }
}
