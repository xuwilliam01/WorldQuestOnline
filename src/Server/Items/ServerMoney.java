package Server.Items;

import Server.ServerWorld;

public class ServerMoney extends ServerItem{

	public ServerMoney(double x, double y, int amount) {
		super(x, y, ServerWorld.MONEY_TYPE);
		setAmount(amount);
	}

	public ServerMoney(double x, double y) {
		super(x, y, ServerWorld.MONEY_TYPE);
	}
}
