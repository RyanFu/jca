package controllers;

import libs.EasyMap;
import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
        List<Map> items = new ArrayList<Map>();
        items.add(new EasyMap("_id", 1).easyPut("name", "测试1").easyPut("cover", "http://img1.mobile.360.cn/c0/baibian/themes/images/origin/cover_resources_177521649_1358999146167.jpg"));
        items.add(new EasyMap("_id", 2).easyPut("name", "测试2").easyPut("cover", "http://img1.mobile.360.cn/c0/baibian/themes/images/origin/cover_resources_133024387_1358834665247.jpg"));
        items.add(new EasyMap("_id", 3).easyPut("name", "测试3").easyPut("cover", "http://img1.mobile.360.cn/c0/baibian/themes/images/origin/cover_resources_177521649_1358493675146.jpg"));
        items.add(new EasyMap("_id", 4).easyPut("name", "测试4").easyPut("cover", "http://img1.mobile.360.cn/c0/baibian/themes/images/origin/cover_resources_177521649_1358488672744.jpg"));
        items.add(new EasyMap("_id", 5).easyPut("name", "测试5").easyPut("cover", "http://img1.mobile.360.cn/c0/baibian/themes/images/origin/cover_resources_177521649_1358483273342.jpg"));
        items.add(new EasyMap("_id", 6).easyPut("name", "测试6").easyPut("cover", "http://img1.mobile.360.cn/c0//baibian/themes/images/origin/cover_resources_177521649_1356601379472.jpg"));
        items.add(new EasyMap("_id", 7).easyPut("name", "测试7").easyPut("cover", "http://img1.mobile.360.cn/c0//baibian/themes/images/origin/cover_resources_177521649_1356591504000.jpg"));
        items.add(new EasyMap("_id", 8).easyPut("name", "测试8").easyPut("cover", "http://img1.mobile.360.cn/c0//baibian/themes/images/origin/cover_resources_177521649_1356588582748.jpg"));
        items.add(new EasyMap("_id", 9).easyPut("name", "测试9").easyPut("cover", "http://img1.mobile.360.cn/c0//baibian/themes/images/origin/cover_resources_177521649_1356586966492.jpg"));
        items.add(new EasyMap("_id", 10).easyPut("name", "测试10").easyPut("cover", "http://img1.mobile.360.cn/c0/baibian/themes/images/origin/cover_resources_177521649_1359343464974.jpg"));
        render(items);
    }

}