package com.jiring.news;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@Data
public class NewsItem implements Serializable {
    private String headline;
    private int priority;
    @Serial
    private static final long serialVersionUID = 1L;

}