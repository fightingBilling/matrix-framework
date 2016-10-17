package org.matrix.framework.core.json;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class JSONBinderTest {

    @SuppressWarnings("unchecked")
    @Test
    public void test1() {
        List<ComplexObject> complexObjects = new ArrayList<ComplexObject>();
        ComplexObject object = new ComplexObject();
        object.setName("pankai");
        object.setAge(25);
        complexObjects.add(object);
        String json = JSONBinder.binder(List.class, ComplexObject.class).toJSON(complexObjects);
        List<ComplexObject> complexObjectList = JSONBinder.binder(List.class, ComplexObject.class).fromJSON(json);
        for (ComplexObject complexObject : complexObjectList) {
            Assert.assertEquals("pankai", complexObject.getName());
            Assert.assertEquals(25, complexObject.getAge());
        }
    }

    @Test
    public void test2() {
        ComplexObject complexObject = new ComplexObject();
        complexObject.setName("pankai");
        complexObject.setAge(25);
        String json = JSONBinder.binder(ComplexObject.class).toJSON(complexObject);
        ComplexObject object = JSONBinder.binder(ComplexObject.class).fromJSON(json);
        Assert.assertEquals("pankai", object.getName());
        Assert.assertEquals(25, object.getAge());
    }

    @Test
    public void test3() {
        String str = JSONBinder.binder(String.class).toJSON("pankai");
        System.out.println(str);
        System.out.println(JSONBinder.binder(String.class).fromJSON(str));
    }
}
