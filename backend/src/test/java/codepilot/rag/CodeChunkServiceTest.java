package codepilot.rag;

import org.junit.jupiter.api.Test;

import codepilot.rag.CodeChunkService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class CodeChunkServiceTest {

    private final CodeChunkService codeChunkService = new CodeChunkService();

    @Test
    void shortContentProducesExactlyOneChunk() {
        String content = "public class Foo {\n    // small file\n}";

        List<String> chunks = codeChunkService.chunk(content);

        assertThat(chunks).hasSize(1);
        assertThat(chunks.get(0)).isEqualTo(content);
    }

    @Test
    void longContentIsSplitIntoMultipleOverlappingChunks() {
        // 150 lines, well over the 60-line chunk size, to force multiple chunks
        String content = IntStream.rangeClosed(1, 150)
                .mapToObj(i -> "line " + i)
                .collect(Collectors.joining("\n"));

        List<String> chunks = codeChunkService.chunk(content);

        assertThat(chunks.size()).isGreaterThan(1);
        assertThat(chunks.get(0)).contains("line 1");
        assertThat(chunks.get(chunks.size() - 1)).contains("line 150");
    }
}
