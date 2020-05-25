package com.example.mytranslator;

public class Content {
    int cid;
    String original_content="", translated_content="";

    public Content(String oc, String tc)
    {
        original_content = oc;
        translated_content = tc;
    }

    public Content(String oc)
    {
        original_content = oc;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getOriginal_content() {
        return original_content;
    }

    public void setOriginal_content(String original_content) {
        this.original_content = original_content;
    }

    public String getTranslated_content() {
        return translated_content;
    }

    public void setTranslated_content(String translated_content) {
        this.translated_content = translated_content;
    }
}
