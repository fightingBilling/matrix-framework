package wechat.menu;

/**
 * Created by pktczwd on 2016/9/20.
 * 简单菜单对象
 */
public class Menu implements IMenu {

    private String type;
    private String name;
    private String key;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

}
