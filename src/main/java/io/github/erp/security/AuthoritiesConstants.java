package io.github.erp.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static final String DEV = "ROLE_DEV";

    public static final String DBA = "ROLE_DBA";

    public static final String BOOK_KEEPING = "ROLE_BOOK_KEEPING";

    public static final String PAYMENTS_USER = "ROLE_PAYMENTS_USER";

    public static final String FIXED_ASSETS_USER = "ROLE_FIXED_ASSETS_USER";

    public static final String TAX_MODULE_USER = "ROLE_TAX_MODULE_USER";

    public static final String PREPAYMENTS_MODULE_USER = "ROLE_PREPAYMENTS_MODULE_USER";

    public static final String GRANULAR_REPORTS_USER = "ROLE_GRANULAR_REPORTS_USER";

    private AuthoritiesConstants() {}
}
