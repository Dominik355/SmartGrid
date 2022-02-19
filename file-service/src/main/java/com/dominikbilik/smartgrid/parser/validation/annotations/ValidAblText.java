package com.dominikbilik.smartgrid.parser.validation.annotations;

import com.dominikbilik.smartgrid.parser.utils.ParserUtils;
import com.dominikbilik.smartgrid.parser.validation.AblTextValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AblTextValidator.class)
@Documented
public @interface ValidAblText {
    String message() default "Text is not valid ABL file";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    int minSize() default ParserUtils.ABLUtils.MINIMAL_FILE_SIZE;
}
