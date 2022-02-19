package com.dominikbilik.smartgrid.parser.validation;

import com.dominikbilik.smartgrid.parser.validation.annotations.ValidAblText;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class AblTextValidator implements ConstraintValidator<ValidAblText, List<String>> {

    private int minSize;

    @Override
    public void initialize(ValidAblText constraintAnnotation) {
        this.minSize = minSize;
    }

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        return !(value == null || value.isEmpty() || value.size() < minSize);
    }
}
