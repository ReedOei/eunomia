package com.reedoei.eunomia.subject;

import com.reedoei.eunomia.ast.JavaProject;
import com.reedoei.eunomia.ast.JavaProjectBuilder;
import com.reedoei.eunomia.subject.classpath.Classpath;
import com.reedoei.eunomia.util.Util;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

// TODO: Use maven calls to get the actual directories to handle project specific cases.
// NOTE: Seems to not really be possible. Might have to use command line to call maven.
public class MavenSubject implements Subject {
    private final String name;
    private final Path root;

    public static MavenSubject fromRoot(final Path root) {
        final String name = root.toAbsolutePath().getFileName() != null ? root.toAbsolutePath().getFileName().toString() : "unknown";

        return fromRoot(name, root);
    }

    public static MavenSubject fromRoot(final String name, final Path root) {
        return new MavenSubject(name, root);
    }

    public static MavenSubject fromTarget(final String name, final Path target) {
        return fromRoot(name, target);
    }

    public static MavenSubject fromTarget(final Path target) {
        return fromRoot(target.getParent());
    }

    public static MavenSubject fromPom(final Path pomPath) {
        final MavenXpp3Reader reader = new MavenXpp3Reader();
        try {
            final Model model = reader.read(new FileInputStream(pomPath.toFile()));
            return MavenSubject.fromRoot(model.getGroupId() + ":" + model.getArtifactId() + ":" + model.getVersion(), pomPath.getParent());
        } catch (IOException | XmlPullParserException ignored) {}

        return MavenSubject.fromRoot(pomPath.getParent());
    }

    private MavenSubject(final String name, final Path root) {
        this.name = name;
        this.root = root;
    }

    @Override
    public Path root() {
        return root;
    }

    @Override
    public Path testClasses() {
        return root.resolve("target").resolve("test-classes");
    }

    @Override
    public Path classes() {
        return root.resolve("target").resolve("classes");
    }

    @Override
    public Path dependencies() {
        return root.resolve("target").resolve("dependency");
    }

    @Override
    public Path mainSrc() {
        return root.resolve("src").resolve("main").resolve("java");
    }

    @Override
    public Path testSrc() {
        return root.resolve("src").resolve("test").resolve("java");
    }

    @Override
    public String classpath() {
        return Classpath.build(classes().toString(), testClasses().toString(), dependencies() + "/*").toString();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Stream<Subject> submodules() {
        try {
            return Files.walk(root)
                    .filter(p -> p.toFile().getName().equals("pom.xml") && !root.resolve("pom.xml").equals(p))
                    .map(p -> MavenSubject.fromRoot(p.getParent()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Stream.empty();
    }

    @Override
    public JavaProject project() throws Exception {
        return new JavaProjectBuilder(root()).build();
    }
}
