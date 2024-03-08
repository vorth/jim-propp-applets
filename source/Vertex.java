/*
 *
 * Vertex
 *
 */
// import java.applet.*;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Color;
import java.util.Vector;

public class Vertex extends Applet
{
	private static int m_nNextID = 1;
	private int m_nID;
	
	private Vector m_vHeight;  // How high am I?

	private Vector m_vLoc;     // Where am I?
	
	private Vector m_vEdges;   // What edges do I serve?
	private Vector m_vGEdges;  // How many are real?

	private Vector m_vFaces;   // An ordered vector of the faces around me
							   // going clockwise starting with a black face.
	Vertex()
	{
		m_nID = m_nNextID ++;
		m_vLoc = new Vector(0, 1);
		m_vEdges = new Vector(0, 1);
		m_vGEdges = new Vector(Tiling.figures);
		m_vHeight = new Vector(Tiling.figures);
		for (int i = Tiling.figures; i > 0; i--)
		{
			m_vGEdges.addElement(new Integer(0));
			m_vHeight.addElement(new Integer(0));
		}
		m_vFaces = null;		
	}

	Vertex(float x)
	{
		this();
		m_vLoc.addElement(new Float(x));
	}

	Vertex(float x, float y)
	{
		this(x);
		m_vLoc.addElement(new Float(y));
	}

	Vertex(float x, float y, float z)
	{
		this(x, y);
		m_vLoc.addElement(new Float(z));
	}
	
	Vertex(Vertex pa, Vertex pb)
	{
		this();
		
		int nSize = pa.Dimensions();
	    		
		for(int i = 0; i < nSize; i++)
		{
			m_vLoc.addElement(new Float((pa.Dimension(i+1)+pb.Dimension(i+1))/2));
		}
	}

	public void FaceCheck()
	{
		int target;
		int prev;
		Edge m_eTemp;
		Face m_fTemp;
		int count;
		int i;
		Vertex m_vVectorB;
		Vertex m_vVectorW;
		boolean ok;
		
		ok = true;
		for (i = m_vEdges.size() - 1; i >= 0; i--)
		{
			if (((Edge)m_vEdges.elementAt(i)).Faces() != 2)
			{
				ok = false;
				break;
			}
		}		
		if (ok)
		{
			m_vFaces = new Vector();
			m_fTemp = ((Edge)m_vEdges.elementAt(0)).Face(1);
			if (m_fTemp.Color() == Color.black)
			{
				m_vFaces.addElement(m_fTemp);
			}
			else
			{
				m_vFaces.addElement(((Edge)m_vEdges.elementAt(0)).Face(2));
			}
			target = ((Face)m_vFaces.lastElement()).ID();
			prev = 0;
			for (count = m_vEdges.size() - 1; count > 0; count --)
			{
				for (i = m_vEdges.size() - 1; i > 0; i--)
				{
					if (prev == i)
					{
						i--;
					}
					m_eTemp = (Edge)m_vEdges.elementAt(i);
					m_fTemp = m_eTemp.Face(1);
					if (m_fTemp.ID() == target)
					{
						m_fTemp = m_eTemp.Face(2);
						m_vFaces.addElement(m_fTemp);
						prev = i;
						target = m_fTemp.ID();
						break;
					}
					m_fTemp = m_eTemp.Face(2);
					if (m_fTemp.ID() == target)
					{
						m_fTemp = m_eTemp.Face(1);
						m_vFaces.addElement(m_fTemp);
						prev = i;
						target = m_fTemp.ID();
						break;
					}
				}
			}
			m_vVectorB = ((Face)m_vFaces.firstElement()).Loc();
			m_vVectorB = new Vertex(m_vVectorB.X() - X(), m_vVectorB.Y() - Y());
			m_vVectorW = ((Face)m_vFaces.elementAt(1)).Loc();
			m_vVectorW = new Vertex(m_vVectorW.X() - X(), m_vVectorW.Y() - Y());
			if (0 < X() * (m_vVectorB.Y() * m_vVectorW.Z() - m_vVectorB.Z() * m_vVectorW.Y()) +
			        Y() * (m_vVectorB.Z() * m_vVectorW.X() - m_vVectorB.X() * m_vVectorW.Z()) +
                    (Z() + 0.001f) * (m_vVectorB.X() * m_vVectorW.Y() - m_vVectorB.Y() * m_vVectorW.X()))
			{
				Vector m_vTemp = m_vFaces;

				m_vFaces = new Vector();
				for (i = m_vEdges.size(); i > 0; i--)
				{
					m_vFaces.addElement(m_vTemp.elementAt(i % m_vEdges.size()));
				}
			}
		}
	}
	
	public boolean cycle()
	{
		boolean ok = true;

		for(int i = Edges(); i > 0; i--)
		{
			if(this.Edge(i).Faces() != 2)
			{
				return false;
			}
		}
		return true;
	}

	public boolean Rotatable(int figure)
	{
		if (this.REdges(figure) * 2 == this.Edges())
		{
			boolean ok = true;

			for(int i = Edges(); i > 0; i--)
			{
				if(this.Edge(i).Faces() != 2)
				{
					ok = false;
					break;
				}
			}
			return ok;
		}
		return false;
	}
		
	public boolean Raisable(int figure)
	{
		if(((Face)m_vFaces.elementAt(0)).Tile(figure) != (((Face)m_vFaces.elementAt(1)).Tile(figure)))
			{
				return true;
			}		
		return false;
	}

	public int Height(int figure)
	{
		return ((Integer)m_vHeight.elementAt(figure)).intValue();
	}

	public void Height(int figure, int height)
	{
		m_vHeight.setElementAt(new Integer(height), figure);
	}

	public float Dimension(int d)
	{
		return (float)(((Float)m_vLoc.elementAt(d-1)).floatValue());
	}

	public int ID()
	{
		return m_nID;
	}
	
	public float X()
	{
		return this.Dimension(1);
	}

	public float Y()
	{
		return this.Dimension(2);
	}

	public float Z()
	{
		if (Dimensions() < 3)
		{
			return 0f;
		}
		else
		{
			return this.Dimension(3);
		}
	}

	public int Dimensions()
	{
		return m_vLoc.size();
	}

	public void addDimension(int pa)
	{
		m_vLoc.addElement(new Float(pa));
	}

	public Edge Edge(int edge)
	{
	   return (Edge)m_vEdges.elementAt(edge - 1);
	}

	public Face Face(int face)
	{
		return (Face)m_vFaces.elementAt(face - 1);
	}

	public void addGhost(int figure)
	{
		m_vGEdges.setElementAt(new Integer(((Integer)m_vGEdges.elementAt(figure)).intValue() + 1), figure);
	}

	public void remGhost(int figure)
	{
		m_vGEdges.setElementAt(new Integer(((Integer)m_vGEdges.elementAt(figure)).intValue() - 1), figure);
	}		   

	public int Edges()
	{
		return m_vEdges.size();
	}
		
	public int REdges(int figure)
	{
		return m_vEdges.size() - ((Integer)m_vGEdges.elementAt(figure)).intValue();
	}

	public Vector Faces()
	{
		return m_vFaces;
	}

	public void paint(int figure, Color color, Graphics g)
	{
		int nX = figure * Tiling.adjust + (int)(Tiling.absminx + (Tiling.absmaxx - Tiling.absminx)/(Tiling.winmaxx - Tiling.winminx)*(((Float)(m_vLoc.elementAt(0))).floatValue() - Tiling.winminx));
		int nY = (int)(Tiling.absminy + (Tiling.absmaxy - Tiling.absminy)/(Tiling.winmaxy - Tiling.winminy)*(((Float)(m_vLoc.elementAt(1))).floatValue() - Tiling.winminy));
		
		g.setColor(Color.black);
		g.fillOval(nX - 3, nY - 3, 7, 7);
		g.setColor(color);
		g.fillOval(nX - 2, nY - 2, 5, 5);
		//g.drawLine(nX - 2, nY - 2, nX + 2, nY + 2);
		//g.drawLine(nX - 2, nY + 2, nX + 2, nY - 2);
		//g.drawString((new Integer(m_vFaces.size())).toString(), nX + 5, nY + 10);
	}

	public void MoveTo(Vector loc)
	{
		m_vLoc = loc;
	}

	public void AddEdge(Edge edge)
	{
		m_vEdges.addElement(edge);
	}

	public void Lower(int figure)
	{
	    Vector faces = Faces();
		Vector tiles = new Vector();
		int i;
		Face m_fTemp;
		Tile m_tTemp;

		for (i = faces.size() - 1; i >= 0; i-=2)
		{
			m_fTemp = (Face)faces.elementAt(i);
			m_tTemp = m_fTemp.Tile(figure);
			tiles.addElement(m_tTemp);
			m_tTemp.cleanup(figure);
		}
		m_tTemp = (Tile)tiles.elementAt(tiles.size()-1);
		m_tTemp.Reset(figure, (Face)faces.firstElement(), (Face)faces.lastElement());
		for (i = tiles.size() - 2; i >= 0; i--)
		{
			m_tTemp = (Tile)tiles.elementAt(i);
			m_tTemp.Reset(figure, (Face)faces.elementAt(2*i+1), (Face)faces.elementAt(2*i+2));
		}
		Tiling.m_vHeights.setElementAt(new Integer(((Integer)Tiling.m_vHeights.elementAt(figure)).intValue() - 1), figure);
	}
		   
	public void Raise(int figure)
	{
	    Vector faces = Faces();
		Vector tiles = new Vector();
		int i;
		Face m_fTemp;
		Tile m_tTemp;

		for (i = faces.size() - 1; i >= 0; i-=2)
		{
			m_fTemp = (Face)faces.elementAt(i);
			m_tTemp = m_fTemp.Tile(figure);
			tiles.addElement(m_tTemp);
			m_tTemp.cleanup(figure);
		}
		for (i = tiles.size() - 1; i >= 0; i--)
		{
			m_tTemp = (Tile)tiles.elementAt(i);
			m_tTemp.Reset(figure, (Face)faces.elementAt(2*i), (Face)faces.elementAt(2*i+1));
		}
		Tiling.m_vHeights.setElementAt(new Integer(((Integer)Tiling.m_vHeights.elementAt(figure)).intValue() + 1), figure);
	}
}

