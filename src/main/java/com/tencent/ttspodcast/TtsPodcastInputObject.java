package com.tencent.ttspodcast;

import com.google.gson.annotations.SerializedName;

public class TtsPodcastInputObject {

    /**
     * 必填：是
     * 输入类型：
     * TYPE_TEXT：文本类型 
     * TYPE_URL：网址类型 
     * TYPE_FILE：文件类型 
     * 注：当类型为文件时，需满足以下条件 
     *   - 文件格式：pdf, .txt, .docx, .md 
     *   - 文件大小：30M
     */
    @SerializedName("ObjectType")
    private String objectType;

    /**
     * 必填：否
     * 文本内容（仅当 ObjectType 设置 TYPE_TEXT 时生效）
     */
    @SerializedName("Text")
    private String text;

    /**
     * 必填：否
     * 网址内容（仅当 ObjectType 设置 TYPE_URL、TYPE_FILE 时生效）
     * TYPE_URL时，为网页地址
     * TYPE_FILE时，为文件地址
     */
    @SerializedName("Url")
    private String url;

    /**
     * 必填：否
     * 文件格式（仅当 ObjectType 设置 TYPE_FILE 时生效）
     * 格式取值：pdf, .txt, .docx, .md
     */
    @SerializedName("FileFormat")
    private String fileFormat;

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
