package wechat.menu;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by pktczwd on 2016/9/20.
 * 二级菜单
 */
public class SubMenu implements IMenu {

    private String name;
    @JsonProperty("sub_button")
    private List<IMenu> subButton;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<IMenu> getSubButton() {
        return subButton;
    }

    public void setSubButton(List<IMenu> subButton) {
        this.subButton = subButton;
    }
}
