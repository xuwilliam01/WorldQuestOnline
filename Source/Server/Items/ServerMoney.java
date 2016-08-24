package Server.Items;

import Server.ServerWorld;

/**
 * Money for the player
 * @author Alex Raita & William Xu
 *
 */
public class ServerMoney extends ServerItem{

	/**
	 * Constructor for money
	 * @param amount the amount of money
	 */
	public ServerMoney(double x, double y, int amount) {
		super(x, y, ServerWorld.MONEY_TYPE);
		setAmount(amount);
	}

	/**
	 * Constructor for money
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public ServerMoney(double x, double y) {
		super(x, y, ServerWorld.MONEY_TYPE);
	}

	@Override
	public void update()
	{
		// TODO Auto-generated method stub
		
	}
}
