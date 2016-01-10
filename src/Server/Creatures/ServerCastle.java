package Server.Creatures;

import Server.ServerWorld;

public class ServerCastle extends ServerCreature{

	public final static int CASTLE_HP = 1000;
	
	public ServerCastle(double x, double y, int team, ServerWorld world) 
	{
		super(x, y,-1,-1, 0,0,ServerWorld.GRAVITY, "CASTLE.png",ServerWorld.CASTLE_TYPE,CASTLE_HP, world,true);
		setTeam(team);
	}
	
	public void inflictDamage(int amount, double knockBack)
	{
		super.inflictDamage(amount, knockBack);
		if (getHP() <= 0)
		{
			//End the game
		}
	}

}
