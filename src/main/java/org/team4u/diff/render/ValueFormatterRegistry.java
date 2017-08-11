package org.team4u.diff.render;

import com.xiaoleilu.hutool.crypto.SecureUtil;
import jetbrick.template.JetEngine;
import jetbrick.template.JetTemplate;
import org.team4u.kit.core.lang.TimeMap;

import java.io.StringWriter;
import java.util.Map;

/**
 * @author Jay Wu
 */
public enum ValueFormatterRegistry {

    INSTANCE;

    private JetEngine engine = JetEngine.create();

    private Map<String, JetTemplate> templates = new TimeMap<>(60);

    /**
     * 根据模板内容渲染模板
     *
     * @param data            原始数据
     * @param templateContent 模板内容
     */
    public void renderWithContent(Map<String, Object> data, String templateContent) {
        String key = SecureUtil.md5(templateContent);

        JetTemplate template = templates.get(key);
        if (template == null) {
            template = engine.createTemplate(templateContent);
            templates.put(key, template);
        } else {
            // 命中则延长缓存有效期
            templates.put(key, template);
        }

        StringWriter sw = new StringWriter();
        template.render(data, sw);
    }

    /**
     * 注册模板扩展方法
     *
     * @param clazz 模板方法扩展类
     */
    public void registerTemplateMethod(Class<?> clazz) {
        engine.getGlobalResolver().registerMethods(clazz);
    }

    /**
     * 注册模板扩展函数
     *
     * @param clazz 模板函数扩展类
     */
    public void registerTemplateFunction(Class<?> clazz) {
        engine.getGlobalResolver().registerFunctions(clazz);
    }
}