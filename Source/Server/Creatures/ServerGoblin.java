package Server.Creatures;

import java.util.ArrayList;

import Imports.Audio;
import Server.ServerObject;
import Server.ServerWorld;
import Server.Buildings.ServerCastle;
import Server.Effects.ServerText;
import Server.Items.ServerItem;
import Server.Items.ServerProjectile;
import Server.Items.ServerWeapon;
import Server.Items.ServerWeaponSwing;
import Tools.RowCol;

/**
 * A goblin class
 * 
 * @author William Xu and Alex Raita
 *
 */
public class ServerGoblin extends ServerCreature {

	// Types of goblins
	public final static int NUM_TYPES = 12;

	/**
	 * The default HP of a goblin of a certain type
	 */
	public final static int GOBLIN_HP = 60;
	public final static int GOBLIN_NO = 0;
	
	public final static int GOBLIN_ARCHER_HP = 35;
	public final static int GOBLIN_ARCHER_NO = 1;
	public final static int ARCHER_FIGHTING_RANGE = 550;

	public final static int GOBLIN_SOLDIER_HP = 70;
	public final static int GOBLIN_SOLDIER_NO = 2;
	
	public final static int GOBLIN_WIZARD_HP = 60;
	public final static int GOBLIN_WIZARD_NO = 3;
	
	public final static int GOBLIN_SAMURAI_HP = 80;
	public final static int GOBLIN_SAMURAI_NO = 4;
	
	public final static int GOBLIN_NINJA_HP = 60;
	public final static int GOBLIN_NINJA_NO = 5;
	
	public final static int GOBLIN_LORD_HP = 150;
	public final static int GOBLIN_LORD_NO = 6;

	public final static int GOBLIN_GUARD_HP = 90;
	public final static int GOBLIN_GUARD_NO = 7;
	
	public final static int GOBLIN_KNIGHT_HP = 125;
	public final static int GOBLIN_KNIGHT_NO = 8;
	
	public final static int GOBLIN_GIANT_HP = 500;
	public final static int GOBLIN_GIANT_NO = 9;
	
	public final static int GOBLIN_GENERAL_HP = 150;
	public final static int GOBLIN_GENERAL_NO = 10;
	
	public final static int GOBLIN_KING_HP = 250;
	public final static int GOBLIN_KING_NO = 11;
	
	public final static int noOfGoblinTypes = 12;

	/**
	 * The speed at which the goblin walks
	 */
	private double movementSpeed = 3;

	/**
	 * The initial speed the goblin jumps at
	 */
	private int jumpSpeed = 12;

	/**
	 * The action currently performed by the goblin
	 */
	private String action;

	/**
	 * The number of frames before the goblin can perform another action
	 */
	private int actionDelay;

	/**
	 * The number of frames that has passed after the goblin's action is
	 * disabled / the goblin just recently performed an action
	 */
	private int actionCounter;

	/**
	 * The counter that plays the death animation
	 */
	private long deathCounter = -1;

	/**
	 * The target for the goblin to follow and attack
	 */
	private ServerCreature target;

	/**
	 * Whether or not the goblin is actually at the target
	 */
	private boolean onTarget = false;

	/**
	 * The range to lock on to an enemy
	 */
	private int targetRange = 250;

	/**
	 * Whether or not the goblin is a melee user or ranged
	 */
	private boolean isMelee = true;

	/**
	 * The range for the goblin to start fighting the target
	 */
	private int fightingRange;

	/**
	 * The amount of armour the goblin has (reducing the total damage taken)
	 */
	private double armour = 0;

	/**
	 * The damage the goblin does
	 */
	private int damage;

	/**
	 * The weapon the goblin uses
	 */
	private String weapon;

	/**
	 * The fighting range of this goblin
	 */
	private int privateFightingRange;

	/**
	 * Amount of space goblin takes
	 */
	private int housingSpace;

	/**
	 * Reference to the castle
	 */
	private ServerCastle castle = null;

	/**
	 * Constructor for a random goblin type
	 */
	public ServerGoblin(double x, double y, ServerWorld world, int team, int goblinNo) {
		super(x, y, 20, 64, -24, -64, ServerWorld.GRAVITY, "GOB_RIGHT_0_0", ServerWorld.NAKED_GOBLIN_TYPE, GOBLIN_HP,
				world, true);
		
		housingSpace = 1;

		switch (goblinNo) {
		case GOBLIN_NO:
			setType(ServerWorld.NAKED_GOBLIN_TYPE);
			setImage("GOB_RIGHT_0_0");
			fightingRange = (int) (Math.random() * ServerWorld.TILE_SIZE / 2) + ServerWorld.TILE_SIZE / 2;
			setMaxHP(GOBLIN_HP);
			setHP(GOBLIN_HP);

			weapon = "DASTONE_0";
			damage = 4;
			housingSpace = 1;

			setName("A Goblin");
			break;
		case GOBLIN_ARCHER_NO:
			setType(ServerWorld.GOBLIN_ARCHER_TYPE);
			setImage("GOBARCHER_RIGHT_0_0");
			privateFightingRange = ARCHER_FIGHTING_RANGE + (int) (Math.random() * 400 - 200);
			fightingRange = privateFightingRange;
			targetRange = fightingRange;
			setMaxHP(GOBLIN_ARCHER_HP);
			setHP(GOBLIN_ARCHER_HP);

			armour = 0.1;
			weapon = ServerWorld.WOODARROW_TYPE;
			damage = 7;
			isMelee = false;
			housingSpace = 2;

			setName("A Goblin Archer");

			break;
		case GOBLIN_SOLDIER_NO:
			setType(ServerWorld.GOBLIN_SOLDIER_TYPE);
			setImage("GOBSOLDIER_RIGHT_0_0");
			fightingRange = (int) (Math.random() * ServerWorld.TILE_SIZE) + ServerWorld.TILE_SIZE;

			setMaxHP(GOBLIN_SOLDIER_HP);
			setHP(GOBLIN_SOLDIER_HP);

			armour = 0.2;
			weapon = "AXIRON_0";
			damage = 6;
			housingSpace = 2;

			setName("A Goblin Soldier");
			break;
		case GOBLIN_NINJA_NO:
			setType(ServerWorld.GOBLIN_NINJA_TYPE);
			setImage("GOBNINJA_RIGHT_0_0");
			fightingRange = (int) (Math.random() * 100 + 250);
			targetRange = fightingRange;
			setMaxHP(GOBLIN_NINJA_HP);
			setHP(GOBLIN_NINJA_HP);

			weapon = ServerWorld.NINJASTAR_TYPE;
			damage = ServerWeapon.STAR_DMG;
			isMelee = false;
			movementSpeed = 5;
			jumpSpeed = 16;
			housingSpace = 2;

			setName("A Ninja Goblin");
			break;
		case GOBLIN_SAMURAI_NO:
			setType(ServerWorld.GOBLIN_SAMURAI_TYPE);
			setImage("GOBWORKER_RIGHT_0_0");
			fightingRange = (int) (Math.random() * ServerWorld.TILE_SIZE) + ServerWorld.TILE_SIZE * 2;
			setMaxHP(GOBLIN_SAMURAI_HP);
			setHP(GOBLIN_SAMURAI_HP);

			armour = 0.1;
			damage = 9;
			movementSpeed = 4;
			jumpSpeed = 14;
			weapon = "HAIRON_0";
			housingSpace = 2;

			setName("A Goblin Samurai");
			break;
		case GOBLIN_GUARD_NO:
			setType(ServerWorld.GOBLIN_GUARD_TYPE);
			setImage("GOBGUARD_RIGHT_0_0");
			setMaxHP(GOBLIN_GUARD_HP);
			setHP(GOBLIN_GUARD_HP);

			fightingRange = (int) (Math.random() * ServerWorld.TILE_SIZE) + ServerWorld.TILE_SIZE;
			armour = 0.2;
			damage = 8;
			weapon = "SWIRON_0";
			housingSpace = 3;

			setName("A Goblin Guard");
			break;
		case GOBLIN_KNIGHT_NO:
			setType(ServerWorld.GOBLIN_KNIGHT_TYPE);
			setImage("GOBKNIGHT_RIGHT_0_0");
			fightingRange = (int) (Math.random() * ServerWorld.TILE_SIZE) + ServerWorld.TILE_SIZE;
			setMaxHP(GOBLIN_KNIGHT_HP);
			setHP(GOBLIN_KNIGHT_HP);

			armour = 0.4;
			weapon = "SWIRON_0";
			damage = 8;
			housingSpace = 3;

			setName("A Goblin Knight");
			break;
		case GOBLIN_LORD_NO:
			setType(ServerWorld.GOBLIN_LORD_TYPE);
			setImage("GOBLORD_RIGHT_0_0");
			fightingRange = (int) (Math.random() * ServerWorld.TILE_SIZE) + ServerWorld.TILE_SIZE;
			setMaxHP(GOBLIN_LORD_HP);
			setHP(GOBLIN_LORD_HP);

			armour = 0.3;
			weapon = "SWGOLD_0";
			damage = 12;
			housingSpace = 3;

			setName("A Goblin Lord");
			break;
		case GOBLIN_WIZARD_NO:
			setType(ServerWorld.GOBLIN_WIZARD_TYPE);
			setImage("GOBWIZARD_RIGHT_0_0");
			fightingRange = (int) (Math.random() * 250 + 250);
			privateFightingRange = fightingRange;
			targetRange = fightingRange;
			setMaxHP(GOBLIN_WIZARD_HP);
			setHP(GOBLIN_WIZARD_HP);

			armour = 0.2;
			int weaponChoice = (int) (Math.random() * 5);
			weapon = ServerWorld.ICEBALL_TYPE;
			damage = ServerWeapon.ICEWAND_DMG;
			actionDelay = 240;
			if (weaponChoice == 4) {
				weapon = ServerWorld.FIREBALL_TYPE;
				actionDelay = 180;
				damage = ServerWeapon.FIREWAND_DMG;
			}
			isMelee = false;
			housingSpace = 3;

			setName("A Goblin Wizard");
			break;
		case GOBLIN_GIANT_NO:
			setType(ServerWorld.GOBLIN_GIANT_TYPE);
			setImage("GOBGIANT_RIGHT_0_0");
			fightingRange = (int) (Math.random() * 8 + 8);
			setMaxHP(GOBLIN_GIANT_HP);
			setHP(GOBLIN_GIANT_HP);

			housingSpace = 3;
			movementSpeed = 2;
			armour = 0;
			damage = 15;
			setWidth(getWidth() * 2);
			setHeight(getHeight() * 2);
			setRelativeDrawX(getRelativeDrawX() * 2);
			setRelativeDrawY(getRelativeDrawY() * 2);
			setName("A Goblin Giant");
			housingSpace = 6;

			break;
		case GOBLIN_GENERAL_NO:
			setType(ServerWorld.GOBLIN_GENERAL_TYPE);
			setImage("GOBGENERAL_RIGHT_0_0");
			fightingRange = (int) (Math.random() * ServerWorld.TILE_SIZE) + ServerWorld.TILE_SIZE;
			setMaxHP(GOBLIN_GENERAL_HP);
			setHP(GOBLIN_GENERAL_HP);

			armour = 0.5;
			weapon = "SWDIAMOND_0";
			damage = 16;
			setName("A Goblin General");
			housingSpace = 4;

			break;
		case GOBLIN_KING_NO:
			setType(ServerWorld.GOBLIN_KING_TYPE);
			setImage("GOBKING_RIGHT_0_0");
			fightingRange = (int) (Math.random() * ServerWorld.TILE_SIZE) + ServerWorld.TILE_SIZE;
			setMaxHP(GOBLIN_KING_HP);
			setHP(GOBLIN_KING_HP);

			armour = 0.6;
			weapon = "AXDIAMOND_0";
			damage = 20;
			setName("A Goblin King");
			housingSpace = 5;

			break;
		}

		// Randomize the movement speed for each goblin so they don't stack as
		// much
		movementSpeed = Math.random() * 1.0 * (movementSpeed / 2.0) + movementSpeed * 3.0 / 4;

		if (Math.random() < 0.1)
			addItem(ServerItem.randomItem(getX(), getY(), world));
		setTeam(team);

		if (team == ServerCastle.BLUE_TEAM) {
			castle = world.getBlueCastle();
		} else {
			castle = world.getRedCastle();
		}

		castle.addGoblin(this);
	}

	/**
	 * Update the goblin behavior every game tick
	 */
	public void update() {
		if (isAlive()) {
			// Update the goblin's direction or try to jump over tiles
			if (getHSpeed() > 0) {
				setDirection("RIGHT");
			} else if (getHSpeed() < 0) {
				setDirection("LEFT");
			}

			if (getHSpeed() == 0 && isOnSurface() && !onTarget && action == null) {
				setVSpeed(-jumpSpeed);
				setOnSurface(false);
			}

			// Update the action counter for the goblin
			if (action != null && actionCounter < actionDelay) {
				actionCounter++;
			} else {
				action = null;
				actionCounter = -1;
				setRowCol(new RowCol(0, 0));
			}

			// Have the goblin move towards the enemy base when it has no target
			if (getTarget() == null) {
				if (getWorld().getWorldCounter() % 15 == 0) {
					setTarget(findTarget(targetRange));
				}

				onTarget = false;
				if (getTarget() == null && action == null) {
					if (getTeam() == ServerPlayer.BLUE_TEAM) {
						if (quickInRange(getWorld().getRedCastle(), (double) targetRange)) {
							setTarget(getWorld().getRedCastle());
						} else if (getX() - getWorld().getRedCastleX() < 0) {
							setHSpeed(movementSpeed);
						} else if (getX() - getWorld().getRedCastleX() > 0) {
							setHSpeed(-movementSpeed);
						}

					} else if (getTeam() == ServerPlayer.RED_TEAM) {
						if (quickInRange(getWorld().getBlueCastle(), (double) targetRange)) {
							setTarget(getWorld().getBlueCastle());
						} else if (getX() - getWorld().getBlueCastleX() < 0) {
							setHSpeed(movementSpeed);
						} else {
							setHSpeed(-movementSpeed);
						}
					}
				}
			}
			// Remove the target when it is out of range or dies
			else if (!getTarget().isAlive() || !getTarget().exists() || !quickInRange(getTarget(), targetRange)) {
				setTarget(null);
				if (getType().equals(ServerWorld.GOBLIN_ARCHER_TYPE)) {
					fightingRange = privateFightingRange;
				}
			}
			// Follow and attack the target
			else {
				// if (getType().equals(ServerWorld.GOBLIN_ARCHER_TYPE)) {
				// if (getTarget().getType().equals(ServerWorld.CASTLE_TYPE)) {
				// fightingRange = 200;
				// } else {
				// fightingRange = 1200;
				// }
				// }

				// Attack the target with the weapon the goblin uses.
				if (quickInRange(getTarget(), fightingRange)) {
					// System.out.println(getTarget().getImage() + " " +
					// getTarget().getX());
					onTarget = true;
					if (action == null && getWorld().getWorldCounter() % 30 == 0) {
						int actionChoice = (int) (Math.random() * 12);

						// Jump occasionally
						if (actionChoice == 0) {
							setTarget(null);
							setVSpeed(-jumpSpeed);
							setOnSurface(false);
							if (getDirection().equals("RIGHT")) {
								setHSpeed(movementSpeed);
							} else {
								setHSpeed(-movementSpeed);
							}
						}
						// Block occasionally
						else if (actionChoice == 1 || actionChoice == 2) {
							action = "BLOCK";
							actionDelay = 55;
						} else {
							if (isMelee) {
								if (getType().equals(ServerWorld.GOBLIN_GIANT_TYPE)) {
									action = "PUNCH";
									setHasPunched(false);
									actionDelay = 60;
								} else {
									action = "SWING";
									actionDelay = 16;

									int angle = 180;
									if (getDirection().equals("RIGHT")) {
										angle = 0;
									}

									if (getType().equals(ServerWorld.GOBLIN_SAMURAI_TYPE)) {
										getWorld().add(new ServerWeaponSwing(this, 0, -16, weapon, angle, actionDelay,
												damage));
									} else {
										getWorld().add(new ServerWeaponSwing(this, 4, -19, weapon, angle, actionDelay,
												damage));
									}
								}
							} else {
								action = "SHOOT";
								actionDelay = 60;

								int xDist = (int) (getTarget().getX() + getTarget().getWidth() / 2
										- (getX() + getWidth() / 2));

								if (xDist > 0) {
									setDirection("RIGHT");
								} else if (xDist < 0) {
									setDirection("LEFT");
								}

								int yDist;

								double angle = 0;
								double targetHeightFactor = 5;
								if (weapon.equals(ServerWorld.WOODARROW_TYPE)) {

									if (getTarget().getType().equals(ServerWorld.CASTLE_TYPE)) {
										targetHeightFactor = 1.3;
									}

									yDist = (int) ((getY() + getHeight() / 3.0)
											- (getTarget().getY() + getTarget().getHeight() / targetHeightFactor));

									int sign = -1;

									angle = Math.atan(((ServerProjectile.ARROW_SPEED * ServerProjectile.ARROW_SPEED)
											+ sign * Math.sqrt(Math.pow(ServerProjectile.ARROW_SPEED, 4)
													- ServerProjectile.ARROW_GRAVITY
															* (ServerProjectile.ARROW_GRAVITY * xDist * xDist
																	+ 2 * yDist * ServerProjectile.ARROW_SPEED
																			* ServerProjectile.ARROW_SPEED)))
											/ (ServerProjectile.ARROW_GRAVITY * xDist));

									if (!(angle <= Math.PI && angle >= -Math.PI)) {
										fightingRange = (int) (privateFightingRange / 1.5);
									}

									if (xDist <= 0) {
										angle = Math.PI - angle;
									} else {
										angle *= -1;
									}

								} else {
									yDist = (int) (getTarget().getY() + getTarget().getHeight() / 2
											- (getY() + getHeight() / targetHeightFactor));
									angle = Math.atan2(yDist, xDist);
								}
								double random = Math.random() * 6;

								if (random < 2) {
									angle += (Math.PI / 8) * (random - 1);
								}

								ServerProjectile projectile = new ServerProjectile(getX() + getWidth() / 2,
										getY() + getHeight() / 3, this, angle, weapon, getWorld());
								projectile.setDamage(damage);
								getWorld().add(projectile);

							}
						}
					}
				} else {
					onTarget = false;
				}

				if (getTarget() != null && !onTarget && action == null) {
					if ((getX() + getWidth() / 2 < getTarget().getX())) {
						if (getHSpeed() == 0 || getWorld().getWorldCounter() % 20 == 0) {
							setHSpeed(movementSpeed);
						}
					} else {
						if (getHSpeed() == 0 || getWorld().getWorldCounter() % 20 == 0) {
							setHSpeed(-movementSpeed);
						}
					}
				} else {
					setHSpeed(0);
				}
			}
		}

		// Update the animation of the goblin
		if (actionCounter >= 0) {
			switch (action) {
			case "SWING":
				switch (actionCounter) {
				case 0:
					setRowCol(new RowCol(2, 0));
					break;
				case 4:
					setRowCol(new RowCol(2, 1));
					break;
				case 8:
					setRowCol(new RowCol(2, 2));
					break;
				case 12:
					setRowCol(new RowCol(2, 3));
					break;
				}
				break;
			case "SHOOT":
				switch (getType()) {
				case ServerWorld.GOBLIN_WIZARD_TYPE:
				case ServerWorld.GOBLIN_NINJA_TYPE:
					switch (actionCounter) {
					case 0:
						setRowCol(new RowCol(2, 4));
						break;
					case 4:
						setRowCol(new RowCol(2, 5));
						break;
					case 8:
						setRowCol(new RowCol(2, 6));
						break;
					case 40:
						setRowCol(new RowCol(0, 0));
						break;
					}
					break;
				case ServerWorld.GOBLIN_ARCHER_TYPE:
					if (actionCounter == 0) {
						setRowCol(new RowCol(2, 7));
					}
					break;
				}
				break;
			case "BLOCK":
				setRowCol(new RowCol(2, 9));
				break;
			case "PUNCH":
				switch (actionCounter) {
				case 0:
					setRowCol(new RowCol(2, 7));
					break;
				case 10:
					setRowCol(new RowCol(2, 8));
					if (!isHasPunched()) {
						punch(damage);
						setHasPunched(true);
					}
					break;
				}
				break;
			}
		}
		if (getHSpeed() != 0 && isOnSurface()) {
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
		} else if (!isAlive()) {
			if (deathCounter < 0) {
				deathCounter = getWorld().getWorldCounter();
				setRowCol(new RowCol(1, 2));
			} else if (getWorld().getWorldCounter() - deathCounter < 15) {
				setRowCol(new RowCol(1, 3));
			} else if (getWorld().getWorldCounter() - deathCounter < 30) {
				setRowCol(new RowCol(1, 4));
			} else if (getWorld().getWorldCounter() - deathCounter < 100) {
				setRowCol(new RowCol(1, 6));
			} else {
				destroy();
			}
		} else if (Math.abs(getVSpeed()) < 4 && !isOnSurface()) {
			setRowCol(new RowCol(0, 8));
		} else if (getVSpeed() < 0) {
			setRowCol(new RowCol(0, 9));
		} else if (getVSpeed() > 0) {
			setRowCol(new RowCol(0, 7));
		}
		setImage(getBaseImage() + "_" + getDirection() + "_" + getRowCol().getRow() + "_" + getRowCol().getColumn()
				+ "");
	}

	@Override
	public void destroy() {
		if (exists()) {
			super.destroy();
			castle.removeGoblin(this);
		}
	}

	@Override
	/**
	 * Inflict damage to the goblin, with specific behavior (such as armour or
	 * blocking)
	 */
	public void inflictDamage(int amount, ServerCreature source) {
		if (!onTarget && source != getTarget()) {
			setTarget(source);
		}

		amount -= amount * armour;

		if (amount <= 0) {
			amount = 1;
		}

		char textColour = ServerText.YELLOW_TEXT;
		if (action == "BLOCK") {
			textColour = ServerText.BLUE_TEXT;
			amount = 0;
		}

		addCastleXP(Math.min(amount,getHP()), source);
		setHP(getHP() - amount);

		double damageX = Math.random() * getWidth() + getX();
		double damageY = Math.random() * getHeight() / 2 + getY() - getHeight() / 3;
		getWorld().add(new ServerText(damageX, damageY, Integer.toString(amount), textColour, getWorld()));

		// Play the death animation for a goblin when it dies
		if (getHP() <= 0 && isAlive()) {
			setAlive(false);

			dropInventory();
			setHSpeed(0);
			setVSpeed(0);

			setAttackable(false);

			actionCounter = -1;
			action = null;
		}
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

	public int getHousingSpace() {
		return housingSpace;
	}

}
