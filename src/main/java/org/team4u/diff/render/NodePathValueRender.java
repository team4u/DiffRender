package org.team4u.diff.render;

import com.xiaoleilu.hutool.convert.Convert;
import com.xiaoleilu.hutool.lang.Validator;
import com.xiaoleilu.hutool.util.BeanUtil;
import com.xiaoleilu.hutool.util.StrUtil;
import org.team4u.diff.definiton.Renderable;

import java.util.List;

/**
 * @author Jay Wu
 */
public class NodePathValueRender {

    public static String render(Object oldBean, String path) {
        Object pathBean = BeanUtil.getProperty(oldBean, convertToBeanPath(path));

        if (pathBean instanceof Renderable) {
            return ((Renderable) pathBean).renderKeyValue();
        }

        return Convert.toStr(pathBean);
    }

    /**
     * ClassName/A/#B/0/C => A.B[0].C
     */
    private static String convertToBeanPath(String path) {
        StringBuilder builder = new StringBuilder();
        List<String> paths = StrUtil.split(path, '/', true, true);

        for (int i = 1; i < paths.size(); i++) {
            String key = paths.get(i);
            boolean isRoot = i == 1;

            // 集合
            if (key.startsWith("#")) {
                key = key.substring(1);
                if (!isRoot) {
                    builder.append(".");
                }

                builder.append(key);
                continue;
            }

            // 集合内某元素
            if (Validator.isNumber(key)) {
                builder.append("[").append(key).append("]");
                continue;
            }

            if (!isRoot) {
                builder.append(".");
            }
            builder.append(key);
        }

        return builder.toString();
    }
}