package org.matrix.framework.core.json;

import org.junit.Test;
import org.matrix.framework.core.collection.converter.JSONConverter;
import org.matrix.framework.core.page.PageModel;

public class JSONConvertTest {

    @Test
    public void test1() {
        JSONConverter jsonConverter = new JSONConverter();
        System.out.println(jsonConverter.toString("pankai"));
    }

    @Test
    public void test2() {
        JSONConverter jsonConverter = new JSONConverter();
        PageModel<ComplexObject> pageModel = new PageModel<ComplexObject>();
        pageModel.setPageNo(1);
        pageModel.setPageSize(20);
        pageModel.setTotalRecords(100);
        ComplexObject info = new ComplexObject();
        info.setName("pankai");
        info.setAge(25);
        pageModel.setInfo(info);
        System.out.println(jsonConverter.toString(pageModel));
    }
}
