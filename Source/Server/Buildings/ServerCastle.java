package Server.Buildings;

import java.util.ArrayList;

import Server.ServerObject;
import Server.ServerWorld;
import Server.Creatures.ServerCreature;
import Server.Creatures.ServerGoblin;
import Server.Creatures.ServerPlayer;
import Server.Items.ServerPotion;
import Server.Items.ServerProjectile;
import Server.Spawners.ServerSpawner;

/**
 * A castle for a given team
 * 
 * @author Alex Raita & William Xu
 *
 */
public class ServerCastle extends ServerBuilding {

	/**
	 * The number of pixels for a target to be in range for the castle to fire
	 * at it
	 */
	private int targetRange;

	/**
	 * The money invested in upgrading the castle
	 */
	private int money = 0;

	/**
	 * The current tier of the castle
	 */
	private int tier = 0;

	/**
	 * The target for the castle to attack
	 */
	private ServerCreature target;

	/**
	 * The type of arrows the castle shoots
	 */
	private String arrowType = ServerWorld.WOODARROW_TYPE;

	/**
	 * Whether the castle shop is open or not
	 */
	private boolean open = false;
	/**
	 * To prices to advance from each tier
	 */

	/**
	 * The XP of the castle
	 */
	private int xp = 0;
	
	private ArrayList<ServerObject> arrowSources;
	
	public static final int POP_LIMIT = 20;
	private int popLimit;
	private int population;
	
	

	//public final static int[] CASTLE_TIER_XP = {100, 500, 1000, 5000, 10000, 100000}; //Change later
	public final static int[] CASTLE_TIER_XP = {100,500,1000,5000,10000,100000}; 
	public final static int[] CASTLE_TIER_PRICE = { 25, 45, 75, 100, 125, 150 };

	/**
	 * Constructor
	 * 
	 * @param x
	 *            the x-coordinate
	 * @param y
	 *            the y-coordinate
	 * @param team
	 *            the team of the castle
	 * @param world
	 *            the world of the castle
	 */
	public ServerCastle(double x, double y, int team, ServerWorld world) {
		super(x, y, ServerWorld.CASTLE_TYPE, team, world);
		
		arrowSources = new ArrayList<ServerObject>();
		targetRange = 1000;
		
		popLimit = POP_LIMIT;
		population = 0;
	}

	public void upgrade()
	{
		xp -= ServerCastle.CASTLE_TIER_XP[tier];
		setMaxHP(getMaxHP() + 5000);
		setHP(getHP() + 5000);
		tier++;

		for (ServerObject object: arrowSources)
		{
			object.destroy();
		}
		
		arrowSources.clear();
		
		if (tier == 3) {
			arrowType = ServerWorld.STEELARROW_TYPE;
		} else if (tier == 5) {
			arrowType = ServerWorld.MEGAARROW_TYPE;
		}
		
		arrowSources.add(getWorld().add(new ServerArrowSource(getX() + 25, getY() + 225, getTeam(), 90,
				arrowType, targetRange, this, getWorld())));
		arrowSources.add(getWorld().add(new ServerArrowSource(getX() + 815, getY() + 225, getTeam(), 90,
				arrowType, targetRange, this, getWorld())));
		
		setPopLimit(getPopLimit()+10);
	}
	
	@Override
	public void destroy()
	{
		super.destroy();
		for (ServerObject object: arrowSources)
		{
			object.destroy();
		}
		arrowSources.clear();
	}

	public void spendMoney(int money)
	{
		this.money -= money;
	}

	//Methods for the castle shop
	public boolean isOpen()
	{
		return open;
	}

	public void close()
	{
		open = false;
	}

	public void open()
	{
		open = true;
	}

	// ///////////////////////
	// GETTERS AND SETTERS //
	// ///////////////////////
	public ServerCreature getTarget() {
		return target;
	}

	public void setTarget(ServerCreature target) {
		this.target = target;
	}

	public void addMoney(int money) {
		this.money += money;
	}

	public int getMoney() {
		return money;
	}

	public int getTier() {
		return tier;
	}

	public void setTier(int tier) {
		this.tier = tier;
	}

	public int getXP()
	{
		return xp;
	}

	public void reinitialize()
	{
		if (getTeam() == RED_TEAM)
			setName("Red Team's Castle");
		else
			setName("Blue Team's Castle");
		
		
		arrowSources.add(getWorld().add(new ServerArrowSource(getX() + 25, getY() + 225, getTeam(), 90,
				ServerWorld.WOODARROW_TYPE, targetRange, this, getWorld())));
		arrowSources.add(getWorld().add(new ServerArrowSource(getX() + 815, getY() + 225, getTeam(), 90,
				ServerWorld.WOODARROW_TYPE, targetRange, this, getWorld())));
	
		
	}

	public synchronized void addXP(int xp)
	{
		this.xp += xp;
		if (tier < CASTLE_TIER_XP.length && this.xp >= CASTLE_TIER_XP[tier])
		{
			upgrade();

			//Update default player stats
			if(getTeam() == RED_TEAM)
			{
				ServerPlayer.redMoveSpeed += ServerPotion.SPEED_AMOUNT;
				ServerPlayer.redJumpSpeed += ServerPotion.JUMP_AMOUNT;
				ServerPlayer.redPlayerStartHP += ServerPotion.MAX_HP_INCREASE;
				ServerPlayer.redPlayerStartMana += ServerPotion.MAX_MANA_INCREASE;
				ServerPlayer.redStartBaseDamage += ServerPotion.DMG_AMOUNT;
			}
			else
			{
				ServerPlayer.blueMoveSpeed += ServerPotion.SPEED_AMOUNT;
				ServerPlayer.blueJumpSpeed += ServerPotion.JUMP_AMOUNT;
				ServerPlayer.bluePlayerStartHP += ServerPotion.MAX_HP_INCREASE;
				ServerPlayer.bluePlayerStartMana += ServerPotion.MAX_MANA_INCREASE;
				ServerPlayer.blueStartBaseDamage += ServerPotion.DMG_AMOUNT;
			}

			//Upgrade all players
			for (ServerPlayer player : getWorld().getEngine().getListOfPlayers())
			{
				if(player.getTeam() == getTeam())
				{
					player.setBaseDamage(player.getBaseDamage()+ServerPotion.DMG_AMOUNT);
					player.setMaxHP(player.getMaxHP()+ServerPotion.MAX_HP_INCREASE);
					player.setHP(player.getMaxHP());
					player.setMaxMana(player.getMaxMana()+ServerPotion.MAX_MANA_INCREASE);
					player.setMana(player.getMana());
					player.setHorizontalMovement(player
							.getHorizontalMovement()
							+ ServerPotion.SPEED_AMOUNT);
					player.setVerticalMovement(player
							.getVerticalMovement() + ServerPotion.JUMP_AMOUNT);
				}
			}
		}
	}

	public int getPopLimit() {
		return popLimit;
	}

	public synchronized void setPopLimit(int popLimit) {
		this.popLimit = popLimit;
	}
	
	public synchronized void increasePopLimit(int amount)
	{
		setPopLimit(getPopLimit()+amount);
	}
	
	public synchronized void decreasePopLimit(int amount)
	{
		setPopLimit(getPopLimit()-amount);
	}

	public int getPopulation() {
		return population;
	}

	public synchronized void setPopulation(int population) {
		this.population = population;
	}
	
	public void increasePopulation(int amount)
	{
		setPopulation(getPopulation()+amount);
	}
	
	public synchronized void addGoblin(ServerGoblin goblin)
	{
		if (population + goblin.getHousingSpace() <= popLimit)
		{
			setPopulation(getPopulation() + goblin.getHousingSpace());
		}
		else
		{
			// Counter-act the lost housing space from the goblin dying
			setPopulation(getPopulation() + goblin.getHousingSpace());
			goblin.destroy();
		}
		
	}
	
	public synchronized void removeGoblin(ServerGoblin goblin)
	{
		setPopulation(getPopulation() - goblin.getHousingSpace());
	}

}
