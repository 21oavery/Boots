import java.util.*;

public class Main {
    public static HashMap<UUID, BootTaskFull> tasks;
    
    public static HashMap
    
    public static HashMap<String> buildTasks(File config) {
        // Read all files
        BufferedReader r;
        HashMap<String, HashMap<String, String>> keyValueMap = new HashMap<>();
        ArrayList<File> files = new ArrayList<>();
        HashMap<File, Boolean> seen = new HashMap<>();
        files.add(config);
        seen.add(config);
        while (files.size() > 0) {
            File current = files.get(0);
            String l;
            String taskCurrent = null;
            try {
                r = new BufferedReader(new FileReader(current));
            } catch (FileNotFoundException e) {
                System.err.println("[ERROR] Could not load config file");
                return;
            }
            while ((l = r.readline()) != null) {
                if (l.charAt(0) == '#') continue;
                if (l.charAt(0) == ':') {
                    l = l.substring(1);
                    
            }
        }
        HashMap<String, UUID> uuidMap = new HashMap<>();
        for (String s : keyValueMap.keySet()) uuidMap.put(s, UUID.randomUUID());
        for (Map.Entry<String, HashMap<String, String>> e : keyValueMap) {
        }
        }
    }
    
    public static void runAllTasks() {
        boolean notDone = true;
        while (notDone) {
            notDone = false;
            for (Map.Entry<UUID, BootTaskFull> e : tasks.entrySet()) {
                BootTaskFull b = e.getValue();
                if (b.state != 0) continue;
                boolean ok = true;
                for (int i = 0; i < b.deps.length; i++) {
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
                for (int i = 0; i < b.depsOpt.length; i++) {
                    BootTaskFull b2 = task.get(b.depsOpt[i]);
                    if ((b2 == null) || (b2.state == 2)) continue;
                    if (b2.state == 0) {
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
    public UUID[] depsOpt;
    public Callable<Boolean> run;
    public int state = 0; // 0 - not run, 1 - run, 2 - failed
}

class BootTaskProp {
    public String name;
    public String[] macros; // In call order
    public String type;
    public String[] deps;
    public String[] depsOpt;
}

class BootTaskLib {
    public BootTaskFull[] tasks;
    public Object[] export; // Of the form "name1, uuid1, name2, uuid2, ..."
