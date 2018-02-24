package Server.Buildings;
import Server.ServerWorld;
import Server.Creatures.ServerPlayer;
import Server.Effects.ServerText;

public class ServerMine extends ServerBuilding {

	private ServerCastle castle = null;
	
	/**
	 * Amount of gold added after each update
	 */
	public final static int GOLD_PER_TICK = 1;
	public final static int MAX_GOLD = 15;
	public final static int TICK_DELAY = 240;
	
	private int gold = 0;
	private int counter = 0;
	
	public ServerMine(double x, double y, String type, int team, ServerWorld world) {
		super(x, y, type, team, world);

	}

	@Override
	public void update()
	{
		super.update();
		if (castle==null)
		{
			if (getTeam() == ServerPlayer.BLUE_TEAM)
			{
				castle = getWorld().getBlueCastle();
			}
			else
			{
				castle = getWorld().getRedCastle();
			}
		}
		
		if (gold < MAX_GOLD/3.0)
		{
			setImage("GOLD_MINE");
		}
		else if (gold < MAX_GOLD*2/3.0)
		{
			setImage("GOLD_MINEg_0");
		}
		else if (gold < MAX_GOLD)
		{
			setImage("GOLD_MINEg_1");
		}
		else
		{
			setImage("GOLD_MINEg_2");
		}
		
		
		if ((++counter)> TICK_DELAY)
		{
			gold = Math.min(gold+GOLD_PER_TICK, MAX_GOLD);
			counter = 0;
		}
		
		if (gold > MAX_GOLD/3.0 && checkPlayerCollision() && castle!=null)
		{
			castle.addMoney(gold);
			
			getWorld().add(new ServerText(getX()+getWidth()/2, getY() - 20, gold + " gold added to castle", ServerText.LIGHT_YELLOW_TEXT, getWorld()));
			gold = 0;
			
		}
		
	}
	
	public boolean checkPlayerCollision()
	{
		for (ServerPlayer player : getWorld().getEngine().getListOfPlayers())
		{
			if (player.getTeam()==getTeam() && collidesWith(player))
			{
				return true;
			}
		}
		return false;
	}
	
	
}
