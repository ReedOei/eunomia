package com.reedoei.eunomia.ast;

import com.reedoei.eunomia.util.DateUtil;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class JavaProjectTest {
    private JavaProject project;

    @Before
    public void setUp() throws Exception {
        project = new JavaProject("src/test/resources/eunomia-test-project/.git", "HEAD");
    }

    @After
    public void tearDown() throws IOException, GitAPIException {
        // Make sure any changes to the project version get undone
        project.getGit().checkout().setName("master").call();
        project.close();
    }

    @Test
    public void testInitializeRepository() throws Exception {
        assertEquals(0, project.testMethods().count());

        assertEquals(2, project.classes().size());
        assertTrue(project.classes().stream().anyMatch(c -> c.declaration.getQualifiedName().equals("com.reedoei.eunomia.test.project.Main")));
        assertTrue(project.classes().stream().anyMatch(c -> c.declaration.getQualifiedName().equals("com.reedoei.eunomia.test.project.Circle")));
    }

    @Test
    public void getCommitIdFromTimestamp() throws Exception {
        // This value comes from the eunomia-test-project in the test resources directory.
        assertEquals("b704939345681a49fccb2423771b355367795bc4",
                project.commitBefore(DateUtil.makeDate(2018, 5, 4, 8, 0, 0)));
    }
}