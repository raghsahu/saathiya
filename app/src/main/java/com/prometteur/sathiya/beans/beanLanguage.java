package com.prometteur.sathiya.beans;

/**
 * Created by Ravi on 10/24/2016.
 */
public class beanLanguage
{
    String lang_id,language,lang_code;

    public beanLanguage(String lang_id, String language, String lang_code) {
        this.lang_id = lang_id;
        this.language = language;
        this.lang_code = lang_code;
    }


    public String getLang_id() {
        return lang_id;
    }

    public void setLang_id(String lang_id) {
        this.lang_id = lang_id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLang_code() {
        return lang_code;
    }

    public void setLang_code(String lang_code) {
        this.lang_code = lang_code;
    }
}
