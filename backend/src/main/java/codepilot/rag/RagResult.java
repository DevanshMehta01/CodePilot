package codepilot.rag;

import java.util.List;

import codepilot.dto.chat.SourceReference;

public record RagResult(String answer, List<SourceReference> sources) {
}
