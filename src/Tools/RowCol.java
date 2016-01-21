package Tools;

/**
 * Object that stores two integers, the row and column of an index
 * @author William Xu and Alex Raita
 *
 */
public class RowCol
{
	/**
	 * The row number
	 */
	private int row;
	
	/**
	 * The column number
	 */
	private int column;
	
	/**
	 * Constructor for a row column index
	 * @param row
	 * @param column
	 */
	public RowCol(int row, int column)
	{
		this.row = row;
		this.column = column;
	}

	/////////////////////////
	// GETTERS AND SETTERS //
	/////////////////////////
	public int getRow()
	{
		return row;
	}
	public void setRow(int row)
	{
		this.row = row;
	}
	public int getColumn()
	{
		return column;
	}
	public void setColumn(int column)
	{
		this.column = column;
	}
	
	
}
