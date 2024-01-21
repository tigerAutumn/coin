package com.qkwl.admin.layui.dialects.order;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.WebEngineContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * 排序attr解析
 */
public class FieldAttrProcessor extends AbstractAttributeTagProcessor {
  
  private static final String ATTR_NAME = "Field";

    
    
    public FieldAttrProcessor(final String dialectPrefix) {
      super(TemplateMode.HTML,
              dialectPrefix,
              null,
              true,
              ATTR_NAME,
              false,
              StandardDialect.PROCESSOR_PRECEDENCE, true);
  }
    
    
    

//    @Override
//    protected Map<String, String> getModifiedAttributeValues(Arguments arguments, Element element, String attributeName) {
//        String attributeValue = element.getAttributeValue(attributeName);
//        Map<String, Object> contextVariables = arguments.getContext().getVariables();
//        // 获取参数并解析
//        Object param = contextVariables.get("param");
//        String orderDirection = "";
//        if (param != null) {
//            Map<String, String[]> paramMap = (Map<String, String[]>) param;
//            if (paramMap.get("orderDirection") != null && paramMap.get("orderField") != null && paramMap.get("orderField")[0].toString().equals(attributeValue)) {
//                orderDirection = paramMap.get("orderDirection")[0].toString();
//            }
//        }
//        Map<String, String> values = new HashMap<>();
//        values.put("orderField", attributeValue);
//        values.put("orderDirection", orderDirection);
//        return values;
//    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag,
        AttributeName attributeName, String attributeValue,
        IElementTagStructureHandler structureHandler) {
      
      Set<String> variableNames = context.getVariableNames();
      
      // 获取参数并解析
      Object param = context.getVariable("param");
      String orderDirection = "";
      if (param != null) {
          Map<String, String[]> paramMap = (Map<String, String[]>) param;
          if (paramMap.get("orderDirection") != null && paramMap.get("orderField") != null && paramMap.get("orderField")[0].toString().equals(attributeValue)) {
              orderDirection = paramMap.get("orderDirection")[0].toString();
          }
      }
      Map<String, Object> values = new HashMap<>();
      values.put("orderField", attributeValue);
      values.put("orderDirection", orderDirection);
      
      WebEngineContext ctx = (WebEngineContext) context;
      ctx.setVariables(values);
    }
}
