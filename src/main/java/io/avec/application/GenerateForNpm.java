package io.avec.application;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GenerateForNpm {

    public void generate() {
        Set<Pair<String,String>> dependencies = loadDependencies();
        writeDependencies(dependencies);
    }

    private void writeDependencies(Set<Pair<String,String>> dependencies) {
        Validate.notEmpty(dependencies);

//        writeDependenciesWithVersions(dependencies);
        writeDependenciesNoVersions(dependencies);

    }

    private void writeDependenciesNoVersions(Set<Pair<String,String>> dependencies) {
        Set<String> addDependenciesOnlyOnce = new HashSet<>();
        try (BufferedWriter output = new BufferedWriter(new FileWriter("dependencies_no_versions.txt"))) {
            for(Pair<String, String> pair : dependencies) {
                String dependency = pair.getLeft();
                if(!addDependenciesOnlyOnce.contains(dependency)) {
                    output.write(dependency + System.lineSeparator());
                    addDependenciesOnlyOnce.add(dependency);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void writeDependenciesWithVersions(Set<Pair<String,String>> dependencies) {
//        try (BufferedWriter output = new BufferedWriter(new FileWriter("dependencies_with_versions.txt"))) {
//            for(Pair<String, String> pair : dependencies) {
//                String dependency = pair.getLeft() + "@" + pair.getRight();
//                output.write( dependency + System.lineSeparator());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @SuppressWarnings("ConstantConditions")
    private Set<Pair<String,String>> loadDependencies() {
        final Set<Pair<String,String>> dependencies = new HashSet<>();
        try(BufferedReader input = new BufferedReader(new FileReader("pnpm-lock.yaml"))) {
            Pattern p1 = Pattern.compile("^/(.*):"); //   All packages starting with /
            Pattern p2 = Pattern.compile("^'(.{2,})':(\\d+\\.\\d+\\.\\d+)"); // starting with 'xxx' and xxx length minimum 2
            Pattern p3 = Pattern.compile("^([0-9a-zA-Z\\_\\-\\.]+):(\\d+\\.\\d+\\.\\d+)"); // chokidar: 3.5.2

            boolean doMatch1 = false;
            boolean doMatch2 = true;
            boolean doMatch3 = true;

            String line;
            while((line = input.readLine()) != null) {

                line = line.replace(" ", ""); // remove spaces
                Matcher m1 = p1.matcher(line);
                Matcher m2 = p2.matcher(line);
                Matcher m3 = p3.matcher(line);

                if(m1.find() && doMatch1) {
                    String group = m1.group(1); // /xx/xxx/xxx
                    int lastIndexOf = group.lastIndexOf("/");
                    group = group.substring(0, lastIndexOf);

                    dependencies.add(Pair.of(group, ""));
                }
                else if(m2.find() && doMatch2) {
                    dependencies.add(Pair.of(m2.group(1), m2.group(2)));
                }

                else if(m3.find() && doMatch3) {
                    dependencies.add(Pair.of(m3.group(1), m3.group(2)));
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<Pair<String,String>> sorted = dependencies.stream()
                .sorted(Comparator.comparing(Pair::getLeft))
                .collect(Collectors.toCollection(LinkedHashSet::new));

            System.out.println("dependency count: " + sorted.size());
//            deps.forEach(System.out::println);
        return sorted;
    }

    public static void main(String[] args) {
        GenerateForNpm generateForNpm = new GenerateForNpm();
        generateForNpm.generate();
    }
}
