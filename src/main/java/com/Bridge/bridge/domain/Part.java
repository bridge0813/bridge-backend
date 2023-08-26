package com.Bridge.bridge.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
public class Part {

    private int recruitNum;         //모집인원 수

    private String recruitSkill;    //모집 분야

    private String requirement; // 모집 요건

    public Part(int recruitNum, String recruitSkill, String requirement) {
        this.recruitNum = recruitNum;
        this.recruitSkill = recruitSkill;
        this.requirement = requirement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Part)) return false;
        Part part = (Part) o;
        return recruitNum == part.recruitNum && Objects.equals(recruitSkill, part.recruitSkill) && Objects.equals(requirement, part.requirement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recruitNum, recruitSkill, requirement);
    }
}
