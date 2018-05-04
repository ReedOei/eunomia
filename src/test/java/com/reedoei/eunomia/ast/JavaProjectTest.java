package com.reedoei.eunomia.ast;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class JavaProjectTest {
    private JavaProject project;

    @Before
    public void setUp() throws IOException {
        project = new JavaProject("src/test/resources/eunomia-test-project/.git", "HEAD");
    }

    @After
    public void tearDown() throws IOException, GitAPIException {
        // Make sure any changes to the project version get undone
        new Git(project.getRepository()).checkout().setName("master").call();
    }

    @Test
    public void testInitializeRepository() throws Exception {
        assertEquals(0, project.testMethods().count());

        assertEquals(1, project.classes().size());
        assertTrue(project.classes().stream().anyMatch(c -> c.declaration.getQualifiedName().equals("com.reedoei.eunomia.test.project.Main")));
    }

    @Test
    public void getCommitIdFromTimestamp() throws Exception {
    }
}