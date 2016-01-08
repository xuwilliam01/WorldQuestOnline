package Server.Items;

import Server.ServerWorld;

public class ServerHPPotion extends ServerPotion{

	private int healAmount = 50;

	public ServerHPPotion(double x, double y)
	{
		super(x,y,ServerWorld.HP_50);
	//	this.healAmount = Integer.parseInt(getType().substring(3));
	}
	
	public int getHealAmount()
	{
		return healAmount;
	}
}
