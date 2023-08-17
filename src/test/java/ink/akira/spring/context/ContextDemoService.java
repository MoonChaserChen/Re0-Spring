package ink.akira.spring.context;

import org.springframework.context.MessageSource;

import java.util.Locale;

public class ContextDemoService {
    private MessageSource messages;

    public MessageSource getMessages() {
        return messages;
    }

    public void setMessages(MessageSource messages) {
        this.messages = messages;
    }

    public void checkParam(String checkParam) {
        checkParam(checkParam, Locale.getDefault());
    }

    public void checkParam(String checkParam, Locale locale) {
        if (checkParam == null) {
            String message = this.messages.getMessage("argument.required", new Object [] {"checkParam"}, locale);
            System.out.println(message);
        }
    }
}
