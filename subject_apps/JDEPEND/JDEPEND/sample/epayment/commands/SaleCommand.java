package sample.epayment.commands;

import sample.epayment.framework.*;

/**
 * The <code>SaleCommand</code> class is an
 * <code>AbstractPaymentCommand</code> for authorizing
 * and capturing a purchase in an
 * atomic operation.
 * <p>
 * This class is strictly an example.
 *
 * @author <a href="mailto:mike@clarkware.com">Mike Clark</a>
 * @author <a href="http://www.clarkware.com">Clarkware Consulting</a>
 */

public class SaleCommand extends AbstractPaymentCommand {

	/**
	 * Constructs an <code>SaleCommand</code> with
	 * the specified payment request.
	 *
	 * @param request Payment request.
	 */
	public SaleCommand(IPaymentRequest request) {
		super(request);
	}
	
	/**
	 * Executes this command using the specified payment 
	 * adapter and returns a payment response.
	 *
	 * @param adapter Payment adapter.
	 * @return response Payment response.
	 * @throws PaymentException If an error occurs.
	 */
	public IPaymentResponse execute(IGatewayAdapter adapter)
		throws PaymentException {
		
		return adapter.sale(getPaymentRequest());
	}
}
