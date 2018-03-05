package Server.Creatures;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;

import Imports.Images;
import Server.ServerEngine;
import Server.ServerObject;
import Server.ServerWorld;
import Server.Buildings.ServerCastle;
import Server.Buildings.ServerHologram;
import Server.Effects.ServerText;
import Server.Items.ServerAccessory;
import Server.Items.ServerArmour;
import Server.Items.ServerItem;
import Server.Items.ServerMoney;
import Server.Items.ServerWeapon;
import Tools.RowCol;

/**
 * The player (Type 'P')
 * 
 * @author William Xu & Alex Raita
 *
 */
public class ServerAIPlayer extends ServerCreature{

	public static String[] botNames = 
		{
			"Bot Richard", 
			"Bot Stephen", 
			"Bot John",
			"Bot Geralt",
			"Bot Stephen",
			"Bot Gaben",
			"Bot Arnold",
			"Bot Harry",
			"Bot Peter"
		};
	
	public static LinkedList<String> namesList;
	
	private boolean disconnected = false;

	private int respawnXSpeed;
	private int respawnYSpeed;

	private ServerCastle castle = null;

	/**
	 * Whether the game is over or not
	 */
	private boolean endGame = false;
	private boolean closeWriter = false;
	private int losingTeam;

	/**
	 * Boolean describing whether or not the x coordinate has changed since the
	 * last flush
	 */
	private boolean xUpdated;

	/**
	 * Boolean describing whether or not the x coordinate has changed since the
	 * last flush
	 */
	private boolean yUpdated;

	/**
	 * The direction the player is trying to move (so player continues to move
	 * in that direction even after collision, until release of the key) (1 is
	 * right, -1 is left)
	 */
	private int movingDirection = 0;

	/**
	 * The speed the player moves horizontally
	 */
	private int horizontalMovement;

	/**
	 * The speed the player moves vertically
	 */
	private int verticalMovement;

	/**
	 * The current weapon selected (change later to actual inventory slot)
	 */
	private char weaponSelected = '9';

	/**
	 * Whether or not the player can use the item/perform the current action
	 * (used for delaying actions)
	 */
	private boolean canPerformAction;

	/**
	 * The number of frames before the player can perform another action
	 */
	private int actionDelay;
	private int actionSpeed = 13;

	/**
	 * The number of frames that has passed after the player's action is
	 * disabled
	 */
	private int actionCounter;

	/**
	 * The specific action being performed alongside the action counter
	 */
	private String action = ServerPlayer.NOTHING;

	/**
	 * The counter that plays the death animation
	 */
	private long deathCounter = -1;


	/**
	 * Stores the equipped weapons
	 */
	private ServerItem[] equippedWeapons = new ServerItem[ServerPlayer.MAX_WEAPONS];

	/**
	 * The equipped armor
	 */
	private ServerArmour equippedArmour = null;

	/**
	 * The accessory worn on the head
	 */
	private ServerAccessory head;

	/**
	 * The accessory worn on the body
	 */
	private ServerAccessory body;

	/**
	 * The damage the player inflicts from just punching
	 */
	public final static int PUNCHING_DAMAGE = 5;

	/**
	 * Will make the player perform the action in the next game loop
	 */
	private boolean performingAction;

	/**
	 * The mouse's x position when the player last wanted to perform an action
	 */
	private int newMouseX;

	/**
	 * The mouse's y position when the player last wanted to perform an action
	 */
	private int newMouseY;

	/**
	 * The string for the base image not including the specific animation frame
	 */
	private String baseImage;

	/**
	 * The weapon being held by the player during action (ex. bows and wands)
	 */
	private ServerObject heldWeapon;

	/**
	 * Stores the mana the player currently has
	 */
	private int mana;

	/**
	 * Stores the maximum possible mana for the player
	 */
	private int maxMana;

	/**
	 * The current text floating on top of the player
	 */
	private String currentText = "";

	/**
	 * The time when the player last sent a message
	 */
	private long textStartTime = 0;

	/**
	 * The amount of time the text is allowed to be shown
	 */
	private int textDuration = 0;

	private int kills = 0;
	private int deaths = 0;
	private int totalDamageDealt = 0;
	private int totalMoneySpent = 0;

	public final static int RELATIVE_X = -14;
	public final static int RELATIVE_Y = -38;

	private boolean ignoreClient = false;

	private String skinColour;
	private String hair;
	
	public final static int MELEE_TYPE = 0;
	public final static int BOW_TYPE = 1;
	public final static int WAND_TYPE = 2;
	private int weaponType;
	
	public ServerAIPlayer(double x, double y, int width, int height, double gravity, ServerWorld world, int team) {
		super(x, y, width, height, RELATIVE_X, RELATIVE_Y, gravity, "BASE_"
				+ world.getEngine().getServer().getPlayerColours()[(int) (Math.random() * world.getEngine().getServer().getPlayerColours().length)] 
						+ "_RIGHT_0_0", ServerWorld.PLAYER_AI_TYPE, world.getBluePlayerStartHP(), world, true); 
		if (namesList == null)
		{
			namesList = new LinkedList<String>(Arrays.asList(botNames));
		}
		setName(namesList.removeFirst());
		
		setTeam(team);
		
		// Set the initial variables
		actionDelay = 20;
		actionSpeed = 13;
		canPerformAction = true;
		action = ServerPlayer.NOTHING;
		performingAction = false;
		newMouseX = 0;
		newMouseY = 0;

		// Set the action counter to -1 when not used
		actionCounter = -1;

		xUpdated = true;
		yUpdated = true;
		
		this.skinColour = world.getEngine().getServer().getPlayerColours()[(int) (Math.random() * world.getEngine().getServer().getPlayerColours().length)];
		baseImage = "BASE_" + skinColour;

		// Set a random hair style for the player
		this.hair = world.getEngine().getServer().getPlayerHairs()[(int)(Math.random() * world.getEngine().getServer().getPlayerHairs().length)];
		setHair(hair);
		
		this.initPlayer();
	}
	
	public void kick()
	{
		namesList.add(getName());
	}

	public void initPlayer()
	{
		// Give the player random start weapon(s)
		int randomStartWeapon = (int) (Math.random() * 3);
		switch (randomStartWeapon) {
		case 0:
			addItem(new ServerWeapon(0, 0, ServerWorld.SWORD_TYPE
					+ ServerWorld.STONE_TIER, getWorld()));
			this.setWeaponType(ServerAIPlayer.MELEE_TYPE);
			break;
		case 1:
			addItem(new ServerWeapon(0, 0, ServerWorld.AX_TYPE
					+ ServerWorld.STONE_TIER, getWorld()));
			this.setWeaponType(ServerAIPlayer.MELEE_TYPE);
			break;
		case 2:
			addItem(new ServerWeapon(0, 0, ServerWorld.SLINGSHOT_TYPE, getWorld()));
			this.setWeaponType(ServerAIPlayer.BOW_TYPE);
			break;
		}
	}
	
	/**
	 * Update the player after each tick
	 */
	@Override
	public void update() {
		if (exists()) {
			// Change the player's facing direction after its current action
			if (actionCounter < 0 && action == ServerPlayer.NOTHING) {
				super.setDirection(getNextDirection());
			}

			// Update the counter for weapon delay
			if (actionCounter < actionDelay) {
				if (!canPerformAction) {
					actionCounter++;
				}
			} else {
				if (heldWeapon != null) {
					heldWeapon.destroy();
					heldWeapon = null;
				}
				actionCounter = -1;
				action = ServerPlayer.NOTHING;
				canPerformAction = true;
			}

			// Update the animation of the player and its accessories
			// The row and column of the frame in the sprite sheet for the image
			setRowCol(new RowCol(0, 0));
			if (actionCounter >= 0) {
				if (action.equals("SWING")) {
					if (actionCounter < 1.0 * actionSpeed / 4.0) {
						setRowCol(new RowCol(2, 0));
					} else if (actionCounter < 1.0 * actionSpeed / 2.0) {
						setRowCol(new RowCol(2, 1));
					} else if (actionCounter < 1.0 * actionSpeed / 4.0 * 3) {
						setRowCol(new RowCol(2, 2));
					} else if (actionCounter < actionSpeed) {
						setRowCol(new RowCol(2, 3));
					}
				} else if (action.equals("PUNCH")) {
					if (actionCounter < 5) {
						setRowCol(new RowCol(2, 7));
					} else if (actionCounter < 16) {
						setRowCol(new RowCol(2, 8));
						if (!isHasPunched()) {
							punch((int) Math.ceil(PUNCHING_DAMAGE
									* (1 + getBaseDamage() / 100.0)));
							setHasPunched(true);
						}
					}
				} else if (action.equals("BOW")) {
					setRowCol(new RowCol(2, 7));
					if (heldWeapon != null) {
						heldWeapon.setX(getDrawX());
						heldWeapon.setY(getDrawY());
					}
				} else if (action.equals("WAND")) {
					setRowCol(new RowCol(2, 5));
					if (heldWeapon != null) {
						if (getDirection().equals("LEFT")) {
							heldWeapon.setX(getDrawX() - (90 - 64));
						} else {
							heldWeapon.setX(getDrawX());
						}
						heldWeapon.setY(getDrawY());
					}
				} else if (action.equals("BLOCK")) {
					setRowCol(new RowCol(2, 9));
				}
			} else if (!isAlive()) {
				if (deathCounter < 0) {
					deathCounter = getWorld().getWorldCounter();
					setRowCol(new RowCol(5, 1));
				} else if (getWorld().getWorldCounter() - deathCounter < 10) {
					setRowCol(new RowCol(5, 1));
				} else if (getWorld().getWorldCounter() - deathCounter < 20) {
					setRowCol(new RowCol(5, 2));
				} else if (getWorld().getWorldCounter() - deathCounter < 300) { // Respawn time here
					setRowCol(new RowCol(5, 4));
				} else {
					int randomStartWeapon = (int) (Math.random() * 3);

					//If we already have a weapon, don't add a random one
					for(ServerItem sItem : getInventory())
					{
						if(sItem.getType().contains(ServerWorld.WEAPON_TYPE))
						{
							randomStartWeapon = -1;
							break;
						}
					}

					switch (randomStartWeapon) {
					case 0:
						addItem(new ServerWeapon(0, 0, ServerWorld.SWORD_TYPE
								+ ServerWorld.STONE_TIER, getWorld()));
						break;
					case 1:
						addItem(new ServerWeapon(0, 0, ServerWorld.AX_TYPE
								+ ServerWorld.STONE_TIER, getWorld()));
						break;
					case 2:
						addItem(new ServerWeapon(0, 0,
								ServerWorld.SLINGSHOT_TYPE, getWorld()));
						break;
					}

					setAlive(true);

					verticalMovement = respawnYSpeed;
					horizontalMovement = respawnXSpeed;

					if (getTeam() == RED_TEAM) {
						setX(getWorld().getRedCastleX());
						setY(getWorld().getRedCastleY());

					} else {
						setX(getWorld().getBlueCastleX());
						setY(getWorld().getBlueCastleY());
					}

					setHP(getMaxHP());
					mana = maxMana;

					setAttackable(true);
					deathCounter = -1;
				}
			} else if (getHSpeed() != 0 && getVSpeed() == 0) {
				int checkFrame = (int) (getWorld().getWorldCounter() % 30);
				if (checkFrame < 5) {
					setRowCol(new RowCol(0, 1));
				} else if (checkFrame < 10) {
					setRowCol(new RowCol(0, 2));
				} else if (checkFrame < 15) {
					setRowCol(new RowCol(0, 3));
				} else if (checkFrame < 20) {
					setRowCol(new RowCol(0, 4));
				} else if (checkFrame < 25) {
					setRowCol(new RowCol(0, 5));
				} else {
					setRowCol(new RowCol(0, 6));
				}
			} else if (!isOnSurface()) {
				if (Math.abs(getVSpeed()) < 5) {
					setRowCol(new RowCol(1, 8));
				} else if (getVSpeed() < -5) {
					setRowCol(new RowCol(1, 7));
				} else if (getVSpeed() > 5) {
					setRowCol(new RowCol(1, 9));
				}
			}
			// Update the player's image
			setImage(baseImage + "_" + getDirection() + "_"
					+ getRowCol().getRow() + "_" + getRowCol().getColumn() + "");
			if (getHead() != null) {
				getHead().update(getDirection(), getRowCol());
			}
			if (getBody() != null) {
				getBody().update(getDirection(), getRowCol());
			}
		}
	}
	
	/**
	 * Equip a weapon onto the player
	 * 
	 * @param itemType
	 *            the weapon to equip
	 */
	public void equipWeapon(String itemType) {
		// Find next open spot in equipped
		int pos = 0;
		for (; pos < ServerPlayer.MAX_WEAPONS; pos++) {
			if (equippedWeapons[pos] == null)
				break;
		}

		// If there are no equip slots left
		if (pos == ServerPlayer.MAX_WEAPONS)
			return;

		// Find the item in the inventory
		ServerItem toRemove = null;
		for (ServerItem item : getInventory()) {
			if (item.getType().equals(itemType)) {
				toRemove = item;
				break;
			}
		}
		equippedWeapons[pos] = toRemove;
		getInventory().remove(toRemove);
	}

	/**
	 * Equip a certain armor
	 * 
	 * @param itemType
	 *            the type of armor to equip
	 */
	public void equipArmour(String itemType) {
		// First replace the armor in the inventory with the current armor, if
		// it exists
		if (equippedArmour != null) {
			getInventory().add(equippedArmour);
		}

		ServerItem toRemove = null;
		for (ServerItem item : getInventory()) {
			if (item.getType().equals(itemType)) {
				toRemove = item;
			}
		}
		
		equippedArmour = (ServerArmour) toRemove;
		
		if (toRemove != null) {
			getInventory().remove(toRemove);
		}

		ServerAccessory newArmour = new ServerAccessory(this,
				equippedArmour.getArmourImage(), equippedArmour.getArmour(),
				getWorld());
		if (getBody() != null) {
			getBody().destroy();
		}
		setBody(newArmour);
		getWorld().add(newArmour);
	}
	
	public void setHair(String hair)
	{
		ServerAccessory newHair = new ServerAccessory(this, hair, 0, getWorld());
		setHead(newHair);
		getWorld().add(newHair);
	}
	
	public void setTeam(int team) {
		super.setTeam(team);

		// Set default player stats
		if (getTeam() == RED_TEAM) {
			respawnXSpeed = getWorld().getRedMoveSpeed();
			respawnYSpeed = getWorld().getRedJumpSpeed();
			horizontalMovement = getWorld().getRedMoveSpeed();
			verticalMovement = getWorld().getRedJumpSpeed();
			mana = getWorld().getRedPlayerStartMana();
			maxMana = getWorld().getRedPlayerStartMana();
			setMaxHP(getWorld().getRedPlayerStartHP());
			setHP(getMaxHP());
			setBaseDamage(getWorld().getRedStartBaseDamage());
		} else {
			respawnXSpeed = getWorld().getBlueMoveSpeed();
			respawnYSpeed = getWorld().getBlueJumpSpeed();
			horizontalMovement = getWorld().getBlueMoveSpeed();
			verticalMovement = getWorld().getBlueJumpSpeed();
			mana = getWorld().getBluePlayerStartMana();
			maxMana = getWorld().getBluePlayerStartMana();
			setMaxHP(getWorld().getBluePlayerStartHP());
			setHP(getMaxHP());
			setBaseDamage(getWorld().getBlueStartBaseDamage());
		}
	}
	
	/**
	 * Damage the player a certain amount, and destroy if hp is 0 or below (With
	 * specific differences from regular creatures)
	 * 
	 * @param amount
	 *            the amount of damage to inflict
	 * @param source
	 *            the source of the attack
	 */
	@Override
	public void inflictDamage(int amount, ServerCreature source) {
		if (isAlive()) {
			if (equippedArmour != null) {
				amount -= amount * equippedArmour.getArmour();
			}

			if (amount <= 0) {
				amount = 1;
			}

			char textColour = ServerText.RED_TEXT;
			if (action == "BLOCK") {
				textColour = ServerText.BLUE_TEXT;
				amount = 0;
			}

			if(source != this)
				addCastleXP(Math.min(amount, getHP()), source);

			setHP(getHP() - amount);

			double damageX = Math.random() * getWidth() + getX();
			double damageY = Math.random() * getHeight() / 2 + getY()
			- getHeight() / 3;

			getWorld().add(
					new ServerText(damageX, damageY, Integer.toString(amount),
							textColour, getWorld()));

			// Play the death animation of the player when the HP drops to 0 or
			// below, and eventually respawn the player
			if (getHP() <= 0) {
				deaths++;

				// For the scoreboard
				if(source != this)
				{
					if (source.getType().equals(ServerWorld.PLAYER_TYPE)) {
						getWorld().getEngine().broadcast("@ " + ServerPlayer.toChars(source.getID()) + " "
								+ source.getTeam());
						((ServerPlayer) source).addKill();
					}
					getWorld().getEngine().broadcast("! " + ServerPlayer.toChars(getID()) + " " + getTeam());

					if (source.getTeam() == ServerCreature.NEUTRAL) {
						String firstName = getTeam() + getName();
						String secondName = ServerCreature.NEUTRAL
								+ source.getName();
						getWorld().getEngine().broadcast("k " + firstName.split(" ").length + " "
								+ firstName + " " + secondName.split(" ").length
								+ " " + secondName);
					} else {
						String firstName = source.getTeam() + source.getName();
						String secondName = getTeam() + getName();
						getWorld().getEngine().broadcast("f " + firstName.split(" ").length + " "
								+ firstName + " " + secondName.split(" ").length
								+ " " + secondName);
					}
				}
				setAlive(false);

				dropInventory();

				verticalMovement = 0;
				horizontalMovement = 0;

				if (getBody() != null) {
					getBody().destroy();
					setBody(null);
				}
				setHSpeed(0);
				setVSpeed(0);

				setAttackable(false);
			}
		}
	}
	
	/**
	 * Drop inventory and equipment
	 */
	@Override
	public void dropInventory() {
		getInventory().clear();
		equippedWeapons = new ServerItem[ServerPlayer.MAX_WEAPONS];
		equippedArmour = null;
	}
	
	// ///////////////////////
	// GETTERS AND SETTERS //
	// ///////////////////////
	public boolean isDisconnected() {
		return disconnected;
	}

	public void setDisconnected(boolean disconnected) {
		this.disconnected = disconnected;
	}

	public void setX(double x) {
		if (x != super.getX()) {
			xUpdated = true;
			super.setX(x);
		}
	}

	public void setY(double y) {
		if (y != super.getY()) {
			super.setY(y);
			yUpdated = true;
		}
	}

	public boolean isxUpdated() {
		return xUpdated;
	}

	public boolean isyUpdated() {
		return yUpdated;
	}

	/**
	 * Whether or not the player is currently performing an action
	 * 
	 * @return
	 */
	public boolean inAction() {
		return actionCounter >= 0;
	}

	public boolean isPerformingAction() {
		return performingAction;
	}

	public void setPerformingAction(boolean performingAction) {
		this.performingAction = performingAction;
	}

	public int getNewMouseX() {
		return newMouseX;
	}

	public void setNewMouseX(int newMouseX) {
		this.newMouseX = newMouseX;
	}

	public int getNewMouseY() {
		return newMouseY;
	}

	public void setNewMouseY(int newMouseY) {
		this.newMouseY = newMouseY;
	}

	public ServerAccessory getHead() {
		return head;
	}

	public void setHead(ServerAccessory head) {
		this.head = head;
	}

	public ServerAccessory getBody() {
		return body;
	}

	public void setBody(ServerAccessory body) {
		this.body = body;
	}

	public int getMana() {
		return mana;
	}

	public void setMana(int mana) {
		this.mana = mana;
	}

	public int getMaxMana() {
		return maxMana;
	}

	public void setMaxMana(int maxMana) {
		this.maxMana = Math.min(ServerPlayer.PLAYER_MAX_MANA, maxMana);
	}

	public int getHorizontalMovement() {
		return horizontalMovement;
	}

	public void addHorizontalMovement(int amount) {

		if(isAlive())
		{
			this.horizontalMovement = Math.min(horizontalMovement + amount, ServerPlayer.MAX_HSPEED);
			respawnXSpeed = this.horizontalMovement;
		}
		else
			respawnXSpeed = Math.min(respawnXSpeed + amount, ServerPlayer.MAX_HSPEED);
	}

	public int getVerticalMovement() {
		return verticalMovement;
	}

	public void addVerticalMovement(int amount) {
		if(isAlive())
		{
			this.verticalMovement = Math.min(verticalMovement+amount, ServerPlayer.MAX_VSPEED);
			respawnYSpeed = this.verticalMovement;
		}
		else
			respawnYSpeed = Math.min(respawnYSpeed + amount, ServerPlayer.MAX_VSPEED);
	}

	public void setEndGame(boolean endGame, int losingTeam) {
		this.endGame = endGame;
		this.losingTeam = losingTeam;
	}

	public String getCurrentText() {
		return currentText;
	}

	public void setCurrentText(String currentText) {
		this.currentText = currentText;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	public void setMaxHP(int maxHP) {
		super.setMaxHP(Math.min(ServerPlayer.PLAYER_MAX_HP, maxHP));
	}

	public void setBaseDamage(int baseDamage) {
		super.setBaseDamage(Math.min(ServerPlayer.MAX_DMGADD, baseDamage));
	}

	public boolean isIgnoreClient() {
		return ignoreClient;
	}

	public void setIgnoreClient(boolean ignoreClient) {
		this.ignoreClient = ignoreClient;
	}

	public int getTotalDamageDealt() {
		return totalDamageDealt;
	}

	public void addTotalDamage(int amount) {
		totalDamageDealt += amount;
	}

	public int getTotalMoneySpent() {
		return totalMoneySpent;
	}

	public void addTotalMoneySpent(int amount) {
		totalMoneySpent += amount;
	}

	public int getScore() {
		return totalDamageDealt + kills * ServerPlayer.PLAYER_BASE_HP + totalMoneySpent * 10;
	}

	public int getPing() {
		return 0;
	}

	public void setDeathCounter(long amount)
	{
		deathCounter = amount;
	}

	public int getWeaponType() {
		return weaponType;
	}

	public void setWeaponType(int weaponType) {
		this.weaponType = weaponType;
	}
}