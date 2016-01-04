package Tools;
/**
 * A node to be used in the BinaryTree class
 * @author Alex Raita && Willia Xu
 * @param <T> the type of Class that will be used to define objects in this class
 * @version November 22, 2015
 */
public class BinaryTreeNode<T>
{
	private T item;
	private BinaryTreeNode<T> left;
	private BinaryTreeNode<T> right;

	/**
	 * Constructor sets the item of the node
	 * @param item the item to be set
	 */
	public BinaryTreeNode(T item)
	{
		this.item = item;
	}

	/**
	 * Gets the left child
	 * @return the left child
	 */
	public BinaryTreeNode<T> getLeft()
	{
		return left;
	}
	
	/**
	 * Gets the right child
	 * @return the right child
	 */
	public BinaryTreeNode <T>getRight()
	{
		return right;
	}

	/**
	 * Sets the left child
	 * @param left the left child
	 */
	public void setLeft(BinaryTreeNode<T> left)
	{
		this.left= left;
	}
	
	/**
	 * Sets the right child
	 * @param right the right child
	 */
	public void setRight(BinaryTreeNode<T> right)
	{
		this.right = right;
	}

	/**
	 * Gets the item in the node
	 * @return the item in the node
	 */
	public T getItem()
	{
		return this.item;
	}
	
	/**
	 * Sets the item in the node
	 * @param item the item to be set
	 */
	public void setItem(T item)
	{
		this.item = item;
	}
	
	/**
	 * Checks if the node is leaf
	 * @return true if the node is a leaf, false if not
	 */
	public boolean isLeaf()
	{
		return  left== null && right == null;
	}

}