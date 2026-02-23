package com.ecopilot.article.strategy;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class StandardPriceStrategy implements PriceStrategy {
    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice) {
        // Standard logic: just return the base price, or apply standard margin
        // For demonstration, we assume basePrice is the 'prix d'achat' and we return it as is or calculate something.
        if (basePrice == null) return BigDecimal.ZERO;
        return basePrice; 
    }
}
