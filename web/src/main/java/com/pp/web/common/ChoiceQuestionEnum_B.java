package com.pp.web.common;

/**
 * Created by asus on 2017/6/20.
 */
public enum ChoiceQuestionEnum_B {
    CHOICE_A("非常赞同","A"),

    CHOICE_B("赞同","B"),

    CHOICE_C("一般","C"),

    CHOICE_D("不赞同","D"),

    CHOICE_E("非常不赞同","E");

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

    ChoiceQuestionEnum_B(String name, String index){
        this.name = name;
        this.index = index;
    }

    //根据索引获取名称
    public static String getName(String index) {
        String name = "";
        for (ChoiceQuestionEnum_B o : ChoiceQuestionEnum_B.values()) {
            if (index .equals(o.getIndex()) ) {
                name = o.getName();
                break;
            }
        }
        return name;
    }
}
