package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.DefaultValues;
import io.swagger.models.parameters.AbstractSerializableParameter;
import org.springframework.cloud.contract.spec.internal.DslProperty;

import java.util.regex.Pattern;

import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerFields.X_EXAMPLE;

/**
 * Creates values for query and header parameters.
 *
 * @author Sven Bayer
 */
public final class DslValueBuilder {

	private PatternBuilder patternBuilder = new PatternBuilder();
	private DefaultValues defaultValues = new DefaultValues();

	/**
	 * Creates a dsl value for a query or header parameter.
	 *
	 * @param param the query or header parameter
	 * @return the dsl value
	 */
	public DslProperty<Object> createDslValueForParameter(AbstractSerializableParameter param) {
		if (param.getVendorExtensions() != null) {
			Object ignore = param.getVendorExtensions().get("x-ignore");
			if (ignore != null && Boolean.valueOf(ignore.toString())) {
				if (param.getRequired()) {
					throw new SwaggerContractConverterException("Set the parameter '" + param.getName() + "' to required: false to use x-ignore: true");
				}
				return null;
			}
		}
		Object value = createServerValueForParameter(param);
		String type = param.getType();
		String format = param.getFormat();
		String name = param.getName();
		if (value == null) {
			value = this.defaultValues.createDefaultValueForType(type, format, name, param.getMinimum(), param.getMaximum());
		}
		Pattern pattern;
		if (param.pattern != null) {
			pattern = Pattern.compile(param.pattern);
		} else {
			pattern = this.patternBuilder.createPatternForParameter(type, format);
		}
		boolean isPatternMatches = pattern.matcher(String.valueOf(value)).matches();
		if (!isPatternMatches) {
			throw new SwaggerContractConverterException("The pattern '" + pattern.pattern() + "' does not match for the value '"
					+ value + "' for the given param '" + param.getName() + "'");
		}
		return new DslProperty<>(pattern, value);
	}

	/**
	 * Tries to extract the example value from a parameter.
	 *
	 * @param param the parameter
	 * @return the example value
	 */
	private Object createServerValueForParameter(AbstractSerializableParameter param) {
		if (param.getExample() != null) {
			return param.getExample();
		}
		if (param.getVendorExtensions() != null && param.getVendorExtensions().get(X_EXAMPLE.field()) != null) {
			return param.getVendorExtensions().get(X_EXAMPLE.field());
		}
		if (param.getDefaultValue() != null) {
			return param.getDefaultValue();
		}
		if (param.getEnum() != null && param.getEnum().get(0) != null) {
			return param.getEnum().get(0);
		}
		return null;
	}
}
