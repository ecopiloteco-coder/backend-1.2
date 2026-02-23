package com.ecopilot.article.strategy;

import java.math.BigDecimal;

public interface PriceStrategy {
    BigDecimal calculatePrice(BigDecimal basePrice);
}
