package com.qkwl.admin.layui.dialects.decimal;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.expression.IExpressionObjectFactory;
import com.qkwl.admin.layui.dialects.decimal.util.DecimalUtils;

public class DecimalExpressionFactory implements IExpressionObjectFactory {
  
  public static final String DECIMAL_UTILS_EXPRESSION_OBJECT_NAME = "decimal";

  private static final DecimalUtils decimalUtils = new DecimalUtils();


  public static final Set<String> ALL_EXPRESSION_OBJECT_NAMES;


  static {

      final Set<String> allExpressionObjectNames = new LinkedHashSet<String>();
      allExpressionObjectNames.add(DECIMAL_UTILS_EXPRESSION_OBJECT_NAME);
      ALL_EXPRESSION_OBJECT_NAMES = Collections.unmodifiableSet(allExpressionObjectNames);

  }

  @Override
  public Set<String> getAllExpressionObjectNames() {
    return ALL_EXPRESSION_OBJECT_NAMES;
  }

  @Override
  public Object buildObject(IExpressionContext context, String expressionObjectName) {
    return DECIMAL_UTILS_EXPRESSION_OBJECT_NAME.equals(expressionObjectName) ? decimalUtils : null;
  }

  @Override
  public boolean isCacheable(String expressionObjectName) {
    return expressionObjectName != null && "decimal".equals(expressionObjectName);
  }
}