package contains;

public enum DmlEnum {

    SELECT_ONE("selectOne"),
    SELECT_LIST("selectList"),
    UPDATE("update"),
    DELETE("delete"),
    INSERT("insert");
    private String name;
    private DmlEnum(String name){
        this.name = name;
    }
    private String getName() {
        return name;
    }


}
