package Server.Buildings;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import Server.ServerObject;
import Server.ServerWorld;
import Server.Creatures.ServerCreature;
import Server.Creatures.ServerGoblin;
import Server.Creatures.ServerPlayer;
import Server.Effects.ServerText;
import Server.Items.ServerBuildingItem;
import Server.Items.ServerMoney;
import Server.Items.ServerPotion;
import Server.Items.ServerProjectile;
import Server.Spawners.ServerGoblinSpawner;
import Server.Spawners.ServerSpawner;

/**
 * A castle for a given team
 * 
 * @author Alex Raita & William Xu
 *
 */
public class ServerCastle extends ServerBuilding {

	public final static int[][] GOBLIN_SPAWNS = 
		{{ServerGoblin.GOBLIN_NO, ServerGoblin.GOBLIN_NO, ServerGoblin.GOBLIN_NO, ServerGoblin.GOBLIN_NO, ServerGoblin.GOBLIN_GUARD_NO},
				{ServerGoblin.GOBLIN_GUARD_NO, ServerGoblin.GOBLIN_GUARD_NO, ServerGoblin.GOBLIN_GUARD_NO, ServerGoblin.GOBLIN_NO, ServerGoblin.GOBLIN_NO},
				{ServerGoblin.GOBLIN_GUARD_NO,ServerGoblin.GOBLIN_GUARD_NO,ServerGoblin.GOBLIN_GUARD_NO,ServerGoblin.GOBLIN_GUARD_NO,ServerGoblin.GOBLIN_LORD_NO},
				{ServerGoblin.GOBLIN_KNIGHT_NO,ServerGoblin.GOBLIN_KNIGHT_NO,ServerGoblin.GOBLIN_KNIGHT_NO,ServerGoblin.GOBLIN_GUARD_NO,ServerGoblin.GOBLIN_LORD_NO},
				{ServerGoblin.GOBLIN_KNIGHT_NO,ServerGoblin.GOBLIN_KNIGHT_NO,ServerGoblin.GOBLIN_KNIGHT_NO,ServerGoblin.GOBLIN_KNIGHT_NO,ServerGoblin.GOBLIN_KING_NO},
				{ServerGoblin.GOBLIN_KNIGHT_NO,ServerGoblin.GOBLIN_KNIGHT_NO,ServerGoblin.GOBLIN_GENERAL_NO,ServerGoblin.GOBLIN_GENERAL_NO,ServerGoblin.GOBLIN_KING_NO},
				{ServerGoblin.GOBLIN_GENERAL_NO,ServerGoblin.GOBLIN_GENERAL_NO,ServerGoblin.GOBLIN_GENERAL_NO,ServerGoblin.GOBLIN_GENERAL_NO,ServerGoblin.GOBLIN_KING_NO}};

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
	 * To prices to advance from each tier
	 */

	/**
	 * The XP of the castle
	 */
	private int xp = 0;

	/**
	 * Arrow sources on the castle
	 */
	private ArrayList<ServerObject> arrowSources;

	/**
	 * Starting limit of population
	 */
	public static final int POP_LIMIT = 20;

	/**
	 * Current limit of population
	 */
	private int popLimit;

	/**
	 * Current population
	 */
	private int population;

	/**
	 * Goblins that this castle provides each spawn tick
	 */
	private int castleGoblins[];

	/**
	 * All the spawners of this team
	 */
	private ArrayList<ServerGoblinSpawner> spawners;

	private int spawnerNo = 0;

	/**
	 * Buildings currently alive that contribute to spawning goblins
	 */
	private LinkedList<ServerBarracks> barracks;

	private Stack<ServerBarracks> barracksToRemove;

	private Stack<ServerBarracks> barracksToAdd;

	/**
	 * Delay between each spawning session
	 */
	public static final int spawnDelay = 1000;
	
	

	//public final static int[] CASTLE_TIER_XP = {100, 500, 1000, 5000, 10000, 100000}; //Change later
	public final static int[] CASTLE_TIER_XP = {1000,2500,5000,7500,12500,15000}; 

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
		spawners = new ArrayList<ServerGoblinSpawner>();
		barracks = new LinkedList<ServerBarracks>();
		barracksToRemove = new Stack<ServerBarracks>();
		barracksToAdd = new Stack<ServerBarracks>();
		castleGoblins = GOBLIN_SPAWNS[tier];

		arrowSources = new ArrayList<ServerObject>();
		targetRange = 1000;

		popLimit = POP_LIMIT;
		population = 0;
	}

	@Override
	public void update()
	{
		while (!barracksToRemove.isEmpty())
		{
			barracks.remove(barracksToRemove.pop());
		}
		while (!barracksToAdd.isEmpty())
		{
			barracks.add(barracksToAdd.pop());
		}

		if (getWorld().getWorldCounter() % spawnDelay == spawnDelay/4) // Don't spawn immediately once the game starts
		{
			for (ServerBarracks producer:barracks)
			{
				for (Integer goblin:producer.getGoblins())
				{
					spawnGoblin(goblin);
				}
			}

			for (Integer goblin:castleGoblins)
			{
				spawnGoblin(goblin);
			}
		}


	}

	/**
	 * Spawn a goblin at one of the spawners
	 * @param goblinNo
	 */
	public synchronized void spawnGoblin(int goblinNo)
	{
		if(spawners.size() == 0)
			return;
		
		ServerGoblinSpawner spawner = spawners.get(spawnerNo);
		getWorld().add(new ServerGoblin(spawner.getX(), spawner.getY() - spawner.getHeight() - ServerWorld.TILE_SIZE, getWorld(), getTeam(), goblinNo));

		if ((++spawnerNo)>=spawners.size())
		{
			spawnerNo = 0;
		}
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

	/**
	 * Upgrade the castle tier
	 */
	public synchronized void upgrade()
	{
		xp -= ServerCastle.CASTLE_TIER_XP[tier];
		setMaxHP(getMaxHP() + 2500);
		setHP(getHP() + 2500);
		tier++;
		castleGoblins = GOBLIN_SPAWNS[tier];


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

		arrowSources.add(getWorld().add(new ServerArrowSource(getX() + 25, getY() + 225, getTeam(), 45,
				arrowType, targetRange, this, getWorld())));
		arrowSources.add(getWorld().add(new ServerArrowSource(getX() + 815, getY() + 225, getTeam(), 45,
				arrowType, targetRange, this, getWorld())));

		setPopLimit(getPopLimit()+10);

		// Alert each player on the team
		for (ServerPlayer player: getWorld().getEngine().getListOfPlayers())
		{
			if (player.getTeam()==getTeam())
			{
				getWorld().add(new ServerText(player.getX()+player.getWidth()/2, player.getY() - 30, "***Level Up***", ServerText.LIGHT_GREEN_TEXT, getWorld()));
			}
		}
	}

	public void reinitialize()
	{
		if (getTeam() == RED_TEAM)
			setName("Red Team's Castle");
		else
			setName("Blue Team's Castle");

		arrowSources.add(getWorld().add(new ServerArrowSource(getX() + 25, getY() + 225, getTeam(), 45,
				ServerWorld.WOODARROW_TYPE, targetRange, this, getWorld())));
		arrowSources.add(getWorld().add(new ServerArrowSource(getX() + 815, getY() + 225, getTeam(), 45,
				ServerWorld.WOODARROW_TYPE, targetRange, this, getWorld())));
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


	// ///////////////////////
	// GETTERS AND SETTERS //
	// ///////////////////////
	public ServerCreature getTarget() {
		return target;
	}

	public void setTarget(ServerCreature target) {
		this.target = target;
	}

	public synchronized void addMoney(ServerMoney money) {
		if(money.getSource().getType().equals(ServerWorld.PLAYER_TYPE))
		{
			((ServerPlayer)money.getSource()).addTotalMoneySpent(money.getAmount());
		}
		this.money += money.getAmount();
	}

	public synchronized void addMoney(int money) {
		this.money += money;
	}
	
	public synchronized void spendMoney(int money)
	{
		this.money -= money;
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

	public synchronized void addXP(int xp)
	{
		this.xp += xp;
		if (tier < CASTLE_TIER_XP.length && this.xp >= CASTLE_TIER_XP[tier])
		{
			upgrade();

			//Update default player stats
			if(getTeam() == RED_TEAM)
			{
				getWorld().increaseRedMoveSpeed(ServerPotion.SPEED_AMOUNT);
				getWorld().increaseRedJumpSpeed(ServerPotion.JUMP_AMOUNT);
				getWorld().increaseRedPlayerStartHP(ServerPotion.MAX_HP_INCREASE);
				getWorld().increaseRedPlayerStartMana(ServerPotion.MAX_MANA_INCREASE);
				getWorld().increaseRedStartBaseDamage(ServerPotion.DMG_AMOUNT);
			}
			else
			{
				getWorld().increaseBlueMoveSpeed(ServerPotion.SPEED_AMOUNT);
				getWorld().increaseBlueJumpSpeed(ServerPotion.JUMP_AMOUNT);
				getWorld().increaseBluePlayerStartHP(ServerPotion.MAX_HP_INCREASE);
				getWorld().increaseBluePlayerStartMana(ServerPotion.MAX_MANA_INCREASE);
				getWorld().increaseBlueStartBaseDamage(ServerPotion.DMG_AMOUNT);
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
	
	public synchronized void hireMerc()
	{
		if (getMoney()>= ServerBuildingItem.MERC_COST)
		{
			spendMoney(ServerBuildingItem.MERC_COST);
			for (int no = 0; no < 10; no++)
			{
				spawnGoblin(ServerGoblin.GOBLIN_NINJA_NO);
				spawnGoblin(ServerGoblin.GOBLIN_SAMURAI_NO);
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

	public synchronized void removeGoblin(ServerGoblin goblin)
	{
		setPopulation(getPopulation() - goblin.getHousingSpace());
	}

	public synchronized void addBarracks(ServerBarracks barracks)
	{
		this.barracksToAdd.push(barracks);
	}

	public synchronized void removeBarracks(ServerBarracks barracks)
	{
		this.barracksToRemove.push(barracks);
	}

	public synchronized void addSpawner(ServerGoblinSpawner spawner)
	{
		this.spawners.add(spawner);
	}
}
