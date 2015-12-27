package Server;

public class ServerHPPotion extends ServerPotion{

	private int healAmount;
	
	public ServerHPPotion(double x, double y,int healAmount)
	{
		super(x,y,"IPH");
		this.healAmount = healAmount;
	}

	public ServerHPPotion(double x, double y)
	{
		super(x,y,ServerWorld.HP_TYPE + ((int)(Math.random()*4+1))*25);
		this.healAmount = Integer.parseInt(getType().substring(3));
	}
}
