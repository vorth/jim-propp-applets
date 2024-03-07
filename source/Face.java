/*
 *
 * Face
 *
 */
import java.applet.*;
import java.awt.*;
import java.util.Vector;

public class Face extends Applet 
{
	private static int m_nNextID = 1;
	private int m_nID;

	private Color m_cColor;     // What color am I?
	private Vertex m_vLoc;      // Where am I?
	
	private Vector m_vVertices; // What points define me?
	private Vector m_vEdges;    // What edges define me?
	private int m_nEdges;       // How many?

	private Vector m_vFaces;    // Who are my neighbors?
	private int m_nFaces;       // How many?
	
	private Vector m_vTiles;    // What tiles do I serve?
	
	Face(Vector edges)
	{
		this(Color.black, edges);
	}

	Face(Color color, Vector edges)
	{
		Face m_fTemp;
		Edge m_eTemp;
		Vertex m_pTemp;
		int target;
		int previous;
		
		
		m_nID = m_nNextID ++;
		m_cColor = color;
		m_vEdges = (Vector) edges.clone();
		m_nEdges = m_vEdges.size();
		m_vFaces = new Vector(1);
		for (int i = 0; i < m_nEdges; i++)
		{
			m_eTemp = (Edge)m_vEdges.elementAt(i);
			m_eTemp.AddFace(this);
		}
		m_vTiles = new Vector(4);		
		m_vVertices = new Vector(m_nEdges);		
		m_pTemp = (Vertex)((Edge)m_vEdges.elementAt(0)).Left();
		m_vVertices.addElement(m_pTemp);
		target = m_pTemp.ID();
		previous = 0;
		for (int i = m_nEdges - 1; i > 0; i--)
		{
			for (int j = m_nEdges - 1; j > 0; j--)
			{
				if (j == previous)
				{
					j--;
				}
				m_eTemp = (Edge)m_vEdges.elementAt(j);
				if(target == ((Vertex)m_eTemp.Left()).ID())
				{
					m_pTemp = (Vertex)m_eTemp.Right();
					m_vVertices.addElement(m_pTemp);
					target = m_pTemp.ID();
					previous = j;
					break;
				}
				else
				if(target == ((Vertex)m_eTemp.Right()).ID())
				{
					m_pTemp = (Vertex)m_eTemp.Left();
					m_vVertices.addElement(m_pTemp);
					target = m_pTemp.ID();
					previous = j;
					break;
				}		
			}
		}
		if (m_nEdges % 2 == 0)
		{
			m_vLoc = new Vertex((Vertex)m_vVertices.elementAt(0), (Vertex)m_vVertices.elementAt(m_nEdges / 2));
		}
		else
		{
			float xsum = 0, ysum = 0, zsum = 0;
			
			for (int i = 0; i < m_nEdges; i++)
			{
				 m_pTemp = (Vertex)m_vVertices.elementAt(i);
				 xsum += m_pTemp.X();
				 ysum += m_pTemp.Y();
				 zsum += m_pTemp.Z();
			}
			m_vLoc = new Vertex(xsum / m_nEdges, ysum / m_nEdges, zsum / m_nEdges);
		}
	}	   

	public void AddFace(Face face)
	{
		if (!m_vFaces.contains(face))
		{
			m_vFaces.addElement(face);
			m_nFaces++;
		}
	}
	
	public void Tile(int figure, Tile tile)
	{
		while (m_vTiles.size() <= figure)
		{
			m_vTiles.addElement(null);
		}
		m_vTiles.setElementAt(tile, figure);
	}

	public Tile Tile(int figure)
	{
		if (m_vTiles.size() <= figure)
		{
			return null;
		}
		return (Tile)m_vTiles.elementAt(figure);
	}

	public void Color(Color color)
	{
		m_cColor = color;
	}

	public Color Color()
	{
		return m_cColor;
	}

	public int ID()
	{
		return m_nID;
	}

	public Vertex Loc()
	{
		return m_vLoc;
	}

	public int Edges()
	{
		return m_nEdges;
	}

	public Edge EdgeNum(int edge)
	{
		return (Edge)m_vEdges.elementAt(edge - 1);
	}

	public Vertex Vertex(int vertex)
	{
		return (Vertex)m_vVertices.elementAt(vertex - 1);
	}

	public void paint(int figure, Graphics g)
	{
		g.setColor(m_cColor);
		paintHelper(figure, g);
	}
	
	public void paint(Graphics g)
	{
		g.setColor(m_cColor);
		paintHelper(g);
	}
	
	public void paintHelper(Graphics g)
	{
		int[] X;
		int[] Y;
		
		X = new int[m_nEdges];
		Y = new int[m_nEdges];
		if (m_nEdges == m_vVertices.size())
		{
			for (int i = m_nEdges - 1; i >= 0; i--)
			{
				X[i] =  Tiling.adjust / 2 + (int)(Tiling.absminx + (Tiling.absmaxx - Tiling.absminx)/(Tiling.winmaxx - Tiling.winminx)*(((Vertex)m_vVertices.elementAt(i)).X() - Tiling.winminx));
				Y[i] = (int)(Tiling.absminy + (Tiling.absmaxy - Tiling.absminy)/(Tiling.winmaxy - Tiling.winminy)*(((Vertex)m_vVertices.elementAt(i)).Y() - Tiling.winminy));
			}
			g.fillPolygon(X, Y, m_nEdges);
		}
		for (int i = m_nEdges - 1; i >= 0; i--)
		{

			((Edge)m_vEdges.elementAt(i)).paint(g);
		}
	}

	public void paintHelper(int figure, Graphics g)
	{
		int[] X;
		int[] Y;
		
		X = new int[m_nEdges];
		Y = new int[m_nEdges];
		if (m_nEdges == m_vVertices.size())
		{
			for (int i = m_nEdges - 1; i >= 0; i--)
			{
				X[i] = figure * Tiling.adjust + (int)(Tiling.absminx + (Tiling.absmaxx - Tiling.absminx)/(Tiling.winmaxx - Tiling.winminx)*(((Vertex)m_vVertices.elementAt(i)).X() - Tiling.winminx));
				Y[i] = (int)(Tiling.absminy + (Tiling.absmaxy - Tiling.absminy)/(Tiling.winmaxy - Tiling.winminy)*(((Vertex)m_vVertices.elementAt(i)).Y() - Tiling.winminy));
			}
			g.fillPolygon(X, Y, m_nEdges);
		}
		for (int i = m_nEdges - 1; i >= 0; i--)
		{  
			((Edge)m_vEdges.elementAt(i)).paint(figure, g);
		}
		//g.setColor(Color.yellow);
		//g.drawString((new Integer(m_nID)).toString(), (int)(Tiling.absminx + (Tiling.absmaxx - Tiling.absminx)/(Tiling.winmaxx - Tiling.winminx)*(m_vLoc.X() - Tiling.winminx)) - 5, (int)(Tiling.absminy + (Tiling.absmaxy - Tiling.absminy)/(Tiling.winmaxy - Tiling.winminy)*(m_vLoc.Y() - Tiling.winminy)) + 5);
	} 
}

