package com.Bridge.bridge.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
public class Part {

    private String recruitPart;     // 모집 파트

    private int recruitNum;         //모집인원 수

    private List<String> recruitSkill;    //모집 기술 스택

    private String requirement; // 모집 요건

    public Part(String recruitPart, int recruitNum, List<String> recruitSkill, String requirement) {
        this.recruitPart = recruitPart;
        this.recruitNum = recruitNum;
        this.recruitSkill = recruitSkill;
        this.requirement = requirement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Part)) return false;
        Part part = (Part) o;
        return recruitNum == part.recruitNum && Objects.equals(recruitPart, part.recruitPart) && Objects.equals(recruitSkill, part.recruitSkill);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recruitPart, recruitNum, recruitSkill);
    }

}
