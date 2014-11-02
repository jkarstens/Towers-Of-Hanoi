import java.awt.Color;

public class Disk {
  public int radius;
  public Disk above, below;

  public Disk(int r, Disk a, Disk b) {
    radius = r;
    above = a;
    below = b;
  }

  public Disk(int r) {
    this(r, null, null);
  }

  public boolean equals(Disk d) {
    return radius == d.radius;
  }
}
