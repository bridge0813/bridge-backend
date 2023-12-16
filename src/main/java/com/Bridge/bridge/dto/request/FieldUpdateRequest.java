package com.Bridge.bridge.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldUpdateRequest {

    private Long userId;

    private List<String> fields;

}
