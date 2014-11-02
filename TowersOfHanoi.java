import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;
import java.util.ArrayList;

public class TowersOfHanoi extends Applet implements ActionListener, AdjustmentListener {
  private Graphics dbGraphics;
  private Image dbImage;

  private Post[] posts;
  private Post origin, target;
  private ArrayList<Disk> initialDisks;
  private int NUM_DISKS;
  private int MIN_RADIUS, MAX_RADIUS;

  private Scrollbar disksScrollbar;
  private CheckboxGroup speedCbg;
  private Checkbox normalSpeedCb, superSpeedCb;
  private Button startButton;
  private boolean start;

  private Font titleFont = new Font("Bradley Hand ITC", Font.BOLD+Font.ITALIC, 55);
  private Font subtitleFont = new Font("Californian FB", Font.BOLD, 14);

  public void init() {
    setSize(1000, 530);
    setBackground(new Color(95, 180, 195));

    disksScrollbar = new Scrollbar(Scrollbar.HORIZONTAL, 5, 5, 1, 30);
    disksScrollbar.setBackground(new Color(230, 230, 250));
    disksScrollbar.addAdjustmentListener(this);
    disksScrollbar.setFocusable(false);
    add(disksScrollbar);
    speedCbg = new CheckboxGroup();
    normalSpeedCb = new Checkbox("Normal", speedCbg, false);
    normalSpeedCb.setFocusable(false);
    add(normalSpeedCb);
    superSpeedCb = new Checkbox("Super Speed!", speedCbg, true);
    superSpeedCb.setFocusable(false);
    add(superSpeedCb);
    startButton = new Button("START!");
    startButton.setBackground(disksScrollbar.getBackground());
    startButton.addActionListener(this);
    startButton.setFocusable(false);
    add(startButton);
    start = false;
    setUpTowers();
  }

  public void setUpTowers() {
    NUM_DISKS = disksScrollbar.getValue();
    MIN_RADIUS = 2;
    MAX_RADIUS = 2*NUM_DISKS;
    initialDisks = new ArrayList<Disk>();
    initialDisks.add(new Disk(MAX_RADIUS));
    for (int i = 1; i < MAX_RADIUS / 2; i++) {
      Disk d = new Disk(MAX_RADIUS - (2 * i));
      initialDisks.get(i - 1).above = d;
      d.below = initialDisks.get(i - 1);
      initialDisks.add(d);
    }
    posts = new Post[3];
    posts[0] = new Post(new ArrayList<Disk>(initialDisks), 150);
    posts[1] = new Post(450);
    posts[2] = new Post(750);
    origin = posts[0];
    target = posts[2];
  }

  public void paint(Graphics g) {
    drawStrings(g);
    setAWTComponentsBounds(g);
    for (int i = 0; i < posts.length; i++) {
      g.setColor(Color.LIGHT_GRAY);
      g.fillRect(posts[i].x, 200, 6, 300); // draw Posts
      ArrayList<Disk> currentDisks = posts[i].disks;

      //draw Disks
      g.setColor(Color.BLACK);
      for(int j=0; j<currentDisks.size(); j++){
        int r = currentDisks.get(j).radius;
        g.fillRect( posts[i].x + 3 - r, 500-(10*(j+1)), r*2, 9);
      }
    }

    g.setColor(Color.BLACK);

    if(start){ //start the towers of hanoi!

      start = false;
      move( posts[0].disks.get(0), posts[0], posts[2], posts[1], g );
      disksScrollbar.setEnabled(true);
      normalSpeedCb.setEnabled(true);
      superSpeedCb.setEnabled(true);
      startButton.setEnabled(true);
    }
  }

  public void setAWTComponentsBounds(Graphics g){

    disksScrollbar.setBounds(850, 50, 120, 20);
    normalSpeedCb.setBounds(880, 100, 120, 20);
    superSpeedCb.setBounds(860, 120, 120, 20);
    startButton.setBounds(850, 150, 120, 30);
  }

  public void drawStrings(Graphics g){

    g.setFont(titleFont);
    g.drawString("Towers of Hanoi", 275, 50);

    g.setFont(subtitleFont);
    g.drawString("Number of Disks: " + NUM_DISKS, 850, 43);
    g.drawString("Speed", 890, 93);
  }

  public void animateMove(Graphics g, Disk d, Post from, Post to){

    int dms = 0; //delay milliseconds for super speed
    if(normalSpeedCb.getState()) dms = 10;

    //move disk up post
    int x = from.x + 3 - d.radius;
    int y = 500 - (from.size()+1)*10;

    while(y > 200){
      g.clearRect(x, y+10, d.radius*2, 9);//erase previous
      g.setColor(Color.LIGHT_GRAY);
      g.fillRect(from.x, y+10, 6, 9);
      g.setColor(Color.BLACK);
      g.fillRect(x, y, d.radius*2, 9);
      y-=10;
      repaint();
      delay(dms);
    }

    g.clearRect(x, y+10, d.radius*2, 9);//erase previous
    g.setColor(Color.LIGHT_GRAY);
    g.fillRect(from.x, y+10, 6, 9);

    //move disk in quadratic curve path to different post
    double a = 1 / ( Math.pow( (from.x-to.x)/2.0, 2) / 100.0 );
    double yint = (from.x + to.x) / 2.0;
    double b = 100.0;
    int increment = from.x < to.x ? 19 : -19; //multiples of some numbers dont work...

    while(x < to.x-10 || x > to.x+10){

      if( a*Math.pow(x-yint,2)+b < 190 ){

        g.clearRect(x - increment, (int)( a*Math.pow(x-increment-yint,2)+b ), d.radius*2, 9);//erase previous
        g.setColor(Color.BLACK);
        g.fillRect(x, (int)( a*Math.pow(x-yint,2)+b ), d.radius*2, 9);
      }

      x+=increment;
      repaint();
      delay(dms);
    }

    g.clearRect(100, 100, 700, 100);

    //move disk down post
    x = to.x + 3 - d.radius;
    y = 210;

    while(y < 500-to.size()*10){

      g.clearRect(x, y-10, d.radius*2, 9);//erase previous
      g.setColor(Color.LIGHT_GRAY);
      g.fillRect(to.x, y-10, 6, 9);
      g.setColor(Color.BLACK);
      g.fillRect(x, y, d.radius*2, 9);
      y+=10;
      repaint();
      delay(dms);
    }
  }

  public void move(Disk d, Post from, Post target, Post transfer, Graphics g){

    if(d.above != null) move(d.above, from, target, transfer, g); //cant move this one, call the one above it

    if(from.size() % 2 == 0){

      animateMove(g, d, from, transfer);
      from.pop();
      transfer.push(d);
    }

    else{

      animateMove(g, d, from, target);
      from.pop();
      target.push(d);
    }

    if(d.radius == MIN_RADIUS) return; //base case

    //else find Post and spot where next lowest Disk is
    Post p = null;
    int diskIndex = 0;

    for(int i=0; i<posts.length; i++){

      if( posts[i].getIndex(d.radius-2) != -1){

        p = posts[i];
        diskIndex = p.getIndex(d.radius-2);
        break;
      }
    }

    //1st and 2nd choices
    if(p.equals(posts[0])) move(p.disks.get(diskIndex), p, posts[2], posts[1], g);
    else if(p.equals(posts[1])) move(p.disks.get(diskIndex), p, posts[2], posts[0], g);
    else if(p.equals(posts[2])) move(p.disks.get(diskIndex), p, posts[1], posts[0], g);
  }

  public void delay(int t){

    try{

      Thread.sleep(t);
    }
    catch(InterruptedException e){
    }
  }

  public void adjustmentValueChanged(AdjustmentEvent e){

    Object source = e.getSource();

    if(source == disksScrollbar){

      NUM_DISKS = disksScrollbar.getValue();
      setUpTowers();
      repaint();
    }
  }

  public void actionPerformed(ActionEvent e){

    Object source = e.getSource();

    if(source == startButton){

      NUM_DISKS = disksScrollbar.getValue();

      setUpTowers();
      start = true;

      disksScrollbar.setEnabled(false);
      normalSpeedCb.setEnabled(false);
      superSpeedCb.setEnabled(false);
      startButton.setEnabled(false);

      repaint();
    }
  }

  public static void main(String[] args){

    Applet thisApplet = new TowersOfHanoi();
    thisApplet.init();
    thisApplet.start();

    JFrame frame = new JFrame("Towers Of Hanoi");
    frame.setSize(thisApplet.getSize());
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout());
    frame.getContentPane().add(thisApplet, BorderLayout.CENTER);
    frame.setVisible(true);
  }
}
