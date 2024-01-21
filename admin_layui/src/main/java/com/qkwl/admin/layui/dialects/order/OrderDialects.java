package com.qkwl.admin.layui.dialects.order;

import java.util.HashSet;
import java.util.Set;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

/**
 * 排序方言
 */
public class OrderDialects extends AbstractProcessorDialect {
  

    /**
   * @param name
   * @param prefix
   * @param processorPrecedence
   */
  public OrderDialects() {
    super("Order Dialect", "order", StandardDialect.PROCESSOR_PRECEDENCE);
  }


    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
      final Set<IProcessor> processors = new HashSet<>();
      processors.add(new FieldAttrProcessor(dialectPrefix));
      return processors;
    }
    
}
