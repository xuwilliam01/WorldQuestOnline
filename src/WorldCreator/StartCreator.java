package WorldCreator;

import java.io.IOException;

public class StartCreator {

	public static void main(String[] args) throws NumberFormatException, IOException {
		CreatorFrame frame = new CreatorFrame();
		CreatorWorld world = new CreatorWorld();
		CreatorItems items = new CreatorItems(world.getTiles());
		world.setLocation(0,0);
		items.setLocation(CreatorWorld.WIDTH,0);
		
		frame.add(world);
		frame.add(items);
		world.revalidate();
		items.revalidate();
		
		frame.setVisible(true);

	}

}
