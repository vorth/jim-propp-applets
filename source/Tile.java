/*
 *
 * Tile
 *
 */
import java.applet.*;
import java.awt.*;	   
import java.util.Vector;

public class Tile extends Applet 
{
	private static int m_nNextID = 1;
	private int m_nID;

	private Color m_cColor;       // What color am I?

	private Face m_fLeft;         // What two faces
	private Face m_fRight;        //   do I cover?

	private Edge m_eEdge;         // What edge divides me?

	Tile(int figure, Face L, Face R)
	{
		m_nID = m_nNextID++;
	    m_fLeft = L;
		m_fLeft.Tile(figure, this);
		m_fRight = R;
		m_fRight.Tile(figure, this);
	out:
		for (int i = m_fLeft.Edges(); i > 0; i--)
		{
			for (int j = m_fRight.Edges(); j > 0; j--)
			{
				if ((m_fLeft.EdgeNum(i)).ID() == (m_fRight.EdgeNum(j)).ID())
				{
					m_eEdge = m_fLeft.EdgeNum(i);
					m_eEdge.Left().addGhost(figure);
					m_eEdge.Right().addGhost(figure);
					m_cColor = Tiling.Color(m_eEdge);
					m_eEdge.SetTrans(figure, true);
					break out;
				}
			}
		}
	}
			
	public void paint(int figure, Graphics g)
	{
		if (m_eEdge != null)
		{
			if (!Tiling.coupled)
			{
				g.setColor(m_cColor);
				m_fLeft.paintHelper(figure, g);
				g.setColor(m_cColor);
				m_fRight.paintHelper(figure, g);
			}
			else
			{
				g.setColor(m_cColor);
				m_fLeft.paintHelper(g);
				g.setColor(m_cColor);
				m_fRight.paintHelper(g);
			}		
		}
	}

	public void Reset(int figure, Face L, Face R)
	{
	    m_fLeft = L;
		m_fLeft.Tile(figure, this);
		m_fRight = R;
		m_fRight.Tile(figure, this);
	out:
		for (int i = m_fLeft.Edges(); i > 0; i--)
		{
			for (int j = m_fRight.Edges(); j > 0; j--)
			{
				if ((m_fLeft.EdgeNum(i)).ID() == (m_fRight.EdgeNum(j)).ID())
				{
					m_eEdge = m_fLeft.EdgeNum(i);
					m_eEdge.Left().addGhost(figure);
					m_eEdge.Right().addGhost(figure);
					m_cColor = Tiling.Color(m_eEdge);
					m_eEdge.SetTrans(figure, true);
					break out;
				}
			}
		}
	}

	public int ID()
	{
		return m_nID;
	}
	
	public Face Left()
	{
		return m_fLeft;
	}

	public Face Right()
	{
		return m_fRight;
	}

	public void cleanup(int figure)
	{
		m_eEdge.Left().remGhost(figure);
		m_eEdge.Right().remGhost(figure);
		m_fLeft.Tile(figure, null);
		m_fRight.Tile(figure, null);
		m_eEdge.SetTrans(figure, false);
	}
}

