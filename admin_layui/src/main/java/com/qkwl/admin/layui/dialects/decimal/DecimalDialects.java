package com.qkwl.admin.layui.dialects.decimal;


import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

/**
 * 自定义执行变量-格式化BigDecimal
 */
public class DecimalDialects extends AbstractDialect implements IExpressionObjectDialect {

  private final IExpressionObjectFactory EXPRESSION_OBJECTS_FACTORY = new DecimalExpressionFactory();

  public DecimalDialects() {
    super("Decimal Dialects");
  }



    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
      return this.EXPRESSION_OBJECTS_FACTORY;
    }
}
