package wechat;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.Before;
import org.junit.Test;
import org.matrix.framework.core.collection.converter.JSONConverter;
import org.matrix.framework.core.http.Get;
import org.matrix.framework.core.http.HTTPClient;
import org.matrix.framework.core.http.HTTPResponse;
import org.matrix.framework.core.http.TextPost;
import wechat.menu.IMenu;
import wechat.menu.Menu;
import wechat.menu.NewMenuRequest;
import wechat.menu.SubMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pktczwd on 2016/9/20.
 */
public class WeChatTest {

    private HTTPClient httpClient = new HTTPClient();
    private JSONConverter jsonConverter = new JSONConverter();

    @Before
    public void before() {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        httpClient.setPoolingHttpClientConnectionManager(manager);
        httpClient.initalize();
    }

    /**
     * 根据appID和AppSecret来获取微信的access_token.
     */
    @Test
    public void test0() {
        Get get = new Get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxaf4d5d3875cfcaae&secret=1a42aad2ef8989376469da7e69f093b0");
        HTTPResponse response = httpClient.execute(get);
        System.out.println(response.getStatusCode().getStatusCode());
        System.out.println(response.getResponseText());
    }

    /**
     * 获取微信服务器IP地址
     */
    @Test
    public void test1() {
        Get get = new Get("https://api.weixin.qq.com/cgi-bin/getcallbackip?access_token=bv2Y28WTOly-rE0BlvpiX6PyTYaAgQ4o2BjDEeh2nSRAJxqGxBnKj5gCfXgBR6hVsJVpHYEgUxbrLAD4bFpxvjVCOe45PPQpyP2q29DdAgLxpW2k3z6LIPPz3nZ8IzNGGTVhAFABOQ");
        HTTPResponse response = httpClient.execute(get);
        System.out.println(response.getStatusCode().getStatusCode());
        System.out.println(response.getResponseText());
    }

    /**
     * 创建一级菜单
     */
    @Test
    public void test2() {
        NewMenuRequest request = new NewMenuRequest();
        Menu menu = new Menu();
        menu.setType("click");
        menu.setName("到店自助");
        menu.setKey("SHB_MENU_SELFSERVICE");
        List<IMenu> list = new ArrayList<IMenu>();
        list.add(menu);
        request.setButton(list);

        TextPost post = new TextPost("https://api.weixin.qq.com/cgi-bin/menu/create?access_token=SHvuGFx3RZMgLpx5uVUONt9gS-PoeYl1603AAIGMY4sf-F8XwUndustipGBlb1sjTAgG8bGbwtFrNNW_83ISoacmgfLP0jCfCMGiZEdeLG4SRWeAFAWVA");
        String json = jsonConverter.toString(request);

        System.out.println("json=>" + json);
        post.setBody(json);

        HTTPResponse response = httpClient.execute(post);
        System.out.println(response.getStatusCode().getStatusCode());
        System.out.println(response.getResponseText());
    }

    /**
     * 创建多级菜单
     */
    @Test
    public void test3() {
        NewMenuRequest request = new NewMenuRequest();

        SubMenu menu = new SubMenu();
        menu.setName("到店自助");

        List<IMenu> subMenus = new ArrayList<IMenu>();

        Menu subMenu1 = new Menu();
        subMenu1.setType("click");
        subMenu1.setName("网店查询");
        subMenu1.setKey("SHB_WDCX");

        Menu subMenu2 = new Menu();
        subMenu2.setType("click");
        subMenu2.setName("包裹查询");
        subMenu2.setKey("SHB_BGCX");

        Menu subMenu3 = new Menu();
        subMenu3.setType("click");
        subMenu3.setName("速递易快件查询");
        subMenu3.setKey("SHB_SDY_PACK");

        Menu subMenu4 = new Menu();
        subMenu4.setType("click");
        subMenu4.setName("扫一扫");
        subMenu4.setKey("SHB_SYS");

        subMenus.add(subMenu1);
        subMenus.add(subMenu2);
        subMenus.add(subMenu3);
        subMenus.add(subMenu4);

        menu.setSubButton(subMenus);

        List<IMenu> list = new ArrayList<IMenu>();
        list.add(menu);
        request.setButton(list);


        TextPost post = new TextPost("https://api.weixin.qq.com/cgi-bin/menu/create?access_token=SHvuGFx3RZMgLpx5uVUONt9gS-PoeYl1603AAIGMY4sf-F8XwUndustipGBlb1sjTAgG8bGbwtFrNNW_83ISoacmgfLP0jCfCMGiZEdeLG4SRWeAFAWVA");
        String json = jsonConverter.toString(request);

        System.out.println("json=>" + json);
        post.setBody(json);

        HTTPResponse response = httpClient.execute(post);
        System.out.println(response.getStatusCode().getStatusCode());
        System.out.println(response.getResponseText());
    }

    /**
     * 自定义菜单查询
     */
    @Test
    public void test4() {
        Get get = new Get("https://api.weixin.qq.com/cgi-bin/menu/get?access_token=SHvuGFx3RZMgLpx5uVUONt9gS-PoeYl1603AAIGMY4sf-F8XwUndustipGBlb1sjTAgG8bGbwtFrNNW_83ISoacmgfLP0jCfCMGiZEdeLG4SRWeAFAWVA");
        HTTPResponse response = httpClient.execute(get);
        System.out.println(response.getStatusCode().getStatusCode());
        System.out.println(response.getResponseText());
    }

    /**
     * 删除所有自定义菜单
     */
    @Test
    public void test5() {
        Get get = new Get("https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=SHvuGFx3RZMgLpx5uVUONt9gS-PoeYl1603AAIGMY4sf-F8XwUndustipGBlb1sjTAgG8bGbwtFrNNW_83ISoacmgfLP0jCfCMGiZEdeLG4SRWeAFAWVA");
        HTTPResponse response = httpClient.execute(get);
        System.out.println(response.getStatusCode().getStatusCode());
        System.out.println(response.getResponseText());
    }

}
