package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerFields;
import io.swagger.models.Model;
import io.swagger.models.properties.*;
import org.springframework.cloud.contract.spec.internal.DslProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.DefaultValues.*;
import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerTypes.INT_32;
import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerTypes.INT_64;

/**
 * Creates a value for a response header.
 *
 * @author Sven Bayer
 */
public final class ResponseHeaderValueBuilder {

	private ResponseHeaderValueBuilder() {
	}

	/**
	 * Creates a dsl value for a response header property.
	 *
	 * @param key the key of the header
	 * @param property the response header property
	 * @param definitions the Swagger model definition
	 * @return the value for the given response header property
	 */
	public static DslProperty createDslResponseHeaderValue(String key, Property property, Map<String, Model> definitions) {
		Object value = createResponseHeaderValue(key, property, definitions);
		//TODO avoid default values and set the pattern for the corresponding type
		return new DslProperty<>(String.valueOf(value));
	}

	/**
	 * Creates a value for a response header property.
	 *
	 * @param key the key of the header
	 * @param property the response header property
	 * @param definitions the Swagger model definition
	 * @return the value for the given response header property
	 */
	private static Object createResponseHeaderValue(String key, Property property, Map<String, Model> definitions) {
		if (property.getExample() != null) {
			return postFormatNumericValue(property, property.getExample());
		}
		if (property.getVendorExtensions() != null && property.getVendorExtensions().get(SwaggerFields.X_EXAMPLE.field()) != null) {
			return postFormatNumericValue(property, property.getVendorExtensions().get(SwaggerFields.X_EXAMPLE.field()));
		}
		Object defaultValue = getDefaultValue(property);
		if (defaultValue != null) {
			return defaultValue;
		}
		if (property instanceof RefProperty) {
			RefProperty refProperty = RefProperty.class.cast(property);
			return getJsonForPropertiesConstruct(refProperty.get$ref(), definitions);
		}
		if (property instanceof ArrayProperty) {
			ArrayProperty arrayProperty = ArrayProperty.class.cast(property);
			if (arrayProperty.getItems() == null) {
				return new ArrayList<>(Collections.singleton(DEFAULT_INT));
			} else {
				return new ArrayList<>(Collections.singletonList(createResponseHeaderValue(key, arrayProperty.getItems(), definitions)));
			}
		}
		if (property instanceof AbstractNumericProperty) {
			AbstractNumericProperty numeric = (AbstractNumericProperty) property;
			BigDecimal numericPropertyValue = null;
			if (numeric.getMinimum() != null) {
				if (numeric.getExclusiveMinimum()) {
					numericPropertyValue = numeric.getMinimum().add(new BigDecimal(DEFAULT_INT));
				} else {
					numericPropertyValue = numeric.getMinimum();
				}
			}
			if (numeric.getMaximum() != null) {
				if (numeric.getExclusiveMaximum() != null) {
					numericPropertyValue = numeric.getMaximum().subtract(new BigDecimal(DEFAULT_INT));
				} else {
					numericPropertyValue = numeric.getMaximum();
				}
			}
			if (numeric instanceof DoubleProperty || numeric instanceof FloatProperty) {
				if (numericPropertyValue != null) {
					return numericPropertyValue.doubleValue();
				} else {
					return DEFAULT_FLOAT;
				}
			}
			if (numeric instanceof LongProperty || numeric instanceof DecimalProperty
					|| numeric instanceof IntegerProperty || numeric instanceof BaseIntegerProperty) {
				if (numericPropertyValue != null) {
					return numericPropertyValue.longValue();
				} else {
					return DEFAULT_INT;
					//TODO return Pattern.compile("[0-9]+");
				}
			}
			return DEFAULT_INT;
			//TODO return Pattern.compile("[0-9]+");
		}
		if (property instanceof BooleanProperty) {
			return DEFAULT_BOOLEAN;
		}
		if (property instanceof StringProperty) {
			StringProperty stringProperty = StringProperty.class.cast(property);
			if (stringProperty.getEnum() != null) {
				return stringProperty.getEnum().get(0);
			}
		}
		return key;
		//TODO return new MatchingTypeValue(MatchingType.REGEX, ".+");
	}

	/**
	 * Creats a key-value representation for the given reference and Swagger model definitions.
	 *
	 * @param reference the unformatted Swagger reference string
	 * @param definitions the Swagger model definitions
	 * @return a key-value representation of the Swagger model definition
	 */
	static Map<String, Object> getJsonForPropertiesConstruct(String reference, Map<String, Model> definitions) {
		String referenceName = reference.substring(reference.lastIndexOf('/') + 1);
		return definitions.get(referenceName).getProperties().entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> createResponseHeaderValue(entry.getKey(), entry.getValue(), definitions)));
	}

	/**
	 * Formats a numeric property correctly if its value is a double but its format is an int32 or int64.
	 *
	 * @param property the property
	 * @param value the value that could be a double
	 * @return the formatted property
	 */
	private static Object postFormatNumericValue(Property property, Object value) {
		if (property.getFormat() == null) {
			return value;
		}
		if (value instanceof Double && (property.getFormat().equals(INT_32.type()) || property.getFormat().equals(INT_64.type()))) {
			return Double.class.cast(value).intValue();
		}
		return value;
	}

	/**
	 * Returns the property as typed property instance.
	 *
	 * @param property the property
	 * @return the specified typed property or null if not matching subclass is found
	 */
	private static Object getDefaultValue(Property property) {
		if (property instanceof DoubleProperty) {
			return DoubleProperty.class.cast(property).getDefault();
		}
		if (property instanceof FloatProperty) {
			return FloatProperty.class.cast(property).getDefault();
		}
		if (property instanceof LongProperty) {
			return LongProperty.class.cast(property).getDefault();
		}
		if (property instanceof IntegerProperty) {
			return IntegerProperty.class.cast(property).getDefault();
		}
		if (property instanceof BooleanProperty) {
			return BooleanProperty.class.cast(property).getDefault();
		}
		if (property instanceof StringProperty) {
			return StringProperty.class.cast(property).getDefault();
		}
		return null;
	}
}