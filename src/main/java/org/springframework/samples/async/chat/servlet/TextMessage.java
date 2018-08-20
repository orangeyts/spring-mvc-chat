package org.springframework.samples.async.chat.servlet;

public final class TextMessage {

    private final int userId;
    private final String text;

    public TextMessage(final int userId, final String text) {
        super();
        this.userId = userId;
        this.text = text;
    }
    public int getUserId() {
        return userId;
    }
    public String getText() {
        return text;
    }
}
