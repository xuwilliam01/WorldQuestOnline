package Server.Items;

import Server.ServerWorld;

public class ServerHPPotion extends ServerPotion{

	public final static int HEAL_AMOUNT = 50;

	public ServerHPPotion(double x, double y)
	{
		super(x,y,ServerWorld.HP_POTION_TYPE);
	//	this.healAmount = Integer.parseInt(getType().substring(3));
	}
	
	
}
