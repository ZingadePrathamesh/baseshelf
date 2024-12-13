package com.baseshelf.order;

import java.time.LocalDate;

public record ProductOrderFilter(
        LocalDate from,  LocalDate to,
        Float lessThanAmount, Float greaterThanAmount,
        Integer lessThanItem, Integer greaterThanItem
) {
}
