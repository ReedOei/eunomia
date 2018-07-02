package com.reedoei.eunomia.io.capture;

import com.reedoei.eunomia.collections.ListUtil;
import com.reedoei.eunomia.string.StringUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class TerminalOutputStream extends OutputStream {
    private final List<String> lines = new ArrayList<>();
    private final StringBuilder fullOutput = new StringBuilder();

    private String currentLine = "";
    private int linePos = 0;

    @Override
    public void write(int i) throws IOException {
        char c = (char) i;

        switch (c) {
            case '\n':
                lines.add(currentLine);
                fullOutput.append(currentLine).append(c);
                currentLine = "";
                linePos = 0;
                break;

            case '\r':
                linePos = 0;
                break;

            case '\b':
                linePos = Math.max(0, linePos - 1);
                break;

            default:
                if (linePos < currentLine.length()) {
                    currentLine = StringUtil.set(currentLine, linePos, c);
                } else {
                    currentLine += c;
                }

                linePos++;

                break;
        }

        if (currentLine.substring(0, linePos).endsWith("\033[2K")) {
            currentLine = currentLine.substring(0, linePos - "\033[2K".length() + 1);
            linePos = Math.min(linePos, currentLine.length() - 1);
        }
    }

    public List<String> getLines() {
        if (currentLine.isEmpty()) {
            return lines;
        }

        return ListUtil.append(lines, currentLine);
    }

    @Override
    public String toString() {
        return String.join("\n", getLines());
    }
}
