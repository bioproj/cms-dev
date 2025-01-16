package com.wangyang.common.thymeleaf;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.dialect.IExecutionAttributeDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.spring5.processor.*;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.expression.*;
import org.thymeleaf.standard.processor.*;
import org.thymeleaf.standard.serializer.*;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

import java.util.*;

public class TestStandardDialect extends StandardDialect {

    public TestStandardDialect(){
        super("Standard2", "cms", 1000);
    }
    public Map<String, Object> getExecutionAttributes() {

        final Map<String,Object> executionAttributes = new HashMap<String, Object>(5, 1.0f);


        return executionAttributes;

    }

    public Set<IProcessor> getProcessors(String dialectPrefix) {
        return createSpringStandardProcessorsSet(dialectPrefix);
    }

    public  Set<IProcessor> createSpringStandardProcessorsSet(String dialectPrefix){
        Set<IProcessor> processors = super.createStandardProcessorsSet(dialectPrefix);
//        Set<IProcessor> processors = new LinkedHashSet(40);
        processors.add(new WebReplaceTagProcessor(TemplateMode.HTML, dialectPrefix) );

//
//        processors.add(new StandardTextTagProcessor(TemplateMode.HTML, dialectPrefix));
//        processors.add(new StandardValueTagProcessor(dialectPrefix));
////       processors.add(new StandardReplaceTagProcessor(TemplateMode.HTML, dialectPrefix));
////        processors.add(new StandardReplaceTagProcessor(TemplateMode.HTML, dialectPrefix));
//        processors.add(new StandardIfTagProcessor(TemplateMode.HTML, dialectPrefix));
//        processors.add(new StandardFragmentTagProcessor(TemplateMode.HTML, dialectPrefix));
//        processors.add(new StandardInlineHTMLTagProcessor(dialectPrefix));
//        processors.add(new IncludeElementTagProcessor(dialectPrefix));
////
        return processors;

    }
}
