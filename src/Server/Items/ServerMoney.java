package Server.Items;

import Server.ServerWorld;

/**
 * Money for the player
 * @author Alex Raita & William Xu
 *
 */
public class ServerMoney extends ServerItem{

	/**
	 * Constructor
	 * @param amount the amount of money
	 */
	public ServerMoney(double x, double y, int amount) {
		super(x, y, ServerWorld.MONEY_TYPE);
		setAmount(amount);
	}

	/**
	 * Constructor
	 * @param x
	 * @param y
	 */
	public ServerMoney(double x, double y) {
		super(x, y, ServerWorld.MONEY_TYPE);
	}
}
