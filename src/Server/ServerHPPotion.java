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
		super(x,y,"IPH");
		this.healAmount = ((int)(Math.random()*4+1))*25;
	}
}
