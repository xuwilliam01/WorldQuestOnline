package Server.Creatures;

import java.util.ArrayList; 

import Effects.ServerDamageIndicator;
import Server.ServerObject;
import Server.ServerWorld;
import Server.Items.ServerAccessory;
import Server.Items.ServerItem;
import Server.Items.ServerPotion;
import Server.Items.ServerWeaponSwing;
import Tools.RowCol;

public abstract class ServerCreature extends ServerObject {
	/**
	 * Teams
	 */
	public final static int NEUTRAL = 0;
	public final static int RED_TEAM = 1;
	public final static int BLUE_TEAM = 2;

	/**
	 * Maximum possible HP of the creature
	 */
	private int maxHP;
	/**
	 * Current HP of the creature
	 */
	private int HP;

	/**
	 * Whether this creature is attackable or not
	 */
	private boolean attackable;

	/**
	 * Team of the creature
	 */
	private int team = NEUTRAL;

	/**
	 * Stores the inventory of the creature
	 */
	private ArrayList<ServerItem> inventory = new ArrayList<ServerItem>();

	/**
	 * World that the creature is in
	 */
	private ServerWorld world;

	/**
	 * The amount of resistance to a knockback by a weapon (normally based on
	 * size of the creature)
	 */
	private double knockBackResistance;

	/**
	 * The horizontal direction the creature is facing
	 */
	private String direction;

	/**
	 * The direction to change the character to right after an action
	 */
	private String nextDirection;

	/**
	 * The number of pixels relative to the hitbox to draw the creature (sent to
	 * the client)
	 */
	private double relativeDrawX;

	/**
	 * The number of pixels relative to the hitbox to draw the creature (sent to
	 * the client)
	 */
	private double relativeDrawY;

	/**
	 * The current frame of the creature's animation
	 */
	private RowCol rowCol = new RowCol(0, 0);

	/**
	 * Whether or not the creature is alive
	 */
	private boolean alive;

	/**
	 * The base damage the creature does
	 */
	private int baseDamage = 0;

	/**
	 * Constructor for a creature
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param relativeDrawX
	 * @param relativeDrawY
	 * @param gravity
	 * @param image
	 * @param type
	 * @param maxHP
	 * @param world
	 */
	public ServerCreature(double x, double y, int width, int height, double relativeDrawX, double relativeDrawY,
			double gravity, String image, String type, int maxHP, ServerWorld world, boolean attackable) {
		super(x, y, width, height, gravity, image, type);

		this.attackable = attackable;
		this.relativeDrawX = relativeDrawX;
		this.relativeDrawY = relativeDrawY;

		this.alive = true;
		this.maxHP = maxHP;
		HP = maxHP;
		this.world = world;
		direction = "RIGHT";
		nextDirection = "RIGHT";

		// Calculate the resistance to knockback based on weight
		knockBackResistance = Math.sqrt((getWidth() * getHeight())) / 16;
	}

	public boolean isAttackable() {
		return attackable;
	}

	public void setAttackable(boolean attackable) {
		this.attackable = attackable;
	}

	public void setTeam(int team) {
		this.team = team;
	}

	public int getTeam() {
		return team;
	}

	public int getMaxHP() {
		return maxHP;
	}

	public int getHP() {
		return HP;
	}

	/**
	 * Set HP to a certain amount
	 * 
	 * @param HP
	 */
	public void setHP(int HP) {
		this.HP = HP;
	}

	/**
	 * Inflict a certain amount of damage to the npc and destroy if less than 0
	 * hp
	 * 
	 * @param amount
	 */
	public void inflictDamage(int amount, double knockBack) {
		if (HP > 0) {
			HP -= amount;

			// Where the damage indicator appears
			double damageX = Math.random() * getWidth() + getX();
			double damageY = Math.random() * getHeight() / 2 + getY() + getHeight()/4;
			char colour = ServerDamageIndicator.YELLOW_TEXT;

			if (getType().equals(ServerWorld.PLAYER_TYPE)) {
				colour = ServerDamageIndicator.RED_TEXT;
			}

			world.add(new ServerDamageIndicator(damageX, damageY, Integer.toString(amount), colour, world));
		}

		if (HP <= 0) {
			destroy();
			dropInventory();
		} else {
			// Override inflict damage in each subclass (except for npc and
			// player because those have knockback) and change what happens when
			// they get hit
			// Only for NPC and PLAYER
			// Knock back the creature based on the knockback force
			// if (Math.abs(knockBack) - knockBackResistance > 0)
			// {
			// setVSpeed(-(Math.abs(knockBack) - knockBackResistance));
			// if (knockBack > 0)
			// {
			// setHSpeed(getHSpeed()+(knockBack-knockBackResistance)/2);
			// }
			// else
			// {
			// setHSpeed(getHSpeed()-(knockBack+knockBackResistance)/2);
			// }
			// }

		}

		System.out.println("Damage taken: " + amount);
	}

	/**
	 * Drop every item in the creature's inventory
	 */
	public void dropInventory() {
		ServerItem money = null;
		for (ServerItem item : inventory) {
			if (item.getType().equals(ServerWorld.MONEY_TYPE)
					&& getType().substring(0, 2).equals(ServerWorld.PLAYER_TYPE))
				money = item;
			else
				dropItem(item);
		}

		inventory.clear();
		if (money != null)
			inventory.add(money);

	}

	public ArrayList<ServerItem> getInventory() {
		return inventory;
	}

	public void addItem(ServerItem item) {
		if (item.getType().charAt(1) == ServerWorld.STACK_TYPE.charAt(1))
			for (ServerItem sItem : inventory) {
				if (item.getType().equals(sItem.getType())) {
					sItem.increaseAmount(item.getAmount());
					return;
				}
			}
		inventory.add(item);
	}

	public void dropItem(ServerItem item) {

		item.setX(getX() + getWidth() / 2);
		item.setY(getY() + getHeight() / 2 - item.getHeight());
		item.makeExist();
		item.startCoolDown();
		item.setSource(this);
		world.add(item);
		item.setOnSurface(false);
		item.setVSpeed(-Math.random() * 15 - 5);

		if (HP <= 0) {
			int direction = Math.random() < 0.5 ? -1 : 1;
			item.setHSpeed(direction * (Math.random() * 5 + 3));
		} else
			item.setHSpeed(Math.random() * 5 + 3);

	}

	public void use(String item) {
		ServerItem toRemove = null;
		for (ServerItem sItem : inventory) {
			if (sItem.getType().equals(item)) {
				toRemove = sItem;			
					switch (item) {
					case ServerWorld.HP_POTION_TYPE:
						HP = Math.min(maxHP, HP + ServerPotion.HEAL_AMOUNT);
						break;
					case ServerWorld.MAX_HP_TYPE:
						maxHP += ServerPotion.MAX_HP_INCREASE;
						break;
					case ServerWorld.MANA_POTION_TYPE:
						ServerPlayer thisPlayer = (ServerPlayer)this;
						thisPlayer.setMana( Math.min(thisPlayer.getMaxMana(), thisPlayer.getMana()+ ServerPotion.MANA_AMOUNT));
						break;
					case ServerWorld.MAX_MANA_TYPE:
						thisPlayer = (ServerPlayer)this;
						thisPlayer.setMaxMana( thisPlayer.getMaxMana()+ServerPotion.MAX_MANA_INCREASE);
						break;
					case ServerWorld.DMG_POTION_TYPE:
						baseDamage += ServerPotion.DMG_AMOUNT;
						break;
					case ServerWorld.SPEED_POTION_TYPE:
						thisPlayer = (ServerPlayer)this;
						thisPlayer.setHorizontalMovement(thisPlayer.getHorizontalMovement()+ServerPotion.SPEED_AMOUNT);
						break;
					case ServerWorld.JUMP_POTION_TYPE:
						thisPlayer = (ServerPlayer)this;
						thisPlayer.setVerticalMovement(thisPlayer.getVerticalMovement()+ServerPotion.JUMP_AMOUNT);
					
					}				
			}
		}

		if(toRemove != null)
			if (toRemove.getAmount() > 1)
				toRemove.decreaseAmount();
			else
				inventory.remove(toRemove);
	}

	public void drop(String item) {
		ServerItem toRemove = null;
		for (ServerItem sItem : inventory) {
			if (sItem.getType().equals(item)) {
				toRemove = sItem;
				if (toRemove.getAmount() > 1)
					dropItem(ServerItem.copy(sItem));
				else
					dropItem(sItem);
				break;
			}
		}

		if (toRemove.getAmount() > 1)
			toRemove.decreaseAmount();
		else
			inventory.remove(toRemove);
	}

	public double getKnockBackResistance() {
		return knockBackResistance;
	}

	public void setKnockBackResistance(double knockBackResistance) {
		this.knockBackResistance = knockBackResistance;
	}

	public ServerWorld getWorld() {
		return world;
	}

	public void setWorld(ServerWorld world) {
		this.world = world;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	/**
	 * Get the location to draw the image of the creature
	 * 
	 * @return
	 */
	public int getDrawX() {
		return (int) (getX() + relativeDrawX + 0.5);
	}

	/**
	 * Get the location to draw the image of the creature
	 * 
	 * @return
	 */
	public int getDrawY() {
		return (int) (getY() + relativeDrawY + 0.5);
	}

	public String getNextDirection() {
		return nextDirection;
	}

	public void setNextDirection(String nextDirection) {
		this.nextDirection = nextDirection;
	}

	public RowCol getRowCol() {
		return rowCol;
	}

	public void setRowCol(RowCol rowCol) {
		this.rowCol = rowCol;
	}

	public boolean isAlive()
	{
		return alive;
	}

	public void setAlive(boolean alive)
	{
		this.alive = alive;
	}

	public int getBaseDamage()
	{
		return baseDamage;
	}

	public void setBaseDamage(int baseDamage)
	{
		this.baseDamage = baseDamage;
	}


}
