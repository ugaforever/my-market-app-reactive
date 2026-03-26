package ru.ugaforever.reactive.market.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO  implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private Double price;
    private String description;
    private String imgPath;
    @Builder.Default
    private Integer count = 0;

    //для шаблонов
    public Long id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public Double price() {
        return price;
    }

    public String imgPath() {
        return imgPath;
    }

}