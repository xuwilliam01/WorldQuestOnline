package Tools;


/**
 * A binary tree object with various methods
 * @author Alex Raita & William Xu
 * @param <E> The type of class objects in this Class will be defined as
 * @version November 22, 2015
 */
public class BinaryTree<E extends Comparable<E>>
{
	//Store the first elements of the tree
	private BinaryTreeNode<E> head;

	/**
	 * Constructor sets the first element to null
	 */
	public BinaryTree()
	{
		head = null;
	}

	/**
	 * Adds an item to the tree
	 * @param item the item to be added
	 */
	public void add(E item)
	{
		//If the tree is empty, set the head to the item
		if (head == null)
		{
			head = new BinaryTreeNode<E>(item);
		}
		//The tree is not empty so find where the element should go (note this is a sorted tree)
		else
		{
			BinaryTreeNode<E> tempNode = head;

			while (true)
			{
				if (tempNode.getItem().compareTo(item) <= 0)
				{
					if (tempNode.getRight() == null)
					{
						tempNode.setRight(new BinaryTreeNode<E>(item));
						break;
					}

					tempNode = tempNode.getRight();
				}
				else
				{
					if (tempNode.getLeft() == null)
					{
						tempNode.setLeft(new BinaryTreeNode<E>(item));
						break;
					}

					tempNode = tempNode.getLeft();
				}
			}
		}
	}

	/**
	 * Removes an item from the tree
	 * @param item the item to be removed
	 */
	public void remove(E item)
	{
		//Do not do anything if the tree is empty
		if (head == null)
		{
		}
		//Find the item in the tree and remove it
		else
		{
			BinaryTreeNode<E> tempNode = head;
			BinaryTreeNode<E> parentNode = head;
			boolean movedRight = true;
			while (true)
			{
				if (tempNode.getItem().compareTo(item) < 0)
				{
					if (tempNode.getRight() == null)
					{
						break;
					}
					parentNode = tempNode;
					movedRight = true;
					tempNode = tempNode.getRight();
				}
				else if (tempNode.getItem().compareTo(item) > 0)
				{
					if (tempNode.getLeft() == null)
					{
						break;
					}
					parentNode = tempNode;
					movedRight = false;
					tempNode = tempNode.getLeft();
				}
				else
				{
					if (tempNode.getRight() == null
							&& tempNode.getLeft() == null)
					{
						if (movedRight)
						{
							parentNode.setRight(null);
						}
						else
						{
							parentNode.setLeft(null);
						}
						break;
					}
					else if (tempNode.getRight() == null)
					{
						if (movedRight)
						{
							parentNode.setRight(tempNode.getLeft());
						}
						else
						{
							parentNode.setLeft(tempNode.getLeft());
						}
						break;
					}
					else if (tempNode.getLeft() == null)
					{
						if (movedRight)
						{
							parentNode.setRight(tempNode.getRight());
						}
						else
						{
							parentNode.setLeft(tempNode.getRight());
						}
						break;
					}
					else
					{
						BinaryTreeNode<E> originalNode = tempNode;
						parentNode = tempNode;
						tempNode = tempNode.getRight();
						while (tempNode.getLeft() != null)
						{
							parentNode = tempNode;
							tempNode=tempNode.getLeft();
						}

						originalNode.setItem(tempNode.getItem());

						if (tempNode.getRight() != null)
						{
							if (tempNode.getRight().getItem().compareTo(
									parentNode.getItem()) >= 0)
							{
								parentNode.setRight(tempNode.getRight());
							}
							else
							{
								parentNode.setLeft(tempNode.getRight());
							}
							break;
						}

					}
				}
			}
		}
	}
	
	/**
	 * Gets a matching item (matching is defined by the comparable method)
	 * @param item the item to be found
	 * @return the item if it was found, null if not found
	 */
	public E get(E item)
	{
		//If the list is empty return null
		if (head == null)
		{
			return null;
		}
		//Search through the tree for the item by going left and right, given the item in the current node
		else
		{
			BinaryTreeNode<E> tempNode = head;

			while (true)
			{
				if (tempNode.getItem().compareTo(item) < 0)
				{

					if (tempNode.getRight() == null)
					{
						return null;
					}

					tempNode = tempNode.getRight();
				}
				else if (tempNode.getItem().compareTo(item) > 0)
				{

					if (tempNode.getLeft() == null)
					{
						return null;
					}

					tempNode = tempNode.getLeft();
				}
				else
				{
					return tempNode.getItem();
				}
			}
		}
	}
	
	/**
	 * Checks if the tree contains a given item
	 * @param item the item to be checked
	 * @return true if the item is found, false if not
	 */
	public boolean contains(E item)
	{
		//Item is not found if the tree if empty
		if (head == null)
		{
			return false;
		}
		//Look for the item
		else
		{
			BinaryTreeNode<E> tempNode = head;

			while (true)
			{
				if (tempNode.getItem().compareTo(item) < 0)
				{

					if (tempNode.getRight() == null)
					{
						return false;
					}

					tempNode = tempNode.getRight();
				}
				else if (tempNode.getItem().compareTo(item) > 0)
				{

					if (tempNode.getLeft() == null)
					{
						return false;
					}

					tempNode = tempNode.getLeft();
				}
				else
				{
					return true;
				}
			}
		}
	}
}
