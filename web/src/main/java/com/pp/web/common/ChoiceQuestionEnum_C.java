package com.pp.web.common;

/**
 * Created by asus on 2017/6/20.
 */
public enum ChoiceQuestionEnum_C {
    CHOICE_A("100分","A"),

    CHOICE_B("80分","B"),

    CHOICE_C("60分","C"),

    CHOICE_D("40分","D"),

    CHOICE_E("20分","E");

    private String name;

    private String index;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    ChoiceQuestionEnum_C(String name, String index){
        this.name = name;
        this.index = index;
    }

    //根据索引获取名称
    public static String getName(String index) {
        String name = "";
        for (ChoiceQuestionEnum_C o : ChoiceQuestionEnum_C.values()) {
            if (index .equals( o.getIndex())) {
                name = o.getName();
                break;
            }
        }
        return name;
    }
}
