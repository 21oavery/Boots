import java.util.*;

public class Main {
    public static HashMap<UUID, BootTaskFull> tasks;
    
    public static void buildTasks(File config) {
        BufferedReader r = new BufferedReader(new FileReader)
    
    public static void runAllTasks() {
        boolean notDone = true;
        while (notDone) {
            notDone = false;
            for (Map.Entry<UUID, BootTaskFull> e : tasks.entrySet()) {
                BootTaskFull b = e.getValue();
                if (b.state != 0) continue;
                boolean ok = true;
                for (int i = 0; i < b.deps; i++) {
                    BootTaskFull b2 = task.get(b.deps[i]);
                    if ((b2 == null) || (b2.state == 2)) {
                        System.err.println("[ERROR] Task (" + e.getKey() + ") cannot run due to dependency failure");
                        b.state = 2;
                        ok = false;
                        break;
                    } else if (b2.state == 0) {
                        ok = false;
                        break;
                    }
                }
                if (!ok) continue;
                ok = b.run.call();
                if (ok) {
                    b.state = 1;
                    System.out.println("[SUCCESS] Task (" + e.getKey() + ") ran");
                    notDone = true;
                } else {
                    b.state = 2;
                    System.err.println("[ERROR] Task (" + e.getKey() + ") failed");
                }
            }
        }
    }
    
    public static void main(String[] args) {
        
    }
}

class BootTaskFull {
    public UUID[] deps;
    public Callable<Boolean> run;
    public int state = 0; // 0 - not run, 1 - run, 2 - failed
}
