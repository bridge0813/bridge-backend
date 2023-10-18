package com.Bridge.bridge.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class SearchWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "searchWord_id")
    private Long id;

    private String content;

    private LocalDateTime history;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    private User user;

    @Builder
    public SearchWord(String content, LocalDateTime history, User user) {
        this.content = content;
        this.history = history;
        this.user = user;
    }
}
