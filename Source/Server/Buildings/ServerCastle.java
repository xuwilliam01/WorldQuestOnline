package Server.Buildings;

import java.util.ArrayList;

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
	private int targetRange = 1000;

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
	 * The team of the castle
	 */
	private int team;

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
		
		if (team == RED_TEAM)
			setName("Red Team's Castle");
		else
			setName("Blue Team's Castle");
		
		popLimit = POP_LIMIT;
		population = 0;
	}

	public void upgrade()
	{
		xp -= ServerCastle.CASTLE_TIER_XP[tier];
		setMaxHP(getMaxHP() + 5000);
		setHP(getHP() + 5000);
		tier++;

		if (tier == 3) {
			arrowType = ServerWorld.STEELARROW_TYPE;
		} else if (tier == 5) {
			arrowType = ServerWorld.MEGAARROW_TYPE;
		}
		setPopLimit(getPopLimit()+10);
	}
	/**
	 * Update the castle behavior
	 */
	public void update() {
		// Attack a target
		if (getTarget() == null) {
			if (getWorld().getWorldCounter() % 15 == 0) {
				setTarget(findTarget());
			}
		} else if (!getTarget().isAlive() || !getTarget().exists()
				|| !quickInRange(getTarget(), targetRange)) {
			setTarget(null);
		} else {
			// Every second and a half calculate the angle to shoot the target
			// from and launch a projectile at it
			if (getWorld().getWorldCounter() % 90 == 0) {
				int xDist = (int) (getTarget().getX() + getTarget().getWidth()
						/ 2 - (getX() + 270));

				int yDist = (int) ((getY() + 232) - (getTarget().getY() + getTarget()
				.getHeight() / 2));

				int sign = -1;

				double angle = Math
						.atan(((ServerProjectile.ARROW_SPEED * ServerProjectile.ARROW_SPEED) + sign
								* Math.sqrt(Math.pow(
										ServerProjectile.ARROW_SPEED, 4)
										- ServerProjectile.ARROW_GRAVITY
										* (ServerProjectile.ARROW_GRAVITY
												* xDist * xDist + 2 * yDist
												* ServerProjectile.ARROW_SPEED
												* ServerProjectile.ARROW_SPEED)))
								/ (ServerProjectile.ARROW_GRAVITY * xDist));

				if (xDist <= 0) {
					angle = Math.PI - angle;
				} else {
					angle *= -1;
				}

				ServerProjectile arrow = new ServerProjectile(getX() + 270,
						getY() + 232, this, angle, arrowType,getWorld());

				getWorld().add(arrow);
			}
		}
	}

	/**
	 * Find the nearest enemy creature and attack it (in this case any creature
	 * from the enemy team)
	 */
	public ServerCreature findTarget() {
		ArrayList<ServerCreature> enemyTeam = null;

		if (getTeam() == ServerPlayer.BLUE_TEAM) {
			enemyTeam = getWorld().getRedTeam();
		} else if (getTeam() == ServerPlayer.RED_TEAM) {
			enemyTeam = getWorld().getBlueTeam();
		}
		for (ServerCreature enemy : enemyTeam) {
			if (enemy.isAlive() && quickInRange(enemy, targetRange)
					&& !enemy.getType().equals(ServerWorld.CASTLE_TYPE)) {
				return enemy;
			}
		}
		return null;
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

	public int getTeam() {
		return team;
	}

	public void setTeam(int team) {
		this.team = team;
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
	
	public void increasePopLimit(int amount)
	{
		setPopLimit(getPopLimit()+amount);
	}
	
	public void decreasePopLimit(int amount)
	{
		setPopLimit(getPopLimit()+amount);
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
	
	public void addGoblin(ServerGoblin goblin)
	{
		if (population + goblin.getHousingSpace() <= popLimit)
		{
			setPopulation(getPopulation() + goblin.getHousingSpace());
		}
		else
		{
			goblin.destroy();
			System.out.println("Not enough space for " + goblin.getName());
		}
		
	}
	
	public void removeGoblin(ServerGoblin goblin)
	{
		setPopulation(getPopulation() - goblin.getHousingSpace());
	}

}
