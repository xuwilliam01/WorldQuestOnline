package Server.Creatures;

import Server.ServerWorld;

/**
 * The player (Type 'P')
 * 
 * @author William Xu & Alex Raita
 *
 */
public class ServerAiPlayer extends ServerCreature implements Runnable {

	public ServerAiPlayer(double x, double y, int width, int height, double relativeDrawX, double relativeDrawY,
			double gravity, String image, String type, int maxHP, ServerWorld world, boolean attackable) {
		super(x, y, width, height, relativeDrawX, relativeDrawY, gravity, image, type, maxHP, world, attackable);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
}