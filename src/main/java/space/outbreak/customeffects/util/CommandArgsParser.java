package space.outbreak.customeffects.util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.sql.Array;
import java.util.*;
import java.util.function.Consumer;

public class CommandArgsParser implements CommandExecutor, TabCompleter {
    public static class Action {
        private final boolean playerOnly;
        private final ArgsPattern argsPattern;
        private final Consumer<ArgsPattern> callback;

        public Action(boolean playerOnly, Consumer<ArgsPattern> callback, ArgsPattern argsPattern) {
            this.playerOnly = playerOnly;
            this.callback = callback;
            this.argsPattern = argsPattern;
        }

        public void run() {
            if (playerOnly && !(argsPattern.getSender() instanceof Player)) {
                if (argsPattern.onPlayerOnlyFail != null)
                    argsPattern.onPlayerOnlyFail.accept(argsPattern);
                return;
            }
            callback.accept(argsPattern);
        }
    }

    @FunctionalInterface
    public interface OptionsProvider {
        List<String> getOptions(CommandSender sender);
    }

    public class ArgsPattern {
        private CommandSender sender;
        int requiredArgsCount = 0;
        private final String permission;
        private final List<String> pattern = new ArrayList<>();
        private final List<Action> actions = new ArrayList<>();
        private final HashMap<String, Arg> argValues = new HashMap<>();
        private Consumer<ArgsPattern> onPlayerOnlyFail;
        private final Map<String, OptionsProvider> optionsProviders = new HashMap<>();

        public class Arg {
            String name;
            final boolean required;
            String value;

            void setValue(String value) {
                this.value = value;
            }

            public String getValue() {
                return value;
            }

            Arg(String name, boolean required) {
                this.name = name;
                this.required = required;
                this.value = name;
            }
        }

        public ArgsPattern addOptionsProvider(String arg, OptionsProvider provider) {
            optionsProviders.put(arg, provider);
            return this;
        }

        @Override
        public String toString() {
            return "ArgsPattern('"+String.join(", ", pattern)+"')";
        }

        /** Позволяет назначить действие, которое будет срабатывать, если
         * консоль пытается выполнить команду, предназначенную только для игроков */
        public ArgsPattern setPlayerOnlyTestFailAction(Consumer<ArgsPattern> callback){
            this.onPlayerOnlyFail = callback;
            return this;
        }

        class PlaceholderArg extends Arg {
            PlaceholderArg(String name, boolean required) {
                super(name, required);
            }
        }

        class VariableArg extends Arg implements OptionsProvider {
            private final List<String> options = new ArrayList<>();

            VariableArg(String name, boolean required) {
                super(name, required);
                options.addAll(Arrays.asList(name.split("\\|")));
            }

            @Override
            public List<String> getOptions(CommandSender sender) {
                return options;
            }
        }

        private ArgsPattern(String permission, String... args) {
            this.permission = permission;
            for (String argname : args) {
                boolean required = !argname.contains("?");
                if (argname.startsWith("<") && argname.endsWith(">")) {
                    pattern.add(argname);
                    argValues.put(argname, new PlaceholderArg(argname, required));
                }
                else if (argname.contains("|")) {
                    VariableArg variableArg = new VariableArg(argname, required);
                    pattern.add(argname);
                    argValues.put(argname, variableArg);
                    optionsProviders.put(argname, variableArg);
                }
                else {
                    pattern.add(argname);
                    argValues.put(argname, new Arg(argname, required));
                }
                if (required)
                    requiredArgsCount++;
            }
        }

        public ArgsPattern addAction(Consumer<ArgsPattern> action) {
            actions.add(new Action(false, action, this));
            return this;
        }

        public ArgsPattern addPlayerOnlyAction(Consumer<ArgsPattern> action) {
            actions.add(new Action(true, action, this));
            return this;
        }

        private List<String> getOptions(String arg, CommandSender sender) {
            OptionsProvider optionsProvider = optionsProviders.get(arg);
            if (optionsProvider == null)
                return new ArrayList<>();
            return optionsProvider.getOptions(sender);
        }

        public String getArgVal(String arg) {
            return argValues.get(arg).getValue();
        }

        public CommandSender getSender() {
            return sender;
        }

        void run(CommandSender sender) {
            this.sender = sender;
            for (Action action : actions)
                action.run();
        }

        private boolean match(String[] args) {
            int i = 0;
            System.out.println();
            System.out.println();
            System.out.println(this);
            for (String argname : pattern) {
                Arg arg = argValues.get(argname);
                if (i >= args.length) {
                    System.out.println("   - i (" + i + ") больше длины аргументов ("+String.join(",", args)+")" );
                    return false;
                }

                if (arg instanceof PlaceholderArg) {
                    arg.setValue(args[i]);
                } else if (arg instanceof VariableArg) {
                    if (!((VariableArg)arg).options.contains(args[i]) && arg.required) {
                        System.out.println("   - " + arg.name  + " не содержится в " + ((VariableArg)arg).options);
                        return false;
                    }
                } else if (!args[i].equalsIgnoreCase(arg.name) && arg.required) {
                    System.out.println("   - " + String.join(", ", args) + " не подходит.");
                    return false;
                }
                if (arg.required)
                    i++;
            }
            return true;
        }

        void setSender(CommandSender sender) {
            this.sender = sender;
        }

        boolean havingPermission() {
            if (permission == null)
                return true;
            return sender.hasPermission(permission);
        }
    }

    private final List<ArgsPattern> argsPatterns = new ArrayList<>();

    /** Добавляет паттерн аргументов */
    public ArgsPattern addPattern(String permission, String args) {
        ArgsPattern argsPattern = new ArgsPattern(permission, args.split(" "));
        argsPatterns.add(argsPattern);
        return argsPattern;
    }

    private boolean detect(CommandSender sender, String... args) {
        for (ArgsPattern argsPattern : argsPatterns) {
            if (argsPattern.match(args)) {
                argsPattern.run(sender);
                return true;
            }
        }
        return false;
    }

    private List<String> processTabCompletion(CommandSender sender, String... args) {
        List<String> options = new ArrayList<>();
        List<String> _completions = new ArrayList<>();

        for (ArgsPattern argsPattern : argsPatterns) {
            argsPattern.setSender(sender);
            if (!argsPattern.havingPermission()) continue;
            int i = 0;
            for (String argname : argsPattern.pattern) {
                ArgsPattern.Arg arg = argsPattern.argValues.get(argname);
                if (i >= args.length)
                    break;
                if (!arg.name.equalsIgnoreCase(args[i]) && arg.required) {
                    break;
                }
                options = argsPattern.getOptions(arg.name, sender);
                i++;
            }
        }

        StringUtil.copyPartialMatches(args[args.length-1], options, _completions);
        Collections.sort(_completions);
        return _completions;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return this.detect(sender, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return this.processTabCompletion(sender, args);
    }
}
