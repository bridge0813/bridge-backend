package com.Bridge.bridge.dto.response;

import com.Bridge.bridge.domain.Field;
import com.Bridge.bridge.domain.FieldAndStack;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class FieldAndStackResponse {

    private String field;

    private List<String> stacks;

    private FieldAndStackResponse(String field, List<String> stacks) {
        this.field = field;
        this.stacks = stacks;
    }

    public static FieldAndStackResponse from(FieldAndStack fieldAndStack) {
       String fieldVal = fieldAndStack.getField().getValue();
        List<String> stackVal = fieldAndStack.getStacks().stream()
                .map(s -> s.getValue())
                .collect(Collectors.toList());

        return new FieldAndStackResponse(fieldVal, stackVal);
    }
}
