package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.PerformanceRateProportionMeasureDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ProblemCode;

/**
 * Validates the QRDA Category III Performance Rate Proportion Measure for the cpc+ program
 */
@Validator(value = TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE)
public class PerformanceRateValidator extends NodeValidator {

	public static final String NULL_ATTRIBUTE = "NA";

	/**
	 * Validates that the node given contains a value in range of 0-1 or null attribute
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void performValidation(Node node) {
		if (!NULL_ATTRIBUTE.equals(node.getValue(PerformanceRateProportionMeasureDecoder.NULL_PERFORMANCE_RATE))) {

			Checker checker = checkErrors(node)
				.valueIsNotEmpty(ProblemCode.PERFORMANCE_RATE_MISSING, PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE);

			if (!checker.shouldShortcut()) {
				String performanceRate = node.getValue(PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE);

				checkErrors(node)
					.inDecimalRangeOf(ProblemCode.PERFORMANCE_RATE_INVALID_VALUE.format(performanceRate),
						PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE, 0F, 1F);
			}
		}
	}
}
