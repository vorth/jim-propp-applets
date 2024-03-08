/*
 *
 * Edge
 *
 */
// import java.applet.*;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Color;
import java.util.Vector;

public class Edge extends Applet
{
	private static int m_nNextID = 1;	
	private int m_nID;

	private Vertex m_pLeft;	     // What do I connect?
	private Vertex m_pRight;
    private Vector m_vFaces;     // What faces do I serve?
	private Vertex m_pMedian;    // Where am I?
	private Vector transparent;  // Should I be drawn?
	boolean extendable;              // Am I on the edge?
	int m_nKind;                 // What kind of edge am I?

	Edge(Vertex left, Vertex right)
	{			
		m_nID = m_nNextID++;
		m_pLeft = left;
		m_pLeft.AddEdge(this);
		m_pRight = right;
		m_pRight.AddEdge(this);
		m_vFaces = new Vector(1);
		m_pMedian =	new Vertex(m_pLeft, m_pRight);
		transparent = new Vector(Tiling.figures);
		extendable = false;
		m_nKind = 0;
		for (int i = Tiling.figures; i > 0; i--)
		{
			transparent.addElement(new Integer(0));
		}
	}

	Edge(boolean E, Vertex left, Vertex right)
	{
		this(left, right);
		extendable = E;
	}

	Edge(int type, Vertex left, Vertex right)
	{
		this(left, right);
		m_nKind = type;
	}

	Edge(boolean E, int type, Vertex left, Vertex right)
	{
		this(left, right);
		extendable = E;
		m_nKind = type;
	}

	public float slope()
	{
		return (m_pLeft.Y()-m_pRight.Y())/(m_pLeft.X()-m_pRight.X());
	}

	public void SetTrans(int figure, boolean value)
	{
		if (value)
		{
			transparent.setElementAt(new Integer(1), figure);
		}
		else
		{
			transparent.setElementAt(new Integer(0), figure);
		}
	}

	public void AddFace(Face face)
	{
		m_vFaces.addElement(face);
	}

	public int Faces()
	{
		return m_vFaces.size();
	}

	public Face Other(Face face)
	{
		if (m_vFaces.size() != 2)
		{
			return null;
		}
		if (m_vFaces.elementAt(0) == face)
		{
			return (Face)m_vFaces.elementAt(1);
		}
		else
		if (m_vFaces.elementAt(1) == face)
		{
			return (Face)m_vFaces.elementAt(0);
		}
		else
		{
			return null;
		}
	}

	public Vertex Other(Vertex vertex)
	{
		if (vertex.ID() == m_pLeft.ID())
		{
			return m_pRight;
		}
		else
		{
			return m_pLeft;
		}
	}

	public Face Face(int face)
	{
		return (Face)m_vFaces.elementAt(face - 1);
	}

	public Vertex Left()
	{
		return m_pLeft;
	}

	public Vertex Right()
	{
		return m_pRight;
	}

	public void paint(int figure, Graphics g)
	{
		if(!Trans(figure))
		{
			int X1 = (int)(Tiling.absminx + (Tiling.absmaxx - Tiling.absminx)/(Tiling.winmaxx - Tiling.winminx)*(m_pLeft.X() - Tiling.winminx));
			int Y1 = (int)(Tiling.absminy + (Tiling.absmaxy - Tiling.absminy)/(Tiling.winmaxy - Tiling.winminy)*(m_pLeft.Y() - Tiling.winminy));
			int X2 = (int)(Tiling.absminx + (Tiling.absmaxx - Tiling.absminx)/(Tiling.winmaxx - Tiling.winminx)*(m_pRight.X() - Tiling.winminx));
			int Y2 = (int)(Tiling.absminy + (Tiling.absmaxy - Tiling.absminy)/(Tiling.winmaxy - Tiling.winminy)*(m_pRight.Y() - Tiling.winminy));
		
			g.setColor(Color.black);
			g.drawLine(X1 + Tiling.adjust * figure, Y1, X2 + Tiling.adjust * figure, Y2);
			//g.drawString((new Integer(m_nID)).toString(), (X1 + X2) / 2 + Tiling.adjust * figure , (Y1 + Y2) / 2 + 5);
		}
	}

	public void paint(Graphics g)
	{
		if(!Trans(0))
		{
			int X1 = (int)(Tiling.absminx + (Tiling.absmaxx - Tiling.absminx)/(Tiling.winmaxx - Tiling.winminx)*(m_pLeft.X() - Tiling.winminx));
			int Y1 = (int)(Tiling.absminy + (Tiling.absmaxy - Tiling.absminy)/(Tiling.winmaxy - Tiling.winminy)*(m_pLeft.Y() - Tiling.winminy));
			int X2 = (int)(Tiling.absminx + (Tiling.absmaxx - Tiling.absminx)/(Tiling.winmaxx - Tiling.winminx)*(m_pRight.X() - Tiling.winminx));
			int Y2 = (int)(Tiling.absminy + (Tiling.absmaxy - Tiling.absminy)/(Tiling.winmaxy - Tiling.winminy)*(m_pRight.Y() - Tiling.winminy));
		
			g.setColor(Color.black);
			g.drawLine(X1 + Tiling.adjust / 2, Y1, X2 + Tiling.adjust / 2, Y2);
		}
	}


	public int ID()
	{
		return m_nID;
	}

	public boolean Trans(int figure)
	{
		return (((Integer)transparent.elementAt(figure)).intValue() != 0);
	}

	public void Reset()
	{
		m_nNextID = 1;
	}
}

