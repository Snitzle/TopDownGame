import java.awt.*;
import java.awt.image.BufferStrategy;

public class Game extends Canvas implements Runnable {

    public static int WIDTH = 800, HEIGHT = 600;
    public static String title = "ZOMBIE GAME";

    private Thread thread;
    private boolean isRunning = false;

    private Handler handler;

    private synchronized void start() {
        // Stops spinning up new threads if game is already running
        if ( isRunning ) return;

        thread = new Thread(this);
        thread.start();
        isRunning = true;

    }

    private synchronized void stop() {
        if ( !isRunning) return;

        thread = Thread.currentThread();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        isRunning = false;
    }

    public Game() {

        //Constructor
        new Window(WIDTH, HEIGHT, title, this);
        start();

        handler = new Handler();
        KeyInput input = new KeyInput();

        addKeyListener(input);
        // ID is just a reference to an ENUM? How is that an ID?
        // This can be turned into a factory to create instances of the enemies.
        handler.addObject(new Player(100,100, ID.Player, input));

    }

    // This is where the game loop runs
    public void run () {

        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();

        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        int updates = 0;
        int frames = 0;

        while( isRunning ){

            long now = System.nanoTime();

            delta += (now - lastTime) / ns;
            lastTime = now;

            while ( delta >= 1 ) {

                tick();
                updates++;
                delta--;

            }

            render();

            frames++;

            if ( System.currentTimeMillis() - timer > 1000 ) {

                timer += 1000;
                System.out.println("FPS: " + frames + " TICKS: " + updates);
                frames = 0;
                updates = 0;

            }
        }




    }

    private void tick () {
        // Updates the game logic
        handler.tick();
    }

    private void render() {

        // updates the screen
        BufferStrategy bs = this.getBufferStrategy();

        // Only gets ran once when it doesn't exist
        if ( bs == null ) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        // This is the bulk of the rendering
        g.setColor(Color.BLACK);
        g.fillRect(0,0, WIDTH, HEIGHT);

        handler.render(g);

        bs.show();
        g.dispose();

    }

    public static void main(String[] args) {
        new Game();
    }

}