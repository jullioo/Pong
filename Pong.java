import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*; 
import java.io.*; 
import javax.imageio.*; 
import java.util.ArrayList; 
import java.awt.MouseInfo;

public class Pong extends JFrame implements ActionListener{
	Timer myTimer;   
	Timer wallTimer;
	GamePanel game;
		
    public Pong() {
		super("PONG REWIRED");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1300,750);

		myTimer = new Timer(10, this);	 // trigger every 10 ms
		
		wallTimer =new Timer (10000, this);
		
		game = new GamePanel(this);
		add(game);

		setResizable(false);
		setVisible(true);
    }
	
	public void start(){
		myTimer.start();
		wallTimer.start();
	}

	public void actionPerformed(ActionEvent evt){
		if(game != null){
     		game.update();
      		game.repaint();
    	}
	}

    public static void main(String[] arguments) {
		Pong frame = new Pong();		
    }
}

class GamePanel extends JPanel implements KeyListener, MouseListener{
	
	private int p1PaddleX,p1PaddleY,p1Paddle2X,p1Paddle2Y;
	private int p2PaddleX,p2PaddleY,p2Paddle2X,p2Paddle2Y;
	private int ballx,bally,ballvx,ballvy;
	private int p1Score,p2Score;
	private int screen;
	//private int ability;
	public static final int MENU=1, GAME=2, INSTRUCTIONS=3, PICK1=4, PICK2=5;
	//public static final int DASH=1, POWER=2;
	private Point mouse;
	private boolean p1Start,p2Start;
	private boolean ready=false;
	private boolean []keys;
	private Image back, buttonPlayUp, buttonInstUp, buttonDashUp, buttonPowerUp, buttonNext1, buttonNext2, buttonBack, buttonDashDown, buttonPowerDown;
	private Rectangle menuInst, menuPlay, pDash, pPower, next1, next2, bNext1, bNext2, bBack;
	private Pong mainFrame;
	
	Font fontLocal=null, fontSys=null;
	
	public GamePanel(Pong m){
		keys = new boolean[KeyEvent.KEY_LAST+1];
	
		buttonPlayUp = new ImageIcon("buttons/PlayButton.png").getImage();
    	buttonInstUp = new ImageIcon("buttons/InstButton.png").getImage();
    	buttonDashUp = new ImageIcon("buttons/Dash.png").getImage();
    	buttonPowerUp = new ImageIcon("buttons/Power.png").getImage();
    	buttonDashDown = new ImageIcon("buttons/DashSelect.png").getImage();
    	buttonPowerDown = new ImageIcon("buttons/PowerSelect.png").getImage();
    	buttonNext1 = new ImageIcon("buttons/Next.png").getImage();
    	buttonNext2 = new ImageIcon("buttons/Next2.png").getImage();
    	buttonBack = new ImageIcon("buttons/Back.png").getImage();
    	
    	menuPlay = new Rectangle(550,250,200,50);
    	menuInst= new Rectangle(550,450,200,50);
    	pDash = new Rectangle(100,150,200,200);
    	pPower = new Rectangle(400,150,200,200);
    	bNext1 = new Rectangle(250,450,350,150);
    	bNext2 = new Rectangle(650,450,350,150);
    	bBack = new Rectangle(0,0,100,100);
    	
		back = new ImageIcon("pongBackground.png").getImage();
		back = back.getScaledInstance(1300,725,Image.SCALE_SMOOTH);//resize background
	 	// back = Toolkit.getDefaultToolkit().getImage("yourFile.gif");
		
		mainFrame = m;
		
		screen = MENU;
		
		System.out.println(screen);
	    
	    p1PaddleX = 10;
        p1PaddleY = 150;
        
        p1Paddle2X=65;
        p1Paddle2Y=535;
        
        p2PaddleX = 1260;
        p2PaddleY = 150;
        
        p2Paddle2X=1210;
        p2Paddle2Y=535;
        
        ballvx=0;
        ballvy=0;
        
        p1Start=true;
        p2Start=false;
        
        p1Score=0;
        p2Score=0;	
        
        String fName = "ARCADECLASSIC.ttf";
    	InputStream is = GamePanel.class.getResourceAsStream(fName);
    	try{
    		fontLocal = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(72f);
    	}
    	catch(IOException ex){
    		System.out.println(ex);	
    	}
    	catch(FontFormatException ex){
    		System.out.println(ex);	
    	}
        
		
		setPreferredSize( new Dimension(1300, 750));
        addKeyListener(this);
        addMouseListener(this);
        
	}
	
    public void addNotify() {
        super.addNotify();
        requestFocus();
        mainFrame.start();
    }
    
    public void update(){
    if(screen == GAME){
      	paddleMove();
      	paddleMove();
		paddleBoundaries();
		p2PaddleCollide();
		p1PaddleCollide();
		
		ballMove();
		ballBoundaries();
		
		outOfBounds();
		spikes();
    }
    mouse = MouseInfo.getPointerInfo().getLocation();
    Point offset = getLocationOnScreen();
    mouse.translate(-offset.x, -offset.y);
  }
    
    
	public void paddleMove(){
		if(keys[KeyEvent.VK_W] ){
			p1PaddleY -= 5;
			p1Paddle2Y += 5;
		}
		if(keys[KeyEvent.VK_S] ){
			p1PaddleY += 5;
			p1Paddle2Y -= 5;
		}
		if(keys[KeyEvent.VK_O] ){
			p2PaddleY -= 5;
			p2Paddle2Y += 5;
		}
		if(keys[KeyEvent.VK_K] ){
			p2PaddleY += 5;
			p2Paddle2Y -= 5;
		}
		
		Point mouse = MouseInfo.getPointerInfo().getLocation();
		Point offset = getLocationOnScreen();
		//System.out.println("("+(mouse.x-offset.x)+", "+(mouse.y-offset.y)+")");
		
	}
	
	public void paddleBoundaries(){
		if(p1PaddleY<20){
			p1PaddleY=20;
		}
		if(p1PaddleY>625){
			p1PaddleY=625;
		}
		
		if(p1Paddle2Y<20){
			p1Paddle2Y=20;
		}
		if(p1Paddle2Y>625){
			p1Paddle2Y=625;
		}
		
		if(p2PaddleY<20){
			p2PaddleY=20;
		}
		if(p2PaddleY>625){
			p2PaddleY=625;
		}
		
		if(p2Paddle2Y<20){
			p2Paddle2Y=20;
		}
		if(p2Paddle2Y>625){
			p2Paddle2Y=625;
		}
	}
	
	public void p2PaddleCollide(){//if ball hits the paddle
		Rectangle p2PaddleRect = new Rectangle(p2PaddleX,p2PaddleY,25,75);
		Rectangle p2Paddle2Rect = new Rectangle(p2Paddle2X,p2Paddle2Y,25,75);
		Rectangle ballRect=new Rectangle(ballx,bally,10,10);
   		 
   		 if(ballRect.intersects(p2PaddleRect) || ballRect.intersects(p2Paddle2Rect)){
   		 	ballvx=-ballvx;
   		 }
   	}

	public void p1PaddleCollide(){//if ball hits the paddle
		Rectangle p1PaddleRect = new Rectangle(p1PaddleX,p1PaddleY,25,75);
		Rectangle p1Paddle2Rect = new Rectangle(p1Paddle2X,p1Paddle2Y,25,75);
		Rectangle ballRect=new Rectangle(ballx,bally,10,10);
   		 
   		 if(ballRect.intersects(p1PaddleRect) || ballRect.intersects(p1Paddle2Rect)){
   		 	ballvx=-ballvx;
   		 }
   	}
	
	public void ballMove(){
		ballx+=ballvx;//ball movement
		bally+=ballvy;
	}
	
	public void ballBoundaries(){
		if (bally>700){
			ballvy=-ballvy;
		}
		if (bally<20){
			ballvy=-ballvy;
		}
	}

	public void outOfBounds(){
		if (ballx<0){
			p1Start=true;
			ballx=p1Paddle2X+30;
			bally=p1Paddle2Y+37;
			ballvx=0;
			ballvy=0;
			p2Score+=1;
		}
		if (ballx>1300){
			p2Start=true;
			ballx=p2Paddle2X+30;
			bally=p2Paddle2Y+37;
			ballvx=0;
			ballvy=0;
			p1Score+=1;
		}
		System.out.println(ballvx);
	}
	
	public void spikes(){
		Polygon spike1 = new Polygon(new int[] {310, 325, 340}, new int[] {15, 30, 15}, 3);//top edges
		Polygon spike2 = new Polygon(new int[] {620, 635, 650}, new int[] {15, 30, 15}, 3);
		Polygon spike3 = new Polygon(new int[] {930, 945, 960}, new int[] {15, 30, 15}, 3);
		
		Polygon spike4 = new Polygon(new int[] {310, 325, 340}, new int[] {705, 690, 705}, 3);//bottom edges
		Polygon spike5 = new Polygon(new int[] {620, 635, 650}, new int[] {705, 690, 705}, 3);
		Polygon spike6 = new Polygon(new int[] {930, 945, 960}, new int[] {705, 690, 705}, 3);
		
		Rectangle ballRect=new Rectangle(ballx,bally,10,10);
		
		if(spike1.intersects(ballRect)||spike2.intersects(ballRect)||spike3.intersects(ballRect)){
   		 	ballvy=-ballvy;
   		 	ballvx=10;
   		}
   		 
   		if(spike4.intersects(ballRect)||spike5.intersects(ballRect)||spike6.intersects(ballRect)){
   		 	ballvy=-ballvy;
   		 	ballvx=10;
   		}	
	}

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        
        if(p1Start==true && e.getKeyCode()==KeyEvent.VK_SPACE){
			p1Start=false;
        	ballvx=6;
        	ballvy=4;
        }
        if(p2Start==true && e.getKeyCode()==KeyEvent.VK_SPACE){
			p2Start=false;
        	ballvx=6;
        	ballvy=4;
        	ballx-=ballvx;
        	bally-=ballvy;
        }
    }
    
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }
    
    public void mouseClicked(MouseEvent e){}
  	public void mouseEntered(MouseEvent e){}
  	public void mouseExited(MouseEvent e){}
  
  	public void mousePressed(MouseEvent e){
    	if(screen == MENU){
      		if(menuInst.contains(mouse)){
        		screen = INSTRUCTIONS; 
      		}
      		if(menuPlay.contains(mouse)){
        		screen = PICK1;
      		}      		
    	}
    	
    	if(screen == PICK1){
    		if(bNext1.contains(mouse)){
        		screen = PICK2;
    		}  		
    	}
    	
    	
    	if(screen == PICK2){
    		if(bNext2.contains(mouse)){
        		screen = GAME;
    		}
    	}
    	
    	if(bBack.contains(mouse)){
    		screen = MENU; 
    	}
  	} 
    
    public void mouseReleased(MouseEvent e){}
    
    public void imageInRect(Graphics g, Image img, Rectangle area){
    	g.drawImage(img, area.x, area.y, area.width, area.height, null);
  	}
  	
  	public void drawMenu(Graphics g){
	    g.setColor(new Color(0x000000));  
	    g.fillRect(0,0,1300,750);
	    imageInRect(g, buttonPlayUp, menuPlay);
	    imageInRect(g, buttonInstUp, menuInst);
	    
	    /*
	    if(menuInstructions.contains(mouse)){
	      imageInRect(g, buttonPlayUp, menuInstructions);
	    }
	    else{
	      imageInRect(g, buttonDown, menuInstructions);   
	    }
	    if(menuPlay.contains(mouse)){
	      imageInRect(g, buttonUp, menuPlay);
	    }
	    else{
	      imageInRect(g, buttonDown, menuPlay);
	    }*/
  	}
    
    public void drawGame(Graphics g){ 	
    	g.drawImage(back,0,0,null);  
		g.setColor(Color.white);  
		g.fillRect(p1PaddleX,p1PaddleY,25,75);
		g.fillRect(p1Paddle2X,p1Paddle2Y,25,75);
		g.fillRect(p2PaddleX,p2PaddleY,25,75);
		g.fillRect(p2Paddle2X,p2Paddle2Y,25,75);
		
		g.drawPolygon(new int[] {310, 325, 340}, new int[] {15, 30, 15}, 3);//top edges
		g.drawPolygon(new int[] {620, 635, 650}, new int[] {15, 30, 15}, 3);
		g.drawPolygon(new int[] {930, 945, 960}, new int[] {15, 30, 15}, 3);
		
		g.drawPolygon(new int[] {310, 325, 340}, new int[] {705, 690, 705}, 3);//bottom edges
		g.drawPolygon(new int[] {620, 635, 650}, new int[] {705, 690, 705}, 3);
		g.drawPolygon(new int[] {930, 945, 960}, new int[] {705, 690, 705}, 3);
		
		g.setFont(fontLocal);
    	g.drawString(""+p1Score,550,70);
    	g.drawString(""+p2Score,680,70);
    	
		
		if (p1Start==true){//when ball goes off screen restart
			g.fillRect(p1Paddle2X+30,p1Paddle2Y+37,10,10);
			ballx=p1Paddle2X+30;
			bally=p1Paddle2Y+37;
		}
		
		if (p2Start==true){//when ball goes off screen restart
			g.fillRect(p2Paddle2X-15,p2Paddle2Y+37,10,10);
			ballx=p2Paddle2X-15;
			bally=p2Paddle2Y+37;
		}
		
		else{
			g.fillRect(ballx,bally,10,10);
			g.fillRect(ballx,bally,10,10);
		}
    }
    public void drawInstructions(Graphics g){
	    g.setColor(new Color(0x000000));
	    g.fillRect(0,0,1300,750);
	    imageInRect(g, buttonBack, bBack);
  	}
  	
  	public void drawPick1(Graphics g){
	    g.setColor(new Color(0x000000));
	    g.fillRect(0,0,1300,750);
	    imageInRect(g, buttonDashUp, pDash);
	    imageInRect(g, buttonPowerUp, pPower);
	    imageInRect(g, buttonNext1, bNext1);
	    imageInRect(g, buttonBack, bBack);
	    if(pDash.contains(mouse)){
	    	imageInRect(g, buttonDashDown, pDash);
	    }
	    if(pPower.contains(mouse)){
	    	imageInRect(g, buttonPowerDown, pPower);
	    }
  	}
  	
  	public void drawPick2(Graphics g){
	    g.setColor(new Color(0x000000));
	    g.fillRect(0,0,1300,750);
	    imageInRect(g, buttonDashUp, pDash);
	    imageInRect(g, buttonPowerUp, pPower);
	    imageInRect(g, buttonNext2, bNext2);
	    imageInRect(g, buttonBack, bBack);
	    if(pDash.contains(mouse)){
	    	imageInRect(g, buttonDashDown, pDash);
	    }
	    if(pPower.contains(mouse)){
	    	imageInRect(g, buttonPowerDown, pPower);
	    }
  	}
  
  	public void paint(Graphics g){
    	if(screen == MENU){
     		drawMenu(g);
	    }
	    else if(screen == GAME){
	    	drawGame(g);
	    }
	    else if(screen == INSTRUCTIONS){
	      	drawInstructions(g);
    	}
    	else if(screen == PICK1){
    		drawPick1(g);
    	}
    	else if(screen == PICK2){
    		drawPick2(g);
    	}
  	}
}

