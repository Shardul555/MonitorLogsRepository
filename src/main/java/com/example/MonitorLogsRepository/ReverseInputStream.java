package com.example.MonitorLogsRepository;

import java.io.*;
import java.util.*;

public class ReverseInputStream extends InputStream {
    private final RandomAccessFile file;
    private Long endOfFile;
    public ReverseInputStream(File RandomTextFile, Long currLength) throws IOException {
        this.file = new RandomAccessFile(RandomTextFile, "rw");
        this.file.seek(currLength);
        this.endOfFile = currLength;
    }
    @Override
    public int read() {
        return 1;
    }

    public String pushLastLines() throws IOException {
        StringBuilder line = new StringBuilder();
        Stack<String>stringBuilderStack = new Stack<>();
        int count=0;
        for (long pointer = this.file.length()-1; pointer >= endOfFile; pointer--) {
            file.seek(pointer);
            char c = (char) this.file.readByte();
            if (c == '\n' || pointer == 0) {
                if (pointer != 0) {
                    line.reverse();
                } else {
                    line.append(c);
                    line.reverse();
                }
                stringBuilderStack.push(String.valueOf(line));
                line.setLength(0);
                count++;
                if (count == 10 && endOfFile == 0L) {
                    break;
                }
            } else {
                line.append(c);
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        while(!stringBuilderStack.empty()) {
            stringBuilder.insert(stringBuilder.length(), stringBuilderStack.peek());
            stringBuilder.append(System.lineSeparator());
            WebSocketMessageService.lastLogs.add(stringBuilderStack.peek());
            stringBuilderStack.pop();
        }
        while(WebSocketMessageService.lastLogs.size()>10) {
            WebSocketMessageService.lastLogs.pop();
        }
        return stringBuilder.toString();
    }
}

