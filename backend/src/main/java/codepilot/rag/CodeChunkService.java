package codepilot.rag;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CodeChunkService {

    private static final int LINES_PER_CHUNK = 60;
    private static final int OVERLAP_LINES = 10;

  
    public List<String> chunk(String content) {
        String[] lines = content.split("\n", -1);
        List<String> chunks = new ArrayList<>();

        if (lines.length <= LINES_PER_CHUNK) {
            chunks.add(content);
            return chunks;
        }

        int start = 0;
        while (start < lines.length) {
            int end = Math.min(start + LINES_PER_CHUNK, lines.length);
            chunks.add(String.join("\n", Arrays.copyOfRange(lines, start, end)));

            if (end == lines.length) {
                break;
            }
            start = end - OVERLAP_LINES;
        }

        return chunks;
    }
}
