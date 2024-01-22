package com.Bridge.bridge.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class FieldAndStack {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "field_and_stack_id")
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private Field field;

    @ElementCollection
    private List<Stack> stacks = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @Builder
    public FieldAndStack(Field field, List<Stack> stacks) {
        this.field = field;
        this.stacks = stacks;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
