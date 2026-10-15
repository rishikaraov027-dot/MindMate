package com.mindmate.dto;

public class AiReflectionResponse {
    private String title;
    private String text;
    private String signal;
    private String signalClass;
    private String tipTitle;
    private String tipBody;
    private boolean aiGenerated;

    public AiReflectionResponse() {}

    public AiReflectionResponse(String title, String text, String signal, String signalClass, String tipTitle, String tipBody, boolean aiGenerated) {
        this.title = title;
        this.text = text;
        this.signal = signal;
        this.signalClass = signalClass;
        this.tipTitle = tipTitle;
        this.tipBody = tipBody;
        this.aiGenerated = aiGenerated;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getSignal() { return signal; }
    public void setSignal(String signal) { this.signal = signal; }

    public String getSignalClass() { return signalClass; }
    public void setSignalClass(String signalClass) { this.signalClass = signalClass; }

    public String getTipTitle() { return tipTitle; }
    public void setTipTitle(String tipTitle) { this.tipTitle = tipTitle; }

    public String getTipBody() { return tipBody; }
    public void setTipBody(String tipBody) { this.tipBody = tipBody; }

    public boolean isAiGenerated() { return aiGenerated; }
    public void setAiGenerated(boolean aiGenerated) { this.aiGenerated = aiGenerated; }
}
