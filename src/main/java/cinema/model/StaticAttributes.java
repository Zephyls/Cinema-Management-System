package cinema.model;

import java.math.BigDecimal;

public final class StaticAttributes {
    public static final int MINIMUM_AGE_CHILDREN = 5;
    public static final int MINIMUM_SCREENING_PRICE = 35;
    public static final int MAINTENANCE_PERIOD_DAYS = 30;
    public static final BigDecimal EMPLOYEE_SALARY_GROWTH_RATE = new BigDecimal("0.3");

    private StaticAttributes() {}
}
