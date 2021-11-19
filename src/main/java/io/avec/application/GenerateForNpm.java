package io.avec.application;

import org.apache.commons.lang3.Validate;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateForNpm {

    public void generate() {
        List<String> dependencies = loadDependencies();
        writeDependencies(dependencies);
    }

    private void writeDependencies(List<String> dependencies) {
        Validate.notEmpty(dependencies);

        try (BufferedWriter output = new BufferedWriter(new FileWriter("dependencies.txt"))) {
            for(String dependency : dependencies) {
                output.write(dependency + System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private List<String> loadDependencies() {
        final Set<String> dependencies = new HashSet<>();
        try(BufferedReader input = new BufferedReader(new FileReader("pnpm-lock.yaml"))) {
            Pattern p1 = Pattern.compile("^/(.*):"); //   All packages starting with /
            Pattern p2 = Pattern.compile("^'(.{2,})'"); // starting with 'xxx' and xxx length minimum 2
            Pattern p3 = Pattern.compile("^(?!integrity:|node:)([0-9a-zA-Z\\_\\-\\.]+):[0-9a-zA-Z\\^\\.\\_\\-\\+\\@]+(?<!true|false)$"); // chokidar: 3.5.2

            boolean doMatch1 = true;
            boolean doMatch2 = true;
            boolean doMatch3 = true;

            String line;
            while((line = input.readLine()) != null) {

                line = line.replaceAll(" ", ""); // remove spaces
                Matcher m1 = p1.matcher(line);
                Matcher m2 = p2.matcher(line);
                Matcher m3 = p3.matcher(line);

                if(m1.find() && doMatch1) {
                    String group = m1.group(1); // /xx/xxx/xxx
                    int lastIndexOf = group.lastIndexOf("/");
                    group = group.substring(0, lastIndexOf);

                    dependencies.add(group);
                }
                else if(m2.find() && doMatch2) {
                    dependencies.add(m2.group(1));
                }

                else if(m3.find() && doMatch3) {
                    dependencies.add(m3.group(1));
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> deps = dependencies.stream()
                .sorted()
                .filter(s -> !(s.startsWith("@vaadin") && s.endsWith("-plugin"))) // remove|
                .filter(s -> !(s.startsWith("@vaadin") && s.endsWith("/flow-frontend"))) // remove|
                .filter(s -> !(s.startsWith("@vaadin") && s.endsWith("/theme-loader"))) // remove|
                .filter(s -> !(s.startsWith("@vaadin") && s.endsWith("/form"))) // remove|
                .toList();

            System.out.println("dependency count: " + deps.size());
//            deps.forEach(System.out::println);
        return deps;
    }

    public static void main(String[] args) {
        GenerateForNpm generateForNpm = new GenerateForNpm();
        generateForNpm.generate();
    }
}
