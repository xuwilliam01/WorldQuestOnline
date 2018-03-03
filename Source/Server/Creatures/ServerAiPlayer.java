package Server.Creatures;

import Server.ServerWorld;

/**
 * The player (Type 'P')
 * 
 * @author William Xu & Alex Raita
 *
 */
public class ServerAiPlayer extends ServerCreature{

	public ServerAiPlayer(double x, double y, int width, int height, double relativeDrawX, double relativeDrawY,
			double gravity, String image, String type, int maxHP, ServerWorld world, boolean attackable) {
		super(x, y, width, height, relativeDrawX, relativeDrawY, gravity, image, type, maxHP, world, attackable);
		
	}

	@Override
	public void update() {
		
	}
}