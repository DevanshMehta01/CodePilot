package codepilot.dto.chat;

import java.util.List;

public class ChatResponse {

    private final String answer;
    private final List<SourceReference> sources;

    public ChatResponse(String answer, List<SourceReference> sources) {
        this.answer = answer;
        this.sources = sources;
    }

    public String getAnswer() {
        return answer;
    }

    public List<SourceReference> getSources() {
        return sources;
    }
}
