package wechat.menu;

import java.util.List;

/**
 * Created by pktczwd on 2016/9/20.
 */
public class NewMenuRequest {

    private List<IMenu> button;
    
    public List<IMenu> getButton() {
        return button;
    }

    public void setButton(List<IMenu> button) {
        this.button = button;
    }
}
