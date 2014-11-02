import java.util.ArrayList;

public class Post {

  public ArrayList<Disk> disks;
  public int x;

  public Post(ArrayList<Disk> d, int loc) {
    disks = d;
    x = loc;
  }

  public Post(int loc) {
    this(new ArrayList<Disk>(), loc);
  }

  public void push(Disk d) {
    if (!isEmpty()) {
      peek().above = d;
      d.below = peek();
    }
    disks.add(d);
  }

  public void pop() {
    if (!isEmpty()) {
      Disk d = peek();
      if(d.below != null) {
        d.below.above = null;
      }
      disks.remove(d);
    }
  }

  public Disk peek() {
    if (!isEmpty()) {
      return disks.get(size() - 1);
    }
    return null;
  }

  public int getIndex(Disk d) {
    for (int i = 0; i < size(); i++) {
      if (disks.get(i).equals(d)) {
        return i;
      }
    }
    return -1;
  }

  public int getIndex(int r) {
    return getIndex(new Disk(r));
  }

  public int size() {
    return disks.size();
  }

  public boolean isEmpty() {
    return size() == 0;
  }

  public boolean equals(Post p) {
    return x == p.x;
  }
}
