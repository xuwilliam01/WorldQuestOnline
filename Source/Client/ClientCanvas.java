package Client;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ConcurrentModificationException;

import Server.ServerWorld;
import Server.Creatures.ServerCreature;


public class ClientCanvas extends Canvas
{
	private Client client;
	
	public ClientCanvas()
	{
	}

	public void paint(Graphics graphics)
	{
		// Update the map
				try
				{
					client.getWorld().update(graphics, client.getPlayer());
				}
				catch (NullPointerException e)
				{
					System.out.println("Null Pointer Exception for world.update");
				}

				// Draw the ping and the FPS
				graphics.setFont(ClientWorld.NORMAL_FONT);
				graphics.setColor(new Color(240,240,240));
				graphics.drawString(client.getPingString(), Client.SCREEN_WIDTH - 60, 20);
				graphics.drawString("FPS: " + client.getCurrentFPS(), Client.SCREEN_WIDTH - 60, 40);

				// Set the time of day to be displayed
				// DAWN: 5AM - 9AM
				// DAY: 9AM - 5PM
				// DUSK: 5PM - 9PM
				// NIGHT: 9PM - 5AM

				if(client.getWorld() != null)
				{

					String timeOfDay = "DAY";

					if (client.getWorld().getWorldTime() >= ServerWorld.DAY_COUNTERS / 6 * 5)
					{
						timeOfDay = "DAWN";
					}
					else if (client.getWorld().getWorldTime() >= ServerWorld.DAY_COUNTERS / 2)
					{
						timeOfDay = "NIGHT";
					}
					else if (client.getWorld().getWorldTime() >= ServerWorld.DAY_COUNTERS / 3)
					{
						timeOfDay = "DUSK";
					}

					int hour = (client.getWorld().getWorldTime() / 60) + 9;
					if (hour >= 24)
					{
						hour -= 24;
					}
					int minute = client.getWorld().getWorldTime() % 60;

					String amPm = "AM";

					if (hour >= 12)
					{
						hour -= 12;
						amPm = "PM";
					}

					if (hour == 0)
					{
						hour = 12;
					}

					String hourString = "";
					String minuteString = "";

					if (hour < 10)
					{
						hourString = "0";
					}
					if (minute < 10)
					{
						minuteString = "0";
					}
					hourString += hour;
					minuteString += minute;

					graphics.drawString(hourString + ":" + minuteString + " " + amPm,
							Client.SCREEN_WIDTH - 60, 60);
					graphics.drawString(timeOfDay, Client.SCREEN_WIDTH - 60, 80);
				}

				// Draw the chat
				graphics.setFont(ClientWorld.NORMAL_FONT);

				while (true)
				{
					try
					{
						int textY = 40;
						for (String str : client.getChatQueue())
						{
							if (str.substring(0, 2).equals("CH"))
							{
								String newStr = str.substring(3);
								int space = newStr.indexOf(':');
								String coloured = newStr.substring(1, space+1);
								String mssg = newStr.substring(space + 2);
								if (newStr.charAt(0) - '0' == ServerCreature.RED_TEAM)
									graphics.setColor(Color.RED);
								else if (newStr.charAt(0) - '0' == ServerCreature.BLUE_TEAM)
									graphics.setColor(Color.BLUE);
								else
									graphics.setColor(Color.GRAY);
								graphics.drawString(coloured + " ", 10, textY);
								graphics.setColor(Color.YELLOW);
								graphics.drawString(mssg, 10 + graphics
										.getFontMetrics().stringWidth(coloured + " "),
										textY);
							}
							else if(str.substring(0,2).equals("JO"))
							{
								if (str.charAt(3) - '0' == ServerCreature.RED_TEAM)
									graphics.setColor(Color.RED);
								else if (str.charAt(3) - '0' == ServerCreature.BLUE_TEAM)
									graphics.setColor(Color.BLUE);
								else
									graphics.setColor(Color.GRAY);
								graphics.drawString(str.substring(4) + " ", 10, textY);
								graphics.setColor(Color.ORANGE);
								graphics.drawString("joined the game", 10+graphics.getFontMetrics().stringWidth(str.substring(4)+" "), textY);
							}
							else if(str.substring(0,2).equals("RO"))
							{
								if (str.charAt(3) - '0' == ServerCreature.RED_TEAM)
									graphics.setColor(Color.RED);
								else if (str.charAt(3) - '0' == ServerCreature.BLUE_TEAM)
									graphics.setColor(Color.BLUE);
								else
									graphics.setColor(Color.GRAY);
								graphics.drawString(str.substring(4) + " ", 10, textY);
								graphics.setColor(Color.ORANGE);
								graphics.drawString("left the game", 10+graphics.getFontMetrics().stringWidth(str.substring(4)+" "), textY);
							}
							else
							{
								String[] split = str.split(" ");
								int firstLen = Integer.parseInt(split[1]);
								String firstName = "";
								for (int i = 0; i < firstLen; i++)
									firstName += split[i + 2] + " ";

								int secondLen = Integer.parseInt(split[firstLen + 2]);
								String lastName = "";
								for (int i = 0; i < secondLen; i++)
									lastName += split[firstLen + 3 + i] + " ";

								if (firstName.charAt(0) - '0' == ServerCreature.RED_TEAM)
									graphics.setColor(Color.RED);
								else if (firstName.charAt(0) - '0' == ServerCreature.BLUE_TEAM)
									graphics.setColor(Color.BLUE);
								else
									graphics.setColor(Color.DARK_GRAY);
								graphics.drawString(firstName.substring(1), 10, textY);

								graphics.setColor(Color.ORANGE);


								String killWord = "slain";
								String secondKillWord = "killed";

								//int random = (int) (Math.random() * 5);

								//						if (random == 0)
								//						{
								//							killWord = "slain";
								//							secondKillWord = "slayed";
								//						}
								//						else if (random == 1)
								//						{
								//							killWord = "defeated";
								//							secondKillWord = "defeated";
								//						}
								//						else if (random == 2)
								//						{
								//							killWord = "murdered";
								//							secondKillWord = "murdered";
								//						}
								//						else if (random == 3)
								//						{
								//							killWord = "slaughtered";
								//							secondKillWord = "slaughtered";
								//						}
								//						else if (random == 4)
								//						{
								//							killWord = "ended";
								//							secondKillWord = "ended";
								//						}

								if (str.substring(0, 3).equals("KF1"))
									graphics.drawString(
											"was " + killWord + " by a ",
											5 + graphics.getFontMetrics().stringWidth(
													firstName), textY);
								else
									graphics.drawString(
											secondKillWord + " ",
											5 + graphics.getFontMetrics().stringWidth(
													firstName), textY);

								if (lastName.charAt(0) - '0' == ServerCreature.RED_TEAM)
									graphics.setColor(Color.RED);
								else if (lastName.charAt(0) - '0' == ServerCreature.BLUE_TEAM)
									graphics.setColor(Color.BLUE);
								else
									graphics.setColor(Color.GREEN);

								if (str.substring(0, 3).equals("KF1"))
									graphics.drawString(
											lastName.substring(1),
											8 + graphics.getFontMetrics().stringWidth(
													firstName + "was " + killWord
													+ " by a "), textY);
								else
									graphics.drawString(
											lastName.substring(1),
											8 + graphics.getFontMetrics().stringWidth(
													firstName + secondKillWord + " "),
													textY);
							}
							textY += 20;
						}
						break;
					}
					catch (ConcurrentModificationException E)
					{

					}
				}

				if (client.getHP()> 0)
				{
					client.setJustDied(true);
				}
				else
				{
					if (client.isJustDied())
					{
						client.getInventory().clear();
						client.setJustDied(false);
					}
					graphics.setColor(Color.black);
					graphics.setFont(ClientWorld.MESSAGE_FONT);
					graphics.drawString(
							"YOU ARE DEAD. Please wait 10 seconds to respawn", 300, 20);
				}

				
				
				// Repaint the inventory
				client.getInventory().repaint();
				if (!client.getChat().hasFocus())
					requestFocusInWindow();
	}
	
	public void addClient (Client client)
	{
		this.client= client;
	}

}
