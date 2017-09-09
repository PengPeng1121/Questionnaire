package com.pp.web.common;

/**
 * Created by asus on 2017/6/20.
 */
public enum ChoiceQuestionEnum_A {
    CHOICE_A("非常满意","A"),

    CHOICE_B("满意","B"),

    CHOICE_C("一般","C"),

    CHOICE_D("不满意","D"),

    CHOICE_E("非常不满意","E");

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

    ChoiceQuestionEnum_A(String name, String index){
        this.name = name;
        this.index = index;
    }

    //根据索引获取名称
    public static String getName(String index) {
        String name = "";
        for (ChoiceQuestionEnum_A o : ChoiceQuestionEnum_A.values()) {
            if (index .equals( o.getIndex())) {
                name = o.getName();
                break;
            }
        }
        return name;
    }
}
