package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.EnumSet;
import java.util.Map;

import static nl.han.ica.icss.ast.types.ExpressionType.*;

/**
 * Defines the allowed {@link ExpressionType}s for supported CSS properties.
 *
 * <p>This class acts as a stateless lookup utility used by the checker to validate
 * whether a declaration assigns a compatible value type to a property.</p>
 *
 * <p>If a property name is not supported, {@link #allowedTypesFor(String)} returns
 * {@code null}.</p>
 */
public final class PropertyRules {
    private static final Map<String, EnumSet<ExpressionType>> ALLOWED_TYPES = Map.of(
            "width", EnumSet.of(PERCENTAGE, PIXEL),
            "height", EnumSet.of(PIXEL, PERCENTAGE),
            "color", EnumSet.of(COLOR),
            "background-color", EnumSet.of(COLOR)
    );

    private PropertyRules(){}

    public static EnumSet<ExpressionType> allowedTypesFor(String propertyName) {
        return ALLOWED_TYPES.get(propertyName);
    }
}
