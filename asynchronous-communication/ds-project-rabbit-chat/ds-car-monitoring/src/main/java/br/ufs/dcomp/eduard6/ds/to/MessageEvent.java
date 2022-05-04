package br.ufs.dcomp.eduard6.ds.to;

import java.io.Serializable;
import java.time.LocalDateTime;

public class MessageEvent implements Serializable {
    private String from;
    private String to;
    private String content;
    private LocalDateTime sendDateTime;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSendDateTime() {
        return sendDateTime;
    }

    public void setSendDateTime(LocalDateTime sendDateTime) {
        this.sendDateTime = sendDateTime;
    }

    @Override
    public String toString() {
        return "MessageEvent{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", content='" + content + '\'' +
                ", sendDateTime=" + sendDateTime +
                '}';
    }
}
