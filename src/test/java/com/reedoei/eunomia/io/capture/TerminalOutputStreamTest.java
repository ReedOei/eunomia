package com.reedoei.eunomia.io.capture;

import org.junit.Before;
import org.junit.Test;

import java.io.PrintStream;

import static org.junit.Assert.*;

public class TerminalOutputStreamTest {
    @SuppressWarnings("nullness")
    private TerminalOutputStream outputStream;

    @SuppressWarnings("nullness")
    private PrintStream print;

    @Before
    public void setUp() {
        outputStream = new TerminalOutputStream();
        print = new PrintStream(outputStream);
    }

    @Test
    public void testBasicPrint() {
        print.println("Hello world!");

        assertEquals("Hello world!", outputStream.toString());
    }

    @Test
    public void testPrintSeveralLines() {
        print.println("Hello");
        print.println("My name is");
        print.println("Reed");

        assertEquals("Hello\nMy name is\nReed", outputStream.toString());
    }

    @Test
    public void testPrintCR() {
        print.print("Hello world!");
        print.println("\rGoodbye!");

        assertEquals("Goodbye!rld!", outputStream.toString());
    }

    @Test
    public void testClearLine() {
        print.print("Hello world!");
        print.print("\rtest\033[2KHi!");

        assertEquals("testHi!", outputStream.toString());
    }

    @Test
    public void testPrintBackspace() {
        print.print("Hello world!");
        print.print("\b\b\b\b\b");
        print.print("Just kidding.");

        assertEquals("Hello wJust kidding.", outputStream.toString());
    }
}