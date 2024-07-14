import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.image.ImageObserver;
import javax.swing.ImageIcon;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Bird class is used to create a bird object in the game and to move it around the screen
class Bird extends GameObject {
    private ProxyImage proxyImage; // ProxyImage object used to load the image of the bird
    private Tube[] tube; // Array of Tube objects used to create the walls in the game
    private Game game; // Reference to the Game instance

    // Constructor
    public Bird(int x, int y, Game game) {
        super(x, y);
        this.game = game; // Assign the game instance to the bird
        if(proxyImage == null) {
            proxyImage = new ProxyImage("bird.png"); // Load the image of the bird
        }
        this.image = proxyImage.loadImage().getImage();
        this.width = image.getWidth(null); //Set the width and height of the bird
        this.height = image.getHeight(null);
        this.x -= width; // Adjust the x position of the bird
        this.y -= height; // Adjust the y position of the bird
        tube = new Tube[1]; // Create a new array of Tube objects
        tube[0] = new Tube(900, Window.HEIGHT - 60); // Create the first wall
        this.dy = 2; // Set the initial speed of the bird
    }

    // Method used to move the bird
    public void tick() {
        if(dy < 5) { // If the speed of the bird is less than 5
            dy += 2; // Increase the speed of the bird
        }
        this.y += dy; // Move the bird down the screen
        tube[0].tick(); // Move the wall down the screen
        checkWindowBorder(); // Check if the bird has hit the top or bottom of the screen
    }

    public void flyUp() {
        if(dy > 0) { // If the speed of the bird is greater than 0
            dy = 0; // Set the speed of the bird to 0
        }
        dy -= 15; // Move the bird up the screen
    }

    public void flyDown() {
        if(dy < 0) { // If the speed of the bird is less than 0
            dy = 0; // Set the speed of the bird to 0
        }
        dy += 15; // Move the bird down the screen
    }

    // Method used to check if the bird has hit the top or bottom of the screen
    private void checkWindowBorder() {
        if(this.x > Window.WIDTH) { // If the bird has moved off the right side of the screen
            this.x = Window.WIDTH; // Set the x position of the bird to the right side of the screen
        }
        if(this.x < 0) { // If the bird has moved off the left side of the screen
            this.x = 0; // Set the x position of the bird to the left side of the screen
        }
        if(this.y > Window.HEIGHT - 50) { // If the bird has moved off the bottom of the screen
            this.y = Window.HEIGHT - 50; // Set the y position of the bird to the bottom of the screen
            game.setGameOver(true); // End the game if the bird touches the ground
        }
        if(this.y < 0) { // If the bird has moved off the top of the screen
            this.y = 0; // Set the y position of the bird to the top of the screen
        }
    }

    // Method used to check if the bird has hit the wall
    public void render(Graphics2D g, ImageObserver obs) {
        g.drawImage(image, x, y, obs); // Draw the bird
        tube[0].render(g, obs); // Draw the wall
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}


// Tube class is used to create a wall object in the game and to move it around the screen
class TubeColumn {

    private int base = Window.HEIGHT - 60;

    private List<Tube> tubes;
    private Random random;
    private int points = 0; // Variable used to keep track of the score
    private int speed = 5; // Variable used to set the speed of the wall
    private int changeSpeed = speed; 

    public TubeColumn() { 
        tubes = new ArrayList<>();
        random = new Random();
        initTubes();
    }

    // Method used to create the wall
    private void initTubes() {

        int last = base;
        int randWay = random.nextInt(10);

        // Create the first wall in the game and set the position of the wall to the right side of the screen 
        for (int i = 0; i < 20; i++) {

            Tube tempTube = new Tube(900, last); // Create a new Tube object
            tempTube.setDx(speed); // Set the speed of the wall
            last = tempTube.getY() - tempTube.getHeight(); // Set the position of the wall
            if (i < randWay || i > randWay + 4) { // If the wall is not in the middle of the screen 
                tubes.add(tempTube); // Add the wall to the array of Tube objects
            }

        }

    }

    // Method used to check the position of the walls and to create new walls
    public void tick() { 

        for (int i = 0; i < tubes.size(); i++) { // Loop through the array of Tube objects 
            tubes.get(i).tick(); // Get the position of the wall

            if (tubes.get(i).getX() < 0) { // If the wall has moved off the left side of the screen
                tubes.remove(tubes.get(i)); // Remove the wall from the array of Tube objects
            }
        }
        if (tubes.isEmpty()) { // If the array of Tube objects is empty
            this.points += 1; // Increase the score by 1
            if (changeSpeed == points) {
                this.speed += 1; // Increase the speed of the wall by 1
                changeSpeed += 5;
            }
            initTubes(); // Create a new wall
        }

    }

    // Method used to draw the walls
    public void render(Graphics2D g, ImageObserver obs) {
        for (int i = 0; i < tubes.size(); i++) { // Loop through the array of Tube objects
            tubes.get(i).render(g, obs); // Draw the wall 
        }

    }


    public List<Tube> getTubes() {
        return tubes;
    }

    public void setTubes(List<Tube> tubes) {
        this.tubes = tubes;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

}

interface IStrategy {
    
    public void controller(Bird bird, KeyEvent kevent);
    public void controllerReleased(Bird bird, KeyEvent kevent);
}

// Controller class is used to control the movement of the bird
class Controller implements IStrategy {

    public void controller(Bird bird, KeyEvent kevent) {
    }

    public void controllerReleased(Bird bird, KeyEvent kevent) {
        if(kevent.getKeyCode() == KeyEvent.VK_UP) { // If the up arrow is pressed, bird flies up
            bird.flyUp();
        } else if (kevent.getKeyCode() == KeyEvent.VK_DOWN) { // If the down arrow is pressed, bird flies down
            bird.flyDown();
        }
    }
    
}

interface IImage {
    public ImageIcon loadImage();
}

// ProxyImage class is used to load the image of all the objects
class ProxyImage implements IImage {

    private final String src;
    private RealImage realImage;
    
    public ProxyImage(String src) {
        this.src = src;
    }
    
    public ImageIcon loadImage() {
        if(realImage == null) { // If the image has not been loaded 
            this.realImage = new RealImage(src); // Load the image
        }
        
        return this.realImage.loadImage(); 
    }
    
}

class RealImage implements IImage {

    private final String src;
    private ImageIcon imageIcon;
    
    public RealImage(String src) {
        this.src = src;
    }
    @Override
    public ImageIcon loadImage() {
        if(imageIcon == null) {
            this.imageIcon = new ImageIcon(getClass().getResource(src));
        }
        return imageIcon;
    }
    
}

// this class is used to create the window for the game
abstract class GameObject {
    protected int x, y;
    protected int dx, dy;
    protected int width, height;
    protected Image image;

    public GameObject(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public int getDx() {
        return dx;
    }
    public void setDx(int dx) {
        this.dx = dx;
    }
    public int getDy() {
        return dy;
    }
    public void setDy(int dy) {
        this.dy = dy;
    }
    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public Image getImage() {
        return image;
    }
    public void setImage(Image image) {
        this.image = image;
    }
    public abstract void tick();
    public abstract void render(Graphics2D g, ImageObserver obs);
}

// this class is used to create the window for the game
class Game extends JPanel implements ActionListener {

    private Timer gameLoopTimer; // Timer object used to create the game loop
    private Bird bird; // Bird object used to create the bird in the game
    private TubeColumn tubes; // TubeColumn object used to create the walls in the game
    private Controller controller;
    private boolean startScreen = true;
    private boolean gameOver = false;

    // Constructor
    public Game() {
        setFocusable(true); // Make the JPanel focusable
        setDoubleBuffered(true); // Make the JPanel double buffered
        setBackground(Color.BLACK); // Set the background color of the JPanel to black
        addKeyListener(new GameKeyAdapter()); // Add a KeyListener to the JPanel
        gameLoopTimer = new Timer(20, this); // Create a new Timer object
        initGame(); // Initialize the game
    }

    // Method used to initialize the game
    private void initGame() {
        bird = new Bird(250, 250, this); // Create a new Bird object and pass the Game instance
        tubes = new TubeColumn(); // Create a new TubeColumn object
        controller = new Controller(); // Create a new Controller object
        gameLoopTimer.start(); // Start the game loop
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2d = (Graphics2D) g;

        if (startScreen) {
            renderStartScreen(g2d);
        } else if (gameOver) {
            renderGameOverScreen(g2d);
        } else {
            renderGame(g2d);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    // Method used to draw the start screen
    private void renderStartScreen(Graphics2D g2d) {
        drawBackground(g2d);
        drawCenteredString(g2d, "Press UP ARROW to Start", new Rectangle(Window.WIDTH, Window.HEIGHT), new Font("Arial", Font.BOLD, 36));
    }

    // Method used to draw the game over screen
    private void renderGameOverScreen(Graphics2D g2d) {
        drawBackground(g2d);
        drawCenteredString(g2d, "Game Over", new Rectangle(Window.WIDTH, Window.HEIGHT / 2), new Font("Arial", Font.BOLD, 48));
        drawCenteredString(g2d, "Press UP ARROW to Restart", new Rectangle(Window.WIDTH, Window.HEIGHT), new Font("Arial", Font.BOLD, 36));
    }

    // Method used to draw the game screen
    private void renderGame(Graphics2D g2d) {
        drawBackground(g2d);
        bird.render(g2d, this); // Draw the bird
        tubes.render(g2d, this); // Draw the walls
        drawScore(g2d); // Draw the score
    }

    // Method used to draw the background
    private void drawBackground(Graphics2D g2d) {
        ImageIcon ii = new ImageIcon(getClass().getResource("background.png"));
        g2d.drawImage(ii.getImage(), 0, 0, null);
    }

    // Method used to draw the score
    private void drawScore(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 36));
        g2d.drawString("Score: " + tubes.getPoints(), 20, 40);
    }

    private void drawCenteredString(Graphics2D g2d, String text, Rectangle rect, Font font) {
        FontMetrics metrics = g2d.getFontMetrics(font);
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.setFont(font);
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, x, y);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!startScreen && !gameOver) {
            bird.tick(); // Move the bird
            tubes.tick(); // Move the walls
            checkCollision(); // Check for collisions
        }
        repaint(); // Repaint the screen
    }

    // Method used to check for collisions
    private void checkCollision() {
        for (Tube tube : tubes.getTubes()) {
            if (bird.getBounds().intersects(tube.getBounds())) { // If the bird has hit the wall
                gameOver = true; // End the game
            }
        }
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    private class GameKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            if (startScreen && e.getKeyCode() == KeyEvent.VK_UP) {
                startScreen = false;
            } else if (gameOver && e.getKeyCode() == KeyEvent.VK_UP) {
                gameOver = false;
                initGame();
            }
            controller.controller(bird, e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            controller.controllerReleased(bird, e);
        }
    }
}


// Tube class is used to create a wall object in the game and to move it around the screen
class Tube extends GameObject {

    private ProxyImage proxyImage; // ProxyImage object used to load the image of the wall

    public Tube(int x, int y) {
        super(x, y);
        if(proxyImage == null) {
            proxyImage = new ProxyImage("TubeBody.png"); // Load the image of the wall
        }
        this.image = proxyImage.loadImage().getImage(); 
        this.width = image.getWidth(null); // Set the width and height of the wall
        this.height = image.getHeight(null);
        this.x -= width; // Adjust the x position of the wall
    }

    // Method used to move the wall
    @Override
    public void tick() {
        this.x -= this.dx; // Move the wall to the left side of the screen
    }

    @Override
    public void render(Graphics2D g, ImageObserver obs) {
        g.drawImage(image, x, y, obs); // Draw the wall
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}

// this class is used to create the window for the game
class Window extends JFrame {
    public static final int WIDTH = 900;
    public static final int HEIGHT = 700;

    public Window() {
        setTitle("Flappy Bird");
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        init();
    }

    private void init() {
        add(new Game());
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Window window = new Window();
            window.setVisible(true);
        });
    }
}
