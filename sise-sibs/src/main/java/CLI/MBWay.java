package CLI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class MBWay {

	private String phoneNumber;
	private String IBAN;
	private Services services;
	private Sibs sibs = new Sibs(100, this.services);
	private static Map<String, String> MBWay = new HashMap<String, String>(); // MBWAY -> phoneNumber, IBAN
	private Tuple<Integer, MBWay> temp; // code, MBWay

	public MBWay() {
	}

	public MBWay(String phoneNumber, String IBAN) {
		this.phoneNumber = phoneNumber;
		this.IBAN = IBAN;
		MBWay.put(phoneNumber, IBAN);
	}

	/*
	 * Associates a number with an IBAN (this serves exclusively to build the DB of
	 * associated numbers, as the current user is assumed to be allready loggedin
	 */
	public int associateMBWay(String phoneNumber, String IBAN) {
		int code = (int) (Math.random() * (999999));
		MBWay newMB = new MBWay(phoneNumber, IBAN);
		this.temp = new Tuple<Integer, MBWay>(code, newMB);
		return code;
	}

	/*
	 * Confirms the last inserted association with an input with the following
	 * style: confirm-mbway <Code>
	 */
	public void confirmMBWay(int code) {
		MBWay.put(this.temp.y.phoneNumber, this.temp.y.IBAN);
		this.temp.x = null;
		this.temp.y = null;
	}

	/* Transfers money from one account to the other */
	public void transferMBWay(String sourceNumber, String targetNumber, int amount)
			throws SibsException, AccountException, OperationException {
		try {
			String sourceIBAN = MBWay.get(sourceNumber);
			String targetIBAN = MBWay.get(targetNumber);
			this.sibs.transfer(sourceIBAN, targetIBAN, amount);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	/*
	 * Performs split bill assuming the following instruction correspondence:
	 * mbway-split-bil <NUMBER_OF_FRIENDS> <AMOUNT> || friend <PHONE_NUMBER>
	 * <AMOUNT> || friend <PHONE_NUMBER> <AMOUNT> || friend <PHONE_NUMBER> <AMOUNT>
	 * || (..) || end
	 *
	 * corresponds to: decisiontest <numberoffriends> <totalbillamount> ||
	 * decisiontest <billowner/target phone number> <amount to be paid by
	 * loggedinuser> || decisiontest <sourceNumber-friend that is paying> <amount to
	 * be paid by current friend> || decisiontest <sourceNumber-friend that is
	 * paying> <amount to be paid by current friend> || (...) || end
	 */
	public void splitBill(List<Tuple<String, Integer>> instructions)
			throws SibsException, AccountException, OperationException {
		String targetNumber = instructions.get(0).x;
		int amountuser1 = instructions.get(0).y;
		transferMBWay(this.phoneNumber, targetNumber, amountuser1);
		for (int i = 1; i < instructions.size(); i++) {
			String sourceNumber = instructions.get(i).x;
			int amountPerUser = instructions.get(i).y;
			transferMBWay(sourceNumber, targetNumber, amountPerUser);
		}
	}

	/* Auxiliary methods: */

	/* ------------------------------------------------------- */

	// Getters & Setters & Verifiers below:

	public String getIBAN() {
		return this.IBAN;
	}

	public void setIBAN(String iBAN) {
		this.IBAN = iBAN;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public int getTempX() {
		return this.temp.x;
	}

	public boolean isNumRegist(String phoneNumber) {
		return MBWay.containsKey(phoneNumber);
	}

	public int getBalanceByIBAN(String IBAN) {
		return this.services.getAccountByIban(IBAN).getBalance();
	}

	public String getIBANByPhoneNumber(String phoneNumber) {
		return MBWay.get(phoneNumber);
	}

}
