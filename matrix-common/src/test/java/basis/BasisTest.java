package basis;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by pktczwd on 2016/10/18.
 */
public class BasisTest {

    @Test
    public void test0() {
        Integer a = 150;
        Integer b = 100;
        System.out.println((float) a / b);
    }

    @Test
    public void test1() throws ParseException {
        String target = "2016-10-16T17:05:44.000+08:00";
        System.out.println(target.replace("+08:00", "+0800"));
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        System.out.println(format.format(originalFormat.parse(target.replace("+08:00", "+0800"))));
    }

}
