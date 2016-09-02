package Server.Creatures;

import java.util.ArrayList;

import Server.ServerObject;
import Server.ServerWorld;
import Server.Effects.ServerDamageIndicator;
import Server.Items.ServerItem;
import Server.Items.ServerProjectile;
import Server.Items.ServerWeapon;
import Server.Items.ServerWeaponSwing;
import Tools.RowCol;

/**
 * A goblin class
 * 
 * @author alex
 *
 */
public class ServerPlayerBot extends ServerCreature {

	// Types of goblins
	public final static int NUM_TYPES = 12;

	/**
	 * The default HP of a goblin of a certain type
	 */
	public final static int GOBLIN_HP = 60;
	public final static int GOBLIN_ARCHER_HP = 40;
	public final static int ARCHER_FIGHTING_RANGE = 1000;

	public final static int GOBLIN_WIZARD_HP = 60;
	public final static int GOBLIN_WORKER_HP = 65;
	public final static int GOBLIN_NINJA_HP = 60;
	public final static int GOBLIN_LORD_HP = 150;
	public final static int GOBLIN_SOLDIER_HP = 80;
	public final static int GOBLIN_GUARD_HP = 90;
	public final static int GOBLIN_KNIGHT_HP = 125;
	public final static int GOBLIN_GIANT_HP = 500;
	public final static int GOBLIN_GENERAL_HP = 150;
	public final static int GOBLIN_KING_HP = 250;

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
	 * Constructor for a random goblin type
	 */
	public ServerPlayerBot(double x, double y, ServerWorld world, int team) {
		super(x, y, 20, 64, -24, -64, ServerWorld.GRAVITY, "GOB_RIGHT_0_0",
				ServerWorld.NAKED_GOBLIN_TYPE, GOBLIN_HP, world, true);

		int castleTier = world.getRedCastle().getTier();

		if (team == ServerPlayer.BLUE_TEAM) {
			castleTier = world.getBlueCastle().getTier();
		}

		// Set name of the bot
		setName("Bot_" + world.getBotName());

		// Set a random hair style for the player
		String hair = "HAIR0BEIGE";
		int randomHair = (int) (Math.random() * 8);
		switch (randomHair) {
		case 1:
			hair = "HAIR1BEIGE";
			break;
		case 2:
			hair = "HAIR0BLACK";
			break;
		case 3:
			hair = "HAIR1BLACK";
			break;
		case 4:
			hair = "HAIR0BLOND";
			break;
		case 5:
			hair = "HAIR1BLOND";
			break;
		case 6:
			hair = "HAIR0GREY";
			break;
		case 7:
			hair = "HAIR1GREY";
			break;
		}

		// Randomize the movement speed for each goblin so they don't stack as
		// much
		movementSpeed = Math.random() * 1.0 * (movementSpeed / 2)
				+ movementSpeed * 3.0 / 4;

		if (Math.random() < 0.1) {
			addItem(ServerItem.randomItem(getX(), getY()));
		}

		setTeam(team);
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

			if (Math.abs(getHSpeed()) <= 1 && isOnSurface() && !onTarget) {
				setVSpeed(-jumpSpeed);
				setOnSurface(false);
			}

			// Update the action counter for the goblin
			if (action != null && actionCounter < actionDelay) {
				actionCounter++;
			} else {
				action = null;
				actionCounter = -1;
			}

			// Have the goblin move towards the enemy base when it has no target
			if (getTarget() == null) {
				onTarget = false;
				if (getWorld().getWorldCounter() % 15 == 0) {
					setTarget(findTarget());
				}

				if (getTarget() == null && action == null) {
					if (getTeam() == ServerPlayer.BLUE_TEAM) {
						if (quickInRange(getWorld().getRedCastle(),
								(double) targetRange)) {
							setTarget(getWorld().getRedCastle());
						} else if (getX() - getWorld().getRedCastleX() < 0) {
							setHSpeed(movementSpeed);
						} else if (getX() - getWorld().getRedCastleX() > 0) {
							setHSpeed(-movementSpeed);
						}

					} else if (getTeam() == ServerPlayer.RED_TEAM) {
						if (quickInRange(getWorld().getBlueCastle(),
								(double) targetRange)) {
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
			else if (!getTarget().isAlive() || !getTarget().exists()
					|| !quickInRange(getTarget(), targetRange)) {
				setTarget(null);
				fightingRange = privateFightingRange;
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
				if ((getX() + getWidth() / 2 < getTarget().getX()) && !onTarget
						&& action == null) {
					setHSpeed(movementSpeed);
				} else if ((getX() + getWidth() / 2 > getTarget().getX()
						+ getTarget().getWidth())
						&& !onTarget && action == null) {
					setHSpeed(-movementSpeed);
				} else {
					setHSpeed(0);
				}

				// Attack the target with the weapon the goblin uses.
				if (quickInRange(getTarget(), fightingRange)) {
					// System.out.println(getTarget().getImage() + " " +
					// getTarget().getX());
					onTarget = true;
					if (action == null
							&& getWorld().getWorldCounter() % 30 == 0) {
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
								if (getType().equals(
										ServerWorld.GOBLIN_GIANT_TYPE)) {
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

									getWorld().add(
											new ServerWeaponSwing(this, 0, -25,
													weapon, angle, actionDelay,
													damage));
								}
							} else {
								action = "SHOOT";
								actionDelay = 60;

								int xDist = (int) (getTarget().getX()
										+ getTarget().getWidth() / 2 - (getX() + getWidth() / 2));

								if (xDist > 0) {
									setDirection("RIGHT");
								} else if (xDist < 0) {
									setDirection("LEFT");
								}

								int yDist;

								double angle = 0;
								if (weapon.equals(ServerWorld.WOODARROW_TYPE)) {

									double targetHeightFactor = 5;
									if (getTarget().getType().equals(
											ServerWorld.CASTLE_TYPE)) {
										targetHeightFactor = 1.3;
									}

									yDist = (int) ((getY() + getHeight() / 3.0) - (getTarget()
											.getY() + getTarget().getHeight()
											/ targetHeightFactor));

									int sign = -1;

									angle = Math
											.atan(((ServerProjectile.ARROW_SPEED * ServerProjectile.ARROW_SPEED) + sign
													* Math.sqrt(Math
															.pow(ServerProjectile.ARROW_SPEED,
																	4)
															- ServerProjectile.ARROW_GRAVITY
															* (ServerProjectile.ARROW_GRAVITY
																	* xDist
																	* xDist + 2
																	* yDist
																	* ServerProjectile.ARROW_SPEED
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
									yDist = (int) (getTarget().getY()
											+ getTarget().getHeight() / 2 - (getY() + getHeight() / 3));
									angle = Math.atan2(yDist, xDist);
								}
								double random = Math.random() * 6;

								if (random < 2) {
									angle += (Math.PI / 8) * (random - 1);
								}

								ServerProjectile projectile = new ServerProjectile(
										getX() + getWidth() / 2, getY()
												+ getHeight() / 3, this, angle,
										weapon);
								projectile.setDamage(damage);
								getWorld().add(projectile);

							}
						}
					}
				} else {
					onTarget = false;
				}
			}
		}

		// Update the animation of the goblin
		setRowCol(new RowCol(0, 0));
		if (actionCounter >= 0) {
			if (action.equals("SWING")) {
				if (actionCounter < 1.0 * actionDelay / 4.0) {
					setRowCol(new RowCol(2, 0));
				} else if (actionCounter < 1.0 * actionDelay / 2.0) {
					setRowCol(new RowCol(2, 1));
				} else if (actionCounter < 1.0 * actionDelay / 4.0 * 3) {
					setRowCol(new RowCol(2, 2));
				} else if (actionCounter < actionDelay) {
					setRowCol(new RowCol(2, 3));
				}
			}

			else if (action.equals("SHOOT")) {
				if (getType().equals(ServerWorld.GOBLIN_NINJA_TYPE)) {
					if (actionCounter < 4) {
						setRowCol(new RowCol(2, 4));
					} else if (actionCounter < 8) {
						setRowCol(new RowCol(2, 5));
					} else if (actionCounter < 2 * actionDelay / 3) {
						setRowCol(new RowCol(2, 6));
					} else {
						setRowCol(new RowCol(0, 0));
					}
				} else {
					setRowCol(new RowCol(2, 7));
				}
			} else if (action.equals("BLOCK")) {
				setRowCol(new RowCol(2, 9));
			} else if (action.equals("PUNCH")) {
				if (actionCounter < 10) {
					setRowCol(new RowCol(2, 7));
				} else if (actionCounter < 30) {
					setRowCol(new RowCol(2, 8));

					if (!isHasPunched()) {
						punch(damage);
						setHasPunched(true);
					}
				}
			}
		} else if (getHSpeed() != 0 && isOnSurface()) {
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
		setImage(getBaseImage() + "_" + getDirection() + "_"
				+ getRowCol().getRow() + "_" + getRowCol().getColumn() + "");
	}

	/**
	 * Find the nearest enemy creature and attack it, in this case something
	 * from the other team
	 */
	public ServerCreature findTarget() {
		ArrayList<ServerCreature> enemyTeam = getWorld().getBlueTeam();

		if (getTeam() == ServerPlayer.BLUE_TEAM) {
			enemyTeam = getWorld().getRedTeam();
		}

		for (ServerCreature enemy : enemyTeam) {
			if (enemy.isAlive() && quickInRange(enemy, targetRange)) {
				return enemy;
			}
		}
		return null;
	}

	@Override
	/**
	 * Inflict damage to the goblin, with specific behavior (such as armour or blocking)
	 */
	public void inflictDamage(int amount, ServerCreature source) {
		if (!onTarget && source != getTarget()) {
			setTarget(source);
		}

		amount -= amount * armour;

		if (amount <= 0) {
			amount = 1;
		}

		if (action == "BLOCK") {
			amount = 0;
		}

		setHP(getHP() - amount);

		double damageX = Math.random() * getWidth() + getX();
		double damageY = Math.random() * getHeight() / 2 + getY() - getHeight()
				/ 3;
		getWorld().add(
				new ServerDamageIndicator(damageX, damageY, Integer
						.toString(amount), ServerDamageIndicator.YELLOW_TEXT,
						getWorld()));

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
}
