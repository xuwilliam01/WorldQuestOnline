package Server.Effects;

import Client.ClientWorld;
import Server.ServerWorld;
import Server.Creatures.ServerPlayer;

/**
 * A small floating number indicating the damage dealt in an attack
 * 
 * @author William
 *
 */
public class ServerPlayerText extends ServerText {
	ServerPlayer player;

	/**
	 * Constructor for a damage indicator
	 * 
	 * @param x
	 * @param y
	 * @param text
	 * @param colour
	 */
	public ServerPlayerText(double x, double y, String text, char colour,
			ServerWorld world, ServerPlayer player) {


		super(x, y, 0, text, colour, ServerWorld.PLAYER_TEXT_TYPE, world, 120 + text.length());

		this.world = world;
		this.player = player;
	}

	@Override
	public void update() {
		
		if (player.isAlive())
		{
			setX(player.getX() + player.getWidth() / 2 );
		}
		else
		{
			// Player death width
						setX(player.getX() + 84 / 2 );
		}
		setY(player.getY() - 40);
		super.update();
	}
}
