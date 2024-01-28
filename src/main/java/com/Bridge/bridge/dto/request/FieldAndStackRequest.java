package com.Bridge.bridge.dto.request;

import com.Bridge.bridge.domain.Field;
import com.Bridge.bridge.domain.FieldAndStack;
import com.Bridge.bridge.domain.Stack;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldAndStackRequest {

    private String field;

    private List<String> stacks;

    public FieldAndStack toEntity() {

        List<Stack> stacks = new ArrayList<>();
        this.stacks.stream()
                .forEach(s -> stacks.add(Stack.valueOf(s)));

        return FieldAndStack.builder()
                .field(Field.valueOf(field))
                .stacks(stacks).build();
    }
}
