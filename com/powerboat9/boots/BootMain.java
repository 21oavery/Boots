import java.util.*;

public class Main {
    public static HashMap<UUID, BootTaskFull> tasks;
    
    public static HashMap<String, BootTaskProp> buildTasks(File config) {
        void printParseError(int line, String s) {
            System.err.println("[ERROR][LINE " + line + "] " + s);
        }
        // Read all files
        BufferedReader r;
        HashMap<String, BootTaskProp> taskMap = new HashMap<>();
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
            int line = 0;
            while ((l = r.readline()) != null) {
                line++;
                if (l.charAt(0) == '#') continue;
                if (l.charAt(0) == ':') {
                    String k, v;
                    int i;
                    char c;
                    for (i = 1; i < l.length() ; i++) {
                        char c = l.charAt(i);
                        if (Character.isWhitespace(c)) {
                            printParseError(line, "Task name cannot contain whitespace");
                            return null;
                        }
                        if (c == ')') {
                            printParseError(line, "Task name cannot contain mismatched parenthesis");
                            return null;
                        }
                        if (c == '(') break;
                    }
                    BootTaskProp t = new BootTaskPrep();
                    t.name = l.substring(1, i);
                    if (i < l.length()) {
                        if (l.charAt(l.length() - 1) != ')') {
                            printParseError(line, "Parameter list is not closed");
                            return null;
                        }
                        String[] params = l.substring(i + 1, l.length() - 1).split(",", -1);
                        for (int j = 0; j < params.length; j++) {
                            if (params[j])
                            params[j] = params[j].trim();
                            if (params[j].length() == 0) {
                                printParseError(line, "Parameter list has empty parameters"); // Technically parsable, but it's time to stop
                                return null;
                            }
                        }
                        t.macros = params;
                    }
                    t.extraProps = new HashMap<>();
                    taskMap.put(t.name, t);
                } else {
                    if (taskCurrent == null) {
                        printParseError(line, "Properties must have a task");
                        return null;
                    }
                    int i;
                    for (i = 0; i < l.length(); i++) {
                        char c = l.charAt(i);
                        if (Character.isWhitespace(c)) {
                            printParseError(line, "Property name cannot contain whitespace");
                            return null;
                        }
                        if (c == ':') break;
                    }
                    String k = l.substring(0, i);
                    String v;
                    if (i == l.length()) {
                        v = "true";
                    } else {
                        v = l.substring(i + 1);
                    }
                    ArrayList<String> tdeps = new ArrayList<>();
                    ArrayList<String> todeps = new ArrayList<>();
                    BootTaskProp task = taskMap.get(taskCurrent);
                    switch (k) {
                        case "dep":
                            tdeps.add(v);
                            break;
                        case "depo":
                            todeps.add(v);
                            break;
                        case "type":
                            task.name = v;
                            break;
                        default:
                            task.extraProps.put(k, v);
                    }
                }
            }
        }
        return taskMap;
    }
    
    public static BootTaskList cook(HashMap<String, BootTaskProp> propList) {
        String getTrueName(String task) {
            int paran;
            if ((paran = task.indexOf('(')) != -1) return task.substring(0, paran);
            return task;
        }
        BootTaskProp createTrueProp(BootTaskProp templet) {
            if ((templet.macros == null) || (templet.macros.length == 0)) return null;
            
        BootTaskList l = new BootTaskList();
        l.map = new HashMap<>();
        HashMap<String, UUID> uuidMap = new HashMap<>();
        uuidMap.put("root", l.rootUUID = UUID.randomUUID());
        BootTaskProp root = propList.get("root");
        ArrayList<String> namesToResolve = new ArrayList<>();
        if (root.macros != null) {
            System.err.println("[ERROR] Root task cannot require parameters");
            return null;
        }
        while (namesToResolve.size() > 0) {
            String name = namesToResolve.remove(0);
            BootTaskFull full = new BootTaskFull();
            full.state = 0;
            int paran;
            if ((paran = name.indexOf('(')) == -1) {
                BootTaskProp prop = propList.get(name);
                full.deps = new UUID[prop.deps.length];
                full.depsOpt = new UUID[prop.depsOpt.length];
                for (int i = 0; i < prop.deps.length; i++) {
                    String s = prop.deps[i];
                    UUID uuid = uuidMap.get(s);
                    if (uuid == null) {
                        uuid = UUID.randomUUID();
                        uuidMap.put(s, uuid);
                        namesToResolve.add(s);
            
            if ((prop = ) != null) {
                
                full.deps = new UUID[prop.deps.length];
                full.depsOpt = new UUID[prop.depsOpt.length];
    
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
    public HashMap<String, String> extraProps;
}

class BootTaskList {
    public UUID[] globals;
    public HashMap<UUID, BootTaskFull> map;
}
