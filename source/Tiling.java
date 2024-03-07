import java.applet.*;
import java.awt.*;
import java.util.*;

public class Tiling extends Applet implements Runnable
{
	final String PARAM_tiling = "tiling";
	final String PARAM_order = "order";
	final String PARAM_coloring = "coloring";
	final String PARAM_passdepth = "passdepth";
	
	static Vector m_vVertices;	  //  These characteristics	will not
	static Vector m_vEdges;		  //  change over multiple tilings
	static Vector m_vFaces;		  //  of the same region
	Vector m_vTiles;
	static Vector m_vHeights;
	  
	static int order;
	
	static double winminx;
	static double winmaxx;
	static double winminy;
	static double winmaxy;

	static int absminx;
	static int absmaxx;
	static int absminy;
	static int absmaxy;

	static int adjust;
	static int figures;
	static int tiling;
	static int coloring;

	private Image m_image;
	private Font font;
	private FontMetrics m_fm;

	private static boolean painted;
	private static boolean proceed;
	static boolean coupled;
	private static boolean random;
	private static boolean initialized;
	private static int firstextvertex;
	private static Bitbucket bucket;
	// These variables are only used by run, but must not be reset by stop()
	private static int[] oldmin;
	private static int[] oldmax;
	private static int[] newmin;
	private static int[] newmax;
	static byte [] flips;
	static int available;
	private static int cyclesize; 
	private static int pwpass; 
	private static int newend;
	private static int oldend;
	private static int jump;
	private static int maxheight;
	private static int record;
	private static int newmask;
	private static int iter;
	private static int cycle;
	private static int count;
	private static Vertex[] m_aCycles;			// Array of cycles
	private static int m_nCycles;				// How many?
				
	Thread m_Thread;
	  
	public String getAppletInfo()
	{
		return "Name: Tiling\r\n" +
		       "Author: Jason Woolever\r\n" +
		       "Created with Microsoft Visual J++ Version 1.0";
	}

	public String[][] getParameterInfo()
	{
		String[][] info =
		{
			{PARAM_tiling, "int", "tiling"},
			{PARAM_order, "int", "order"},
			{PARAM_coloring, "int", "coloring"},
			{PARAM_passdepth, "int", "passdepth"},
		};
		return info;
	}

	public void init()
	{
        String param;
		
		param = getParameter(PARAM_tiling);
		if (param != null)
		{
			tiling = Integer.parseInt(param);
		}
		else
		{
			tiling = 2;	 // 1: Square, 2: Aztec Diamond, 3: Hexagon, 4: Fortress, 5: Dungeon
		}
		param = getParameter(PARAM_order);
		if (param != null)
		{
			order = Math.max(1, Integer.parseInt(param));
		}
		else
		{
			order = 10;
		}
		param = getParameter(PARAM_coloring);
		if (param != null)
		{
			coloring = Integer.parseInt(param);
		}
		else
		{
			coloring = tiling;
		}
		param = getParameter(PARAM_passdepth);
		if (param != null)
		{
			cyclesize = Integer.parseInt(param);
		}
		else
		{
			cyclesize = 5;
		}
		if ((tiling == 1) && ((order % 2) == 1))
		{
			order++;
		}
		m_Thread = null;
		bucket = new Bitbucket();
		m_vVertices = new Vector();
		m_vEdges = new Vector();
		m_vFaces = new Vector();
		m_vTiles = new Vector();
		winminx = -order;
		winmaxx = order;
		winminy = -order;
		winmaxy = order;
		absminx = 5;
		absmaxx = 250;
		absminy = 3;
		absmaxy = 248;
		adjust = 256;
		cycle = 1000000000;
		available = 0x7FFFFFFF;
	    firstextvertex = 0;
	    m_vTiles.addElement(new Vector());
		figures = 4;
		m_vHeights = new Vector(figures);
		for (int i = 0; i < figures; i++)
		{
			m_vHeights.addElement(new Integer(0));
		}
		font = getFont();
		m_fm = getFontMetrics(font);
		oldmin = new int[257];
		oldmax = new int[257];
		oldend = 0;
		newmin = new int[513];
		newmax = new int[513];
		iter = -(1 << cyclesize);
		record=-1;
		pwpass = cyclesize - 1;
		newend = Math.min(512, 1 << cyclesize);
		oldend = 0;
		jump = Math.max(1, 1 << (9 - cyclesize));
		maxheight = 0;
		newmask = (1 << Math.max(0, pwpass - 8)) - 1; 
		painted = false;
		coupled = false;
		random = false;
		initialized = false;
		setBackground(Color.white);
	}

	public void destroy()
	{
		// TODO: Place applet cleanup code here
	}

	public void paint(Graphics g)
	{
		
		Vertex vertex;
		Edge edge;
		Face face;
		Tile tile;
		int i;
		String text;

		painted = false;
		/*if (!random)
		{
			g.drawString("Rot/sec:", 500, 140);
		}*/
		if (!coupled)
		{
			for (i = 0; i < 2; i++)
			{
				g.setColor(Color.black);
				text = "Height: " + ((Integer)m_vHeights.elementAt(i)).toString();
				g.drawString(text, (absmaxy + absminx - m_fm.stringWidth(text)) / 2 + adjust * i, absmaxy + 13);
			}
		}
		else
		{
			g.setColor(Color.black);
			text = "Height: " + ((Integer)m_vHeights.elementAt(0)).toString();
			g.drawString(text, (absmaxy + absminx - m_fm.stringWidth(text) + adjust) / 2, absmaxy + 13);
		}  
		for (i = ((Vector)m_vTiles.elementAt(0)).size() - 1; i >= 0; i--)
		{
			tile = (Tile) ((Vector)m_vTiles.elementAt(0)).elementAt(i);
			tile.paint(0, g);
			if (!coupled)
			{
				tile = (Tile) ((Vector)m_vTiles.elementAt(1)).elementAt(i);
				tile.paint(1, g);
			}
		}
		g.setColor(Color.black);
		g.drawLine(0, 274, 512, 274);
		g.drawLine(0, 364, 512, 364);
		g.drawLine(0, 274, 0, 364);
		g.drawLine(512, 274, 512, 364);
		g.setColor(Color.lightGray);
		g.drawLine(0, 319, 512, 319);
		if (oldmax[0] > 0)
		{
			for (i = 1; i <= oldend; i++)
			{
				g.setColor(Color.blue.darker());
				g.drawLine(jump * (i - 1) + 256, 364 - oldmin[i - 1], jump * i + 256, 364 - oldmin[i]);
				g.setColor(Color.red.darker());
				g.drawLine(jump * (i - 1) + 256, 364 - oldmax[i - 1], jump * i + 256, 364 - oldmax[i]);
			}
		}
		for (i = 1; i <= newend; i++)
		{
			if (newmax[i] == 0)
			{
				break;
			}
			if (newmin[i-1] == newmax[i-1])
			{
				g.setColor(Color.black);
			}
			else
			{
				g.setColor(Color.blue);
				g.drawLine(jump * (i - 1), 364 - newmin[i - 1], jump * i, 364 - newmin[i]);
				g.setColor(Color.red);
			}
			g.drawLine(jump * (i - 1), 364 - newmax[i - 1], jump * i, 364 - newmax[i]);
		}	
		/*for (i = m_vVertices.size() - 1; i >= 0; i--)
		{
			vertex = (Vertex)m_vVertices.elementAt(i);
			for (int j = 0; j <= 1; j++)
			{
				if (vertex.Rotatable(j))
				{
					if (vertex.Raisable(j))
					{
						vertex.paint(j, Color.green, g);
					}
					else
					{
						vertex.paint(j, Color.red, g);
					}
				}
			}
		}*/
		painted = true;
	}

	public void run()
	{
		int flip;
		Vertex test;
		int i;
		int j;
		boolean ra;
		boolean rb;
		long starttime;
		long totrots = 0;

		if (!initialized)
		{
			try
			{
				prepare();
			}
			catch (InterruptedException e)
			{
				stop();
			}
		}
		starttime = System.currentTimeMillis();
		while (proceed)
		{
			try
			{
				cycle++;
				count--;
				if (cycle >= m_nCycles)
				{
					cycle = 0;
					iter++;
					if ((iter & newmask) == 0)
					{
						record++;
						Graphics g=getGraphics();
						
						newmin[record] = 90 * ((Integer)m_vHeights.elementAt(0)).intValue()/maxheight;
						if (coupled)
						{
							newmax[record] = newmin[record];
						}
						else
						{
							newmax[record] = 90 * ((Integer)m_vHeights.elementAt(1)).intValue()/maxheight;
						}
						if (record > 0)
						{
							if (coupled)
							{
								g.setColor(Color.black);
							}
							else
							{
								g.setColor(Color.blue);
								g.drawLine(jump * (record - 1), 364 - newmin[record-1], jump * record, 364 - newmin[record]);
								g.setColor(Color.red);
							}
							g.drawLine(jump * (record - 1), 364 - newmax[record-1], jump * record, 364 - newmax[record]);
						}
						if (!coupled && (((Integer)m_vHeights.elementAt(0)).intValue() == ((Integer)m_vHeights.elementAt(1)).intValue()))
						{
							coupled = true;
							repaint();
							while (painted) {Thread.sleep(20);}
							while (!painted) {Thread.sleep(20);}
						}								
						if (iter > 0)
						{	
							if (!coupled)
							{
								for (i = (m_vFaces.size() / 2 - 1); i >= 0; i--)
								{
									((Tile)((Vector)m_vTiles.elementAt(0)).elementAt(i)).cleanup(0);
									((Tile)((Vector)m_vTiles.elementAt(1)).elementAt(i)).cleanup(1);
								}
								((Vector)m_vTiles.elementAt(0)).removeAllElements();
								((Vector)m_vTiles.elementAt(1)).removeAllElements();
								for (i = (m_vFaces.size() / 2 - 1); i >= 0; i--)
								{
									((Vector)m_vTiles.elementAt(0)).addElement(new Tile(0, ((Tile)((Vector)m_vTiles.elementAt(2)).elementAt(i)).Left(), ((Tile)((Vector)m_vTiles.elementAt(2)).elementAt(i)).Right())); 
									((Vector)m_vTiles.elementAt(1)).addElement(new Tile(1, ((Tile)((Vector)m_vTiles.elementAt(3)).elementAt(i)).Left(), ((Tile)((Vector)m_vTiles.elementAt(3)).elementAt(i)).Right())); 
								}
								m_vHeights.setElementAt(new Integer(0), 0);
								m_vHeights.setElementAt(new Integer(maxheight), 1);
								oldend = Math.min(newend, 256);
								jump = Math.max(1, jump >>> 1);
								if (newend == 512)
								{
									for (i = 0; i <= oldend; i++)
									{
										oldmin[i] = newmin[i * 2];
										oldmax[i] = newmax[i * 2];
									}
								}
								else
								{
									for (i = 0; i <= oldend; i++)
									{
										oldmin[i] = newmin [i];
										oldmax[i] = newmax [i];
									}
								}
								newend = 2 * oldend;
								newmin = new int[513];
								newmax = new int[513];
								repaint();	   							
								while (painted) {Thread.sleep(20);}
								while (!painted) {Thread.sleep(20);}
								cycle = 100000000;
								record = -1;
								pwpass++;
								newmask = (1 << Math.max(0, pwpass - 8)) - 1;
								iter = - (2 << pwpass);
								count = m_nCycles * (2 << pwpass) - 1;
								
								byte temp[] = flips;
								flips = new byte[(count + 1) / 8];
								for (i = 0; i < (count + 1) / 16; i++)
								{
									flips[i] = temp[i];
								}
								available = (count + 1) / 8;
								bucket.reset((count + 1) / 8 - 1);
								totrots = 0;
								starttime = System.currentTimeMillis();
							}
							else
							{
								((Vector)m_vTiles.elementAt(1)).removeAllElements();
								for (i = (m_vFaces.size() / 2 - 1); i >= 0; i--)
								{
									((Vector)m_vTiles.elementAt(1)).addElement(new Tile(1, ((Tile)((Vector)m_vTiles.elementAt(0)).elementAt(i)).Left(), ((Tile)((Vector)m_vTiles.elementAt(0)).elementAt(i)).Right())); 
								}
								m_vHeights.setElementAt(m_vHeights.elementAt(0), 1);
								random = true;
								cycle = 100000000;
								stop();
							}
						}									
					}										
                }
				while (!painted)
				{
					Thread.sleep(50);
				}
				if (cycle < 100000000)
				{
					test = m_aCycles[cycle];
					while (count/8 < available)
					{
						Thread.sleep(50);
					}
					flip = (flips[count/8] >>> (count%8)) & 1;
					ra = test.Rotatable(0);
					rb = test.Rotatable(1);
					if (flip == 1)
					{
						if(ra && test.Raisable(0))
						{
							test.Raise(0);
							totrots ++;
					        Update(0, test);
						}
						if(rb && !coupled && test.Raisable(1))
						{
							test.Raise(1);
							totrots ++;
					        Update(1, test);
						}						
					}
					else
					{
						if(ra && !test.Raisable(0))
						{
							test.Lower(0);
							totrots ++;
							Update(0, test);
						}
						if(rb && !coupled && !test.Raisable(1))
						{
							test.Lower(1);
							totrots ++;
						    Update(1, test);
						}						
					}
					if ((totrots % 5) == 4)		 // totrots % oddnum
					{
						if (!coupled)
						{
							Graphics g=getGraphics();
							g.setColor(Color.black);
							String text = "Height: " + ((Integer)m_vHeights.elementAt(0)).toString();
							int X = (absmaxx + absminx - m_fm.stringWidth(text)) / 2;
							int Y = absmaxy + 13;
							g.clearRect(X - 50, absmaxy + 1, 150, 20);
							g.drawString(text, X, Y);
							text = "Height: " + ((Integer)m_vHeights.elementAt(1)).toString();
							X = (absmaxx + absminx - m_fm.stringWidth(text)) / 2 + adjust;
							g.clearRect(X - 50, absmaxy + 1, 150, 20);
							g.drawString(text, X, Y);
							/*g.clearRect (550, 75, 50, 30);
							g.drawString(new Long(1000*totrots/((System.currentTimeMillis() - starttime))).toString(), 550, 100);*/
						}
						else
						{
							Graphics g=getGraphics();
							g.setColor(Color.black);
							String text = "Height: " + ((Integer)m_vHeights.elementAt(0)).toString();
							int X = (absmaxx + absminx - m_fm.stringWidth(text) + adjust) / 2;
							int Y = absmaxy + 13;
							g.clearRect(X - 50, absmaxy + 1, 150, 20);
							g.drawString(text, X, Y);
							/*g.clearRect (550, 75, 50, 30);
							g.drawString(new Long(1000*totrots/((System.currentTimeMillis() - starttime))).toString(), 550, 100);*/
						}
					}
				}
			}
			catch (InterruptedException e)
			{
				stop();
			}
		}
	}
	
	public void start()
	{
		proceed = true;
		m_Thread = new Thread(this);
		m_Thread.start();
	} 
	
	public void stop()
	{
		try
		{
			proceed = false;
			Thread.sleep(30);
			if (random)
			{
				Graphics g=getGraphics();
				g.setColor(Color.black);
				String text = "Height: " + ((Integer)m_vHeights.elementAt(0)).toString();
				int X = (absmaxx + absminx - m_fm.stringWidth(text) + adjust) / 2;
				int Y = absmaxy + 13;
				g.clearRect(X - 50, absmaxy + 1, 150, 20);
				g.drawString(text, X, Y);
			}
		}
		catch (InterruptedException e)
			{
				stop();
			}
		/*m_Thread.stop();
		m_Thread = null;*/
	} 

	public boolean mouseDown(Event evt, int x, int y)
	{
		int best = 0;
		int record = 1000000;
		int dist;
		int temp;
		Vertex m_vTemp;
		int figure = 0;

		/*while (x >= adjust)
		{
			x-=adjust;
			figure++;
		}
		for(int i = m_vVertices.size() - 1; i >= 0; i--)
		{
			dist = x - (int)(Tiling.absminx + (Tiling.absmaxx - Tiling.absminx)/(Tiling.winmaxx - Tiling.winminx)*(((Vertex)m_vVertices.elementAt(i)).X() - Tiling.winminx));
			dist *= dist;
			temp = y - (int)(Tiling.absminy + (Tiling.absmaxy - Tiling.absminy)/(Tiling.winmaxy - Tiling.winminy)*(((Vertex)m_vVertices.elementAt(i)).Y() - Tiling.winminy));
			temp *= temp;
			dist += temp;
			if (dist < record)
			{
				record = dist;
				best = i;
			}
		}
		m_vTemp = (Vertex)m_vVertices.elementAt(best); 
		if (m_vTemp.Rotatable(figure))
		{
			if (m_vTemp.Raisable(figure))
			{
				for(int i = 0; i < figures; i++)
				{
					if (m_vTemp.Rotatable(i) && m_vTemp.Raisable(i))
					{
						Raise(i, m_vTemp);
					} 					
				}
			}
			else
			{
				for(int i = 0; i < figures; i++)
				{
					if (m_vTemp.Rotatable(i) && !m_vTemp.Raisable(i))
					{
						Lower(figure, m_vTemp);
					}
					update(i, m_vTemp);
				}
			}
		}
		else*/
		{
			if (!random)
			{
				if (proceed)
				{
					stop();
				}
				else
				{
					start();
				}
			}
		}
		return true;
	}

	public boolean mouseUp(Event evt, int x, int y)
	{
		return true;
	}

	public void prepare()
	{
		Vector cyclevect = new Vector();
		int i;
		Face face;

		try
		{
			while (!painted)
			{
				Thread.sleep(30);
			}
		}
		catch (InterruptedException e)
		{
			stop();
		}
		switch (tiling)
		{
			case 1: Squareseed();
					for (i = m_vFaces.size() - 1; i >= 0; i--)
					{
						face = (Face) m_vFaces.elementAt(i);
						face.paint(0, getGraphics());
						face.paint(1, getGraphics());
					}
					for(i = 2; i < order; i+=2)
					{
						Extend();
						for(int j = m_vEdges.size() - 1; j >= 0; j--)
						{
							Edge edge = (Edge)m_vEdges.elementAt(j);
							if ((Math.max(Math.abs(edge.Left().X()), Math.abs(edge.Left().Y())) >= order) &&
								(Math.max(Math.abs(edge.Right().X()), Math.abs(edge.Right().Y())) >= order))
							{
								edge.extendable = false;
							}
						}
						Extend();
						for(int j = m_vEdges.size() - 1; j >= 0; j--)
						{
							Edge edge = (Edge)m_vEdges.elementAt(j);
							if ((Math.max(Math.abs(edge.Left().X()), Math.abs(edge.Left().Y())) >= order) &&
								(Math.max(Math.abs(edge.Right().X()), Math.abs(edge.Right().Y())) >= order))
							{
								edge.extendable = false;
							}
						}	 
					}
					break;					
			case 2: Diamondseed();
					for (i = m_vFaces.size() - 1; i >= 0; i--)
					{
						face = (Face) m_vFaces.elementAt(i);
						face.paint(0, getGraphics());
						face.paint(1, getGraphics());
					}
					for(i = 1; i < order; i++)
					{
						Extend();
					}
					break;
			case 3:	Hexagonseed();
					for (i = m_vFaces.size() - 1; i >= 0; i--)
					{
						face = (Face) m_vFaces.elementAt(i);
						face.paint(0, getGraphics());
						face.paint(1, getGraphics());
					}
					for(i = 1; i < order; i++)
					{
						Extend();
						Extend();
					}
					break;
			case 4:	Fortressseed();
					for (i = m_vFaces.size() - 1; i >= 0; i--)
					{
						face = (Face) m_vFaces.elementAt(i);
						face.paint(0, getGraphics());
						face.paint(1, getGraphics());
					}
					for(i = 1; i < order; i++)
					{
						for(int j = m_vEdges.size() - 1; j >= 0; j--)
						{
							Edge edge = (Edge)m_vEdges.elementAt(j);
							if (edge.extendable)
							{
								float s;

								if (Math.abs(1-Math.abs(edge.slope())) < 0.01)
								{
									edge.m_nKind = 2;
								}
							}
						}
						Extend();
						Extend();
						Extend();
						Extend();
					}
					break;
			case 5: Dungeonseed();
					for (i = m_vFaces.size() - 1; i >= 0; i--)
					{
						face = (Face) m_vFaces.elementAt(i);
						face.paint(0, getGraphics());
						face.paint(1, getGraphics());
					}
					for(i = 1; i < order; i++)
					{
						CheckAll();
						Retile();
						for(int j = m_vEdges.size() - 1; j >= 0; j--)
						{
							Edge edge = (Edge)m_vEdges.elementAt(j);
							
							if (edge.extendable)
							{
								float squaredlength, xdif, ydif;

								xdif = edge.Left().X() - edge.Right().X();
								ydif = edge.Left().Y() - edge.Right().Y();
								squaredlength = xdif*xdif + ydif*ydif;
								if (squaredlength > .1)
								{
									 edge.extendable = false;
								}
							}
						}
						Extend();
						Extend();
						Extend();
						Extend();
						Extend();
						Extend();
						for(int j = m_vEdges.size() - 1; j >= 0; j--)
						{
							Edge edge = (Edge)m_vEdges.elementAt(j);
							
							edge.m_nKind = 2;
						}
						for(int j = m_vVertices.size() - 1; j >= 0; j--)
						{
							Vertex vertex = (Vertex)m_vVertices.elementAt(j);
							
							if (vertex.Edges() == 2)
							{
								for(int k = 2; k > 0; k--)
								{
									Edge edge = vertex.Edge(k);
									float squaredlength, xdif, ydif;

									xdif = edge.Left().X() - edge.Right().X();
									ydif = edge.Left().Y() - edge.Right().Y();
									squaredlength = xdif*xdif + ydif*ydif;
									
									if (squaredlength > .3)
									{
										edge.m_nKind = 0;
									}
								}
							}
						}
						Extend();
						Extend();
					}
					CheckAll();
					break;					
		}
		for (i = m_vVertices.size() - 1; i >= 0; i--)
		{
			((Vertex)m_vVertices.elementAt(i)).FaceCheck();
		}
		Retile();
		m_vTiles.addElement(new Vector(((Vector)m_vTiles.elementAt(0)).size()));
		m_vTiles.addElement(new Vector(((Vector)m_vTiles.elementAt(0)).size()));
		m_vTiles.addElement(new Vector(((Vector)m_vTiles.elementAt(0)).size()));
		for (i = (m_vFaces.size() / 2 - 1); i >= 0; i--)
		{
		    ((Vector)m_vTiles.elementAt(1)).addElement(new Tile(1, ((Tile)((Vector)m_vTiles.elementAt(0)).elementAt(i)).Left(), ((Tile)((Vector)m_vTiles.elementAt(0)).elementAt(i)).Right())); 
			((Tile)((Vector)m_vTiles.elementAt(1)).lastElement()).paint(1, getGraphics());
		}
		maxheight = Maximize(1) - Minimize(0);
		m_vHeights.setElementAt(new Integer(maxheight), 1);
		m_vHeights.setElementAt(new Integer(0), 0);
		for (i = (m_vFaces.size() / 2 - 1); i >= 0; i--)
		{
		    ((Vector)m_vTiles.elementAt(2)).addElement(new Tile(2, ((Tile)((Vector)m_vTiles.elementAt(0)).elementAt(i)).Left(), ((Tile)((Vector)m_vTiles.elementAt(0)).elementAt(i)).Right())); 
		    ((Vector)m_vTiles.elementAt(3)).addElement(new Tile(3, ((Tile)((Vector)m_vTiles.elementAt(1)).elementAt(i)).Left(), ((Tile)((Vector)m_vTiles.elementAt(1)).elementAt(i)).Right())); 
		}
		for (i = m_vVertices.size() - 1; i >= 0; i--)
		{
			if (((Vertex)m_vVertices.elementAt(i)).cycle())
			{
				cyclevect.addElement(m_vVertices.elementAt(i));
			}
		}
		m_nCycles = cyclevect.size();
		cycle = m_nCycles - 1;
		count = m_nCycles * (2 << pwpass) - 1;
		flips = new byte[(count + 1) / 8];
		available = (count + 1) / 8;
		bucket.start();
		bucket.reset(available - 1);
		m_aCycles = new Vertex[m_nCycles];
		for (i = 0; i < m_nCycles; i++)
		{
			m_aCycles[i] = (Vertex)cyclevect.elementAt(m_nCycles - i - 1);
		}
		initialized = true;
	}			
	
	public void Squareseed()
	{
		Vector edges = new Vector();
		Color c1, c2;

		if ((order % 2) == 1)
		{
			c1 = Color.black;
			c2 = Color.lightGray;
		}
		else
		{
			c1 = Color.lightGray;
			c2 = Color.black;
		}
		m_vVertices.removeAllElements();
		m_vEdges.removeAllElements();
		m_vFaces.removeAllElements();
		for (int i = -1; i <= 1; i++)
		{
			for (int j = -1; j <= 1; j++)
			{
				m_vVertices.addElement(new Vertex(2*i, 2*j));
			}
		}
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(0), (Vertex)m_vVertices.elementAt(1)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(1), (Vertex)m_vVertices.elementAt(2)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(0), (Vertex)m_vVertices.elementAt(3)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(1), (Vertex)m_vVertices.elementAt(4)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(2), (Vertex)m_vVertices.elementAt(5)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(3), (Vertex)m_vVertices.elementAt(4)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(4), (Vertex)m_vVertices.elementAt(5)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(3), (Vertex)m_vVertices.elementAt(6)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(4), (Vertex)m_vVertices.elementAt(7)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(5), (Vertex)m_vVertices.elementAt(8)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(6), (Vertex)m_vVertices.elementAt(7)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(7), (Vertex)m_vVertices.elementAt(8)));
		edges.addElement(m_vEdges.elementAt(0));
		edges.addElement(m_vEdges.elementAt(2));
		edges.addElement(m_vEdges.elementAt(3));
		edges.addElement(m_vEdges.elementAt(5));
		m_vFaces.addElement(new Face(c1, edges));
		edges.removeAllElements();
		edges.addElement(m_vEdges.elementAt(1));
		edges.addElement(m_vEdges.elementAt(3));
		edges.addElement(m_vEdges.elementAt(4));
		edges.addElement(m_vEdges.elementAt(6));
		m_vFaces.addElement(new Face(c2, edges));
		edges.removeAllElements();
		edges.addElement(m_vEdges.elementAt(5));
		edges.addElement(m_vEdges.elementAt(7));
		edges.addElement(m_vEdges.elementAt(8));
		edges.addElement(m_vEdges.elementAt(10));
		m_vFaces.addElement(new Face(c2, edges));
		edges.removeAllElements();
		edges.addElement(m_vEdges.elementAt(6));
		edges.addElement(m_vEdges.elementAt(8));
		edges.addElement(m_vEdges.elementAt(9));
		edges.addElement(m_vEdges.elementAt(11));
		m_vFaces.addElement(new Face(c1, edges));
	}


	public void Diamondseed()
	{
		Vector edges = new Vector();   // Identical to squareseed, but smaller
		Color c1, c2;

		if ((order % 2) == 1)
		{
			c1 = Color.black;
			c2 = Color.lightGray;
		}
		else
		{
			c1 = Color.lightGray;
			c2 = Color.black;
		}
		m_vVertices.removeAllElements();
		m_vEdges.removeAllElements();
		m_vFaces.removeAllElements();
		for (int i = -1; i <= 1; i++)
		{
			for (int j = -1; j <= 1; j++)
			{
				m_vVertices.addElement(new Vertex(i, j));
			}
		}
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(0), (Vertex)m_vVertices.elementAt(1)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(1), (Vertex)m_vVertices.elementAt(2)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(0), (Vertex)m_vVertices.elementAt(3)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(1), (Vertex)m_vVertices.elementAt(4)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(2), (Vertex)m_vVertices.elementAt(5)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(3), (Vertex)m_vVertices.elementAt(4)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(4), (Vertex)m_vVertices.elementAt(5)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(3), (Vertex)m_vVertices.elementAt(6)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(4), (Vertex)m_vVertices.elementAt(7)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(5), (Vertex)m_vVertices.elementAt(8)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(6), (Vertex)m_vVertices.elementAt(7)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(7), (Vertex)m_vVertices.elementAt(8)));
		edges.addElement(m_vEdges.elementAt(0));
		edges.addElement(m_vEdges.elementAt(2));
		edges.addElement(m_vEdges.elementAt(3));
		edges.addElement(m_vEdges.elementAt(5));
		m_vFaces.addElement(new Face(c1, edges));
		edges.removeAllElements();
		edges.addElement(m_vEdges.elementAt(1));
		edges.addElement(m_vEdges.elementAt(3));
		edges.addElement(m_vEdges.elementAt(4));
		edges.addElement(m_vEdges.elementAt(6));
		m_vFaces.addElement(new Face(c2, edges));
		edges.removeAllElements();
		edges.addElement(m_vEdges.elementAt(5));
		edges.addElement(m_vEdges.elementAt(7));
		edges.addElement(m_vEdges.elementAt(8));
		edges.addElement(m_vEdges.elementAt(10));
		m_vFaces.addElement(new Face(c2, edges));
		edges.removeAllElements();
		edges.addElement(m_vEdges.elementAt(6));
		edges.addElement(m_vEdges.elementAt(8));
		edges.addElement(m_vEdges.elementAt(9));
		edges.addElement(m_vEdges.elementAt(11));
		m_vFaces.addElement(new Face(c1, edges));
	}
	
	
	public void Hexagonseed()
	{
		Vector edges = new Vector();
		float sqrt3 = (float) Math.sqrt(3);

		m_vVertices.removeAllElements();
		m_vEdges.removeAllElements();
		m_vFaces.removeAllElements();
		m_vVertices.addElement(new Vertex(0, 0));
		m_vVertices.addElement(new Vertex(0, -1));
		m_vVertices.addElement(new Vertex(sqrt3/2, -0.5f));
		m_vVertices.addElement(new Vertex(sqrt3/2, 0.5f));
		m_vVertices.addElement(new Vertex(0, 1));
		m_vVertices.addElement(new Vertex(-sqrt3/2, 0.5f));
		m_vVertices.addElement(new Vertex(-sqrt3/2, -0.5f));
		for (int i = 1; i <= 6; i++)
		{
			m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(0), (Vertex)m_vVertices.elementAt(i)));
		}
		for (int i = 1; i <= 6; i++)
		{
			m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(i), (Vertex)m_vVertices.elementAt((i % 6) + 1)));
		}
		for (int i = 0; i < 6; i++)
		{
			edges.addElement((Edge)m_vEdges.elementAt(i));
			edges.addElement((Edge)m_vEdges.elementAt((i + 1) % 6));
			edges.addElement((Edge)m_vEdges.elementAt(i + 6));
			m_vFaces.addElement(new Face((i % 2) == 0 ? Color.black : Color.lightGray, edges));
			edges.removeAllElements();
		}
	}

	public void Fortressseed()
	{
		Vector edges = new Vector();

		m_vVertices.removeAllElements();
		m_vEdges.removeAllElements();
		m_vFaces.removeAllElements();
		// Vertices: top to bottom, left to right
		m_vVertices.addElement(new Vertex(-0.5f, -1));
		m_vVertices.addElement(new Vertex(0, -1));
		m_vVertices.addElement(new Vertex(0.5f, -1));
		for (int i = -1; i <= 1; i++)
		{
			for (int j = -2; j <= 2; j++)
			{
				m_vVertices.addElement(new Vertex(j/2.0f, i/2.0f));
			}
		}
		m_vVertices.addElement(new Vertex(-0.5f, 1));
		m_vVertices.addElement(new Vertex(0, 1));
		m_vVertices.addElement(new Vertex(0.5f, 1));
		// Horizontal Edges: top to bottom, left to right
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(0), (Vertex)m_vVertices.elementAt(1)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(1), (Vertex)m_vVertices.elementAt(2)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(3), (Vertex)m_vVertices.elementAt(4)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(4), (Vertex)m_vVertices.elementAt(5)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(5), (Vertex)m_vVertices.elementAt(6)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(6), (Vertex)m_vVertices.elementAt(7)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(8), (Vertex)m_vVertices.elementAt(9)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(9), (Vertex)m_vVertices.elementAt(10)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(10), (Vertex)m_vVertices.elementAt(11)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(11), (Vertex)m_vVertices.elementAt(12)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(13), (Vertex)m_vVertices.elementAt(14)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(14), (Vertex)m_vVertices.elementAt(15)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(15), (Vertex)m_vVertices.elementAt(16)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(16), (Vertex)m_vVertices.elementAt(17)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(18), (Vertex)m_vVertices.elementAt(19)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(19), (Vertex)m_vVertices.elementAt(20)));
		// Verticle Edges: left to right, top to bottom
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(3), (Vertex)m_vVertices.elementAt(8)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(8), (Vertex)m_vVertices.elementAt(13)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(0), (Vertex)m_vVertices.elementAt(4)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(4), (Vertex)m_vVertices.elementAt(9)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(9), (Vertex)m_vVertices.elementAt(14)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(14), (Vertex)m_vVertices.elementAt(18)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(1), (Vertex)m_vVertices.elementAt(5)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(5), (Vertex)m_vVertices.elementAt(10)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(10), (Vertex)m_vVertices.elementAt(15)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(15), (Vertex)m_vVertices.elementAt(19)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(2), (Vertex)m_vVertices.elementAt(6)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(6), (Vertex)m_vVertices.elementAt(11)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(11), (Vertex)m_vVertices.elementAt(16)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(16), (Vertex)m_vVertices.elementAt(20)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(7), (Vertex)m_vVertices.elementAt(12)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(12), (Vertex)m_vVertices.elementAt(17)));
		// Diagonal Edges with + slope: left to right, top to bottom
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(3), (Vertex)m_vVertices.elementAt(0)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(13), (Vertex)m_vVertices.elementAt(9)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(9), (Vertex)m_vVertices.elementAt(5)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(5), (Vertex)m_vVertices.elementAt(2)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(18), (Vertex)m_vVertices.elementAt(15)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(15), (Vertex)m_vVertices.elementAt(11)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(11), (Vertex)m_vVertices.elementAt(7)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(20), (Vertex)m_vVertices.elementAt(17)));
		// Diagonal Edges with - slope: left to right, bottom to top
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(13), (Vertex)m_vVertices.elementAt(18)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(3), (Vertex)m_vVertices.elementAt(9)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(9), (Vertex)m_vVertices.elementAt(15)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(15), (Vertex)m_vVertices.elementAt(20)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(0), (Vertex)m_vVertices.elementAt(5)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(5), (Vertex)m_vVertices.elementAt(11)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(11), (Vertex)m_vVertices.elementAt(17)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(2), (Vertex)m_vVertices.elementAt(7)));
		// Faces: top to bottom, left to right
		// Row one:
		edges.addElement((Edge)m_vEdges.elementAt(2));
		edges.addElement((Edge)m_vEdges.elementAt(18));
		edges.addElement((Edge)m_vEdges.elementAt(32));
		m_vFaces.addElement(new Face(Color.black, edges));
		edges.removeAllElements();
		edges.addElement((Edge)m_vEdges.elementAt(3));
		edges.addElement((Edge)m_vEdges.elementAt(18));
		edges.addElement((Edge)m_vEdges.elementAt(44));
		m_vFaces.addElement(new Face(Color.lightGray, edges));
		edges.removeAllElements();
		edges.addElement((Edge)m_vEdges.elementAt(44));
		edges.addElement((Edge)m_vEdges.elementAt(0));
		edges.addElement((Edge)m_vEdges.elementAt(22));
		m_vFaces.addElement(new Face(Color.black, edges));
		edges.removeAllElements();
		edges.addElement((Edge)m_vEdges.elementAt(22));
		edges.addElement((Edge)m_vEdges.elementAt(1));
		edges.addElement((Edge)m_vEdges.elementAt(35));
		m_vFaces.addElement(new Face(Color.lightGray, edges));
		edges.removeAllElements();
		edges.addElement((Edge)m_vEdges.elementAt(35));
		edges.addElement((Edge)m_vEdges.elementAt(4));
		edges.addElement((Edge)m_vEdges.elementAt(26));
		m_vFaces.addElement(new Face(Color.black, edges));
		edges.removeAllElements();
		edges.addElement((Edge)m_vEdges.elementAt(26));
		edges.addElement((Edge)m_vEdges.elementAt(47));
		edges.addElement((Edge)m_vEdges.elementAt(5));
		m_vFaces.addElement(new Face(Color.lightGray, edges));
		edges.removeAllElements();
		// Row two:
		edges.addElement((Edge)m_vEdges.elementAt(16));
		edges.addElement((Edge)m_vEdges.elementAt(6));
		edges.addElement((Edge)m_vEdges.elementAt(41));
		m_vFaces.addElement(new Face(Color.black, edges));
		edges.removeAllElements();
		edges.addElement((Edge)m_vEdges.elementAt(41));
		edges.addElement((Edge)m_vEdges.elementAt(2));
		edges.addElement((Edge)m_vEdges.elementAt(19));
		m_vFaces.addElement(new Face(Color.lightGray, edges));
		edges.removeAllElements();
		edges.addElement((Edge)m_vEdges.elementAt(19));
		edges.addElement((Edge)m_vEdges.elementAt(3));
		edges.addElement((Edge)m_vEdges.elementAt(34));
		m_vFaces.addElement(new Face(Color.black, edges));
		edges.removeAllElements();
		edges.addElement((Edge)m_vEdges.elementAt(34));
		edges.addElement((Edge)m_vEdges.elementAt(7));
		edges.addElement((Edge)m_vEdges.elementAt(23));
		m_vFaces.addElement(new Face(Color.lightGray, edges));
		edges.removeAllElements();
		edges.addElement((Edge)m_vEdges.elementAt(23));
		edges.addElement((Edge)m_vEdges.elementAt(8));
		edges.addElement((Edge)m_vEdges.elementAt(45));
		m_vFaces.addElement(new Face(Color.black, edges));
		edges.removeAllElements();
		edges.addElement((Edge)m_vEdges.elementAt(45));
		edges.addElement((Edge)m_vEdges.elementAt(4));
		edges.addElement((Edge)m_vEdges.elementAt(27));
		m_vFaces.addElement(new Face(Color.lightGray, edges));
		edges.removeAllElements();
		edges.addElement((Edge)m_vEdges.elementAt(27));
		edges.addElement((Edge)m_vEdges.elementAt(5));
		edges.addElement((Edge)m_vEdges.elementAt(38));
		m_vFaces.addElement(new Face(Color.black, edges));
		edges.removeAllElements();
		edges.addElement((Edge)m_vEdges.elementAt(38));
		edges.addElement((Edge)m_vEdges.elementAt(9));
		edges.addElement((Edge)m_vEdges.elementAt(30));
		m_vFaces.addElement(new Face(Color.lightGray, edges));
		edges.removeAllElements();
		// Row three:
		edges.addElement((Edge)m_vEdges.elementAt(17));
		edges.addElement((Edge)m_vEdges.elementAt(6));
		edges.addElement((Edge)m_vEdges.elementAt(33));
		m_vFaces.addElement(new Face(Color.lightGray, edges));
		edges.removeAllElements();
		edges.addElement((Edge)m_vEdges.elementAt(33));
		edges.addElement((Edge)m_vEdges.elementAt(20));
		edges.addElement((Edge)m_vEdges.elementAt(10));
		m_vFaces.addElement(new Face(Color.black, edges));
		edges.removeAllElements();
		edges.addElement((Edge)m_vEdges.elementAt(20));
		edges.addElement((Edge)m_vEdges.elementAt(42));
		edges.addElement((Edge)m_vEdges.elementAt(11));
		m_vFaces.addElement(new Face(Color.lightGray, edges));
		edges.removeAllElements();
		edges.addElement((Edge)m_vEdges.elementAt(42));
		edges.addElement((Edge)m_vEdges.elementAt(7));
		edges.addElement((Edge)m_vEdges.elementAt(24));
		m_vFaces.addElement(new Face(Color.black, edges));
		edges.removeAllElements();
		edges.addElement((Edge)m_vEdges.elementAt(24));
		edges.addElement((Edge)m_vEdges.elementAt(8));
		edges.addElement((Edge)m_vEdges.elementAt(37));
		m_vFaces.addElement(new Face(Color.lightGray, edges));
		edges.removeAllElements();
		edges.addElement((Edge)m_vEdges.elementAt(37));
		edges.addElement((Edge)m_vEdges.elementAt(12));
		edges.addElement((Edge)m_vEdges.elementAt(28));
		m_vFaces.addElement(new Face(Color.black, edges));
		edges.removeAllElements();
		edges.addElement((Edge)m_vEdges.elementAt(28));
		edges.addElement((Edge)m_vEdges.elementAt(13));
		edges.addElement((Edge)m_vEdges.elementAt(46));
		m_vFaces.addElement(new Face(Color.lightGray, edges));
		edges.removeAllElements();
		edges.addElement((Edge)m_vEdges.elementAt(46));
		edges.addElement((Edge)m_vEdges.elementAt(9));
		edges.addElement((Edge)m_vEdges.elementAt(31));
		m_vFaces.addElement(new Face(Color.black, edges));
		edges.removeAllElements();
		// Row four:
		edges.addElement((Edge)m_vEdges.elementAt(40));
		edges.addElement((Edge)m_vEdges.elementAt(10));
		edges.addElement((Edge)m_vEdges.elementAt(21));
		m_vFaces.addElement(new Face(Color.lightGray, edges));
		edges.removeAllElements();
		edges.addElement((Edge)m_vEdges.elementAt(21));
		edges.addElement((Edge)m_vEdges.elementAt(11));
		edges.addElement((Edge)m_vEdges.elementAt(36));
		m_vFaces.addElement(new Face(Color.black, edges));
		edges.removeAllElements();
		edges.addElement((Edge)m_vEdges.elementAt(36));
		edges.addElement((Edge)m_vEdges.elementAt(14));
		edges.addElement((Edge)m_vEdges.elementAt(25));
		m_vFaces.addElement(new Face(Color.lightGray, edges));
		edges.removeAllElements();
		edges.addElement((Edge)m_vEdges.elementAt(25));
		edges.addElement((Edge)m_vEdges.elementAt(15));
		edges.addElement((Edge)m_vEdges.elementAt(43));
		m_vFaces.addElement(new Face(Color.black, edges));
		edges.removeAllElements();
		edges.addElement((Edge)m_vEdges.elementAt(43));
		edges.addElement((Edge)m_vEdges.elementAt(12));
		edges.addElement((Edge)m_vEdges.elementAt(29));
		m_vFaces.addElement(new Face(Color.lightGray, edges));
		edges.removeAllElements();
		edges.addElement((Edge)m_vEdges.elementAt(29));
		edges.addElement((Edge)m_vEdges.elementAt(13));
		edges.addElement((Edge)m_vEdges.elementAt(39));
		m_vFaces.addElement(new Face(Color.black, edges));
	}
		

	public void Dungeonseed()
	{
		Vector edges = new Vector();
		float sqrt3 = (float) Math.sqrt(3);
		
		m_vVertices.addElement(new Vertex(0, .25f));
		m_vVertices.addElement(new Vertex(0, -.25f));
		m_vVertices.addElement(new Vertex(-sqrt3/6, -.25f));
		m_vVertices.addElement(new Vertex(sqrt3/6, -.25f));
		m_vVertices.addElement(new Vertex(sqrt3/4, .5f));
		m_vVertices.addElement(new Vertex(sqrt3/3, .25f));
		m_vVertices.addElement(new Vertex(sqrt3/6, .75f));
		m_vVertices.addElement(new Vertex(-sqrt3/4, .5f));
		m_vVertices.addElement(new Vertex(-sqrt3/3, .25f));
		m_vVertices.addElement(new Vertex(-sqrt3/6, .75f));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(0), (Vertex)m_vVertices.elementAt(1)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(1), (Vertex)m_vVertices.elementAt(2)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(2), (Vertex)m_vVertices.elementAt(0)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(0), (Vertex)m_vVertices.elementAt(3)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(1), (Vertex)m_vVertices.elementAt(3)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(0), (Vertex)m_vVertices.elementAt(4)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(0), (Vertex)m_vVertices.elementAt(5)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(4), (Vertex)m_vVertices.elementAt(5)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(0), (Vertex)m_vVertices.elementAt(6)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(4), (Vertex)m_vVertices.elementAt(6)));
		m_vEdges.addElement(new Edge(false, (Vertex)m_vVertices.elementAt(0), (Vertex)m_vVertices.elementAt(7)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(0), (Vertex)m_vVertices.elementAt(9)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(7), (Vertex)m_vVertices.elementAt(9)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(0), (Vertex)m_vVertices.elementAt(8)));
		m_vEdges.addElement(new Edge(true, (Vertex)m_vVertices.elementAt(7), (Vertex)m_vVertices.elementAt(8)));
		for (int i = 0; i < 3; i++)
		{
			edges.addElement((Edge)m_vEdges.elementAt(0 + 5*i));
			edges.addElement((Edge)m_vEdges.elementAt(1 + 5*i));
			edges.addElement((Edge)m_vEdges.elementAt(2 + 5*i));
			m_vFaces.addElement(new Face(Color.black, edges));
			edges.removeAllElements();
			edges.addElement((Edge)m_vEdges.elementAt(0 + 5*i));
			edges.addElement((Edge)m_vEdges.elementAt(3 + 5*i));
			edges.addElement((Edge)m_vEdges.elementAt(4 + 5*i));
			m_vFaces.addElement(new Face(Color.lightGray, edges));
			edges.removeAllElements();
		}
		Extend();
	}	   

	public void Update(int figure, Vertex vertex)
	{
		Graphics g = getGraphics();
		int i;

		for (i = vertex.Edges(); i > 0; i-=2)
		{
			vertex.Face(i).Tile(figure).paint(figure, g);
		}
	}
	   
	public void Extend()
	{
		Edge edge;
		Edge edge2;
		Vector newedges;
		Vector extedges = new Vector();
		Vector othedges = new Vector();
		Vector vertices = new Vector();
		Face face;
		Vertex vertex;
		Vertex vertex2;
		Vertex pvertex = null;
		float nX;
		float nY;
		float m;
		float xa;
		float ya;
		float xb;
		float yb;
		float xp;
		float yp;
		float xm;
		float ym;
		int i;
		int j;
		int k;
		int l;

		for (i = m_vEdges.size() - 1; i >= 0; i--)
		{
			edge = (Edge)m_vEdges.elementAt(i);
			if (edge.extendable)
			{
				if (edge.m_nKind > 0)
				{
					edge.m_nKind--;
					othedges.addElement(edge);
				}
				else
				{
					extedges.addElement(edge);
				}
			}
		}
		for (i = 0; i < extedges.size(); i++)
		{
			edge = (Edge)extedges.elementAt(i);
			vertex = edge.Left();
			xa = vertex.X();
			ya = vertex.Y();
			vertex = edge.Right();
			xb = vertex.X();
			yb = vertex.Y();
			m = (ya - yb) / (xa - xb);
			newedges = new Vector();
			face = edge.Face(1);
			vertex = face.Vertex(1);
			// Calculate first reflected vertex
			xp = vertex.X();
			yp = vertex.Y();
			if (Math.abs(xa - xb) < 0.01)
			{
				nX = 2*xa - xp;
				nY = yp;
			}
			else
			{
				xm = xa + (xp - xa + m*(yp - ya)) / (m*m + 1);
				ym = m*xm - m*xa + ya;
				nX = 2*xm - xp;
				nY = 2*ym - yp;
			}
			for (k = m_vVertices.size() - 1; k >= 0; k--)
			{
				pvertex = (Vertex) m_vVertices.elementAt(k);
				if ((Math.abs(nX - (pvertex.X())) < 0.01) && (Math.abs(nY - (pvertex.Y())) < 0.01))
				{
					 break;
				}
			}
			if (k < 0)
			{
				pvertex = new Vertex(nX, nY);
				m_vVertices.addElement(pvertex);
			}
			for (j = face.Edges(); j > 0; j--)
			{
				vertex = face.Vertex(j);
				xp = vertex.X();
				yp = vertex.Y();
				if (Math.abs(xa - xb) < 0.01)
				{
					nX = 2*xa - xp;
					nY = yp;
				}
				else
				{
					xm = xa + (xp - xa + m*(yp - ya)) / (m*m + 1);
					ym = m*xm - m*xa + ya;
					nX = 2*xm - xp;
					nY = 2*ym - yp;
				}
				vertex2 = null;
				edge2 = null;
out:			{
					xp = pvertex.X();
					yp = pvertex.Y();
					for (k = m_vVertices.size() - 1; k >= firstextvertex; k--)
					{
						if ((Math.abs(nX - ((Vertex)m_vVertices.elementAt(k)).X()) < 0.01) && (Math.abs(nY - ((Vertex)m_vVertices.elementAt(k)).Y()) < 0.01))
						{
							vertex2 = (Vertex)m_vVertices.elementAt(k);
							for (l = vertex2.Edges(); l > 0; l--)
							{
								edge2 = vertex2.Edge(l);
								if ((Math.abs(edge2.Other(vertex2).X() - xp) < 0.01) && (Math.abs(edge2.Other(vertex2).Y() - yp) < 0.01))
								{
									int hit;
									
									edge2.extendable = false;
									hit = extedges.indexOf(edge2);
									if (hit > -1)
									{
										extedges.removeElementAt(hit);
										if (hit <= i)
										{
											i--;
										}
									}
									break out;
								}
							}
							edge2 = new Edge(true, pvertex, vertex2);
							m_vEdges.addElement(edge2);
							break out; 						
						}
					}
					vertex2 = new Vertex(nX, nY);
					m_vVertices.addElement(vertex2);
					edge2 = new Edge(true, pvertex, vertex2);
					m_vEdges.addElement(edge2);
				}
				pvertex = vertex2;
				newedges.addElement(edge2);
			}
			m_vFaces.addElement(new Face(face.Color() == Color.lightGray ? Color.black : Color.lightGray, newedges));
			if (((Face)m_vFaces.lastElement()).Tile(0) == null)
			{
				((Face)m_vFaces.lastElement()).paint(0, getGraphics());
			}
			((Face)m_vFaces.lastElement()).paint(1, getGraphics());
		}
out:
		for (i = firstextvertex; i < m_vVertices.size(); i++)
		{
			vertex = (Vertex)m_vVertices.elementAt(i);
			for (j = vertex.Edges(); j > 0; j--)
			{
				if (vertex.Edge(j).Faces() < 2)
				{
					break out;
				}
			}
			firstextvertex++;
		}			
	}

	public void Retile()
	{
		int i, j, k;
		int stop = m_vFaces.size();
		Face on, test;

		//m_vTiles.removeAllElements();
		//m_vTiles.addElement(new Vector());
		SortFaces(0, stop - 1);
		for (i = 0; i < stop; i++)
		{
			on = (Face)(m_vFaces.elementAt(i));
			if (on.Tile(0) == null)
			{
out:
				for (j = i + 1; j < stop; j++)
				{
					test = (Face)(m_vFaces.elementAt(j));
					if (test.Tile(0) == null)
					{
						for (k = on.Edges(); k > 0; k--)
						{
							if (on.EdgeNum(k).Other(on) == test)
							{
								((Vector)m_vTiles.elementAt(0)).addElement(new Tile(0, on, test));
								((Tile)((Vector)m_vTiles.elementAt(0)).lastElement()).paint(0, getGraphics());
								CheckConnects(on);
								CheckConnects(test);
								break out;
							}
						}
					}
				}
			}		
		}
	}


	public void CheckConnects(Face face)
	{
		int i, j, count;
		Face test, temp;

		for (i = face.Edges(); i > 0; i--)
		{
			test = face.EdgeNum(i).Other(face);
			if ((test!= null) && (test.Tile(0) == null))
			{
				count = 0;
				for (j = test.Edges(); j > 0; j--)
				{
					temp = test.EdgeNum(j).Other(test);
					if ((temp!=null) && (temp.Tile(0) == null))
					{
						count++;
					}
				}
				if (count == 1)
				{
					for (j = test.Edges(); j > 0; j--)
					{
						temp = test.EdgeNum(j).Other(test);
						if ((temp!=null) && (temp.Tile(0) == null))
						{
							((Vector)m_vTiles.elementAt(0)).addElement(new Tile(0, test, test.EdgeNum(j).Other(test)));
							((Tile)((Vector)m_vTiles.elementAt(0)).lastElement()).paint(0, getGraphics());
							CheckConnects(test);
							CheckConnects(test.EdgeNum(j).Other(test));
							break;
						}
					}
				}
			}
		}
	}		

	
	public void CheckAll()
	{
		int i, j, count;
		Face test, temp;

		for (i = m_vFaces.size() - 1; i >= 0; i--)
		{
			test = (Face)m_vFaces.elementAt(i);
			if (test.Tile(0) == null)
			{
				count = 0;
				for (j = test.Edges(); j > 0; j--)
				{
					temp = test.EdgeNum(j).Other(test);
					if ((temp!=null) && (temp.Tile(0) == null))
					{
						count++;
					}
				}
				if (count == 1)
				{
					for (j = test.Edges(); j > 0; j--)
					{
						temp = test.EdgeNum(j).Other(test);
						if ((temp!=null) && (temp.Tile(0) == null))
						{
							((Vector)m_vTiles.elementAt(0)).addElement(new Tile(0, test, test.EdgeNum(j).Other(test)));
							((Tile)((Vector)m_vTiles.elementAt(0)).lastElement()).paint(0, getGraphics());
							CheckConnects(test);
							CheckConnects(test.EdgeNum(j).Other(test));
							break;
						}
					}
				}
			}
		}
	}

	/*public void printStatus(long seed)
	{
		System.out.print("Iter ");
		System.out.print(iter);
		System.out.print("  Seed ");
		System.out.println(seed);
	} */
		
	public void SortFaces(int start, int end)
	{
		Face center, test;
		float x, y, diff;
		Vector before;
		Vector after;
		int i, befsize, aftsize;
		
		before = new Vector();
		after = new Vector();
		center = (Face)m_vFaces.elementAt(end);
		x = center.Loc().X();
		y = center.Loc().Y();
		for (i = start; i < end; i++)
		{
			test = (Face)m_vFaces.elementAt(i);
			diff = test.Loc().Y() - y;
			if ((diff > 0.01) || ((Math.abs(diff) <= 0.01) && (test.Loc().X() > x)))
			{
				after.addElement(test);
			}
			else
			{
				before.addElement(test);
			}
		}
		befsize = before.size(); 
		for (i = 0; i < befsize; i++)
		{
			m_vFaces.setElementAt(before.elementAt(i), start + i);
		}
		m_vFaces.setElementAt(center, start + befsize);
		aftsize = after.size();
		for (i = 0; i < aftsize; i++)
		{
			m_vFaces.setElementAt(after.elementAt(i), end - i);
		}
		if (befsize > 1)
		{
			SortFaces (start, start + befsize - 1);
		}
		if (aftsize > 1)
		{
			SortFaces (end - aftsize + 1, end);
		}
	}

			
	static public Color Color(Edge edge)
	{
		if (coloring == 1)	// Square
		{
			if (edge.Left().X() != edge.Right().X())
			{
				if (Math.abs((Math.min(edge.Left().X() + edge.Left().Y(), 
				            edge.Right().X() + edge.Right().Y()) % 4)) == (order % 4))
				{
					return Color.red;
				}
				else
				{
					return Color.green;
				}
			}
			else
			{
				if (Math.abs((Math.min(edge.Left().X() + edge.Left().Y(), 
							  edge.Right().X() + edge.Right().Y()) % 4)) == (order % 4))
				{
					return Color.yellow;
				}
				else
				{
					return Color.blue;
				}
			}
		}
		else
		if (coloring == 2)	// Aztec Diamond
		{
			if (edge.Left().X() != edge.Right().X())
			{
				if (Math.abs((Math.min(edge.Left().X() + edge.Left().Y(), 
				            edge.Right().X() + edge.Right().Y()) % 2)) == (order % 2))
				{
					return Color.red;
				}
				else
				{
					return Color.green;
				}
			}
			else
			{
				if (Math.abs((Math.min(edge.Left().X() + edge.Left().Y(), 
							  edge.Right().X() + edge.Right().Y()) % 2)) == (order % 2))
				{
					return Color.yellow;
				}
				else
				{
					return Color.blue;
				}
			}
		}
		else
		if (coloring == 3)  // Hexagon
		{
			if (Math.abs(edge.Left().X() - edge.Right().X()) < 0.01)
			{
				return Color.green;
			}
			else
			{
				if (0 < (edge.Left().X() - edge.Right().X()) * (edge.Left().Y() - edge.Right().Y()))
				{
					return Color.blue;
				}
				else
				{
					return Color.red;
				}
			}
		}
		else
		if (coloring == 4)  // Fortress: Aztec style
		{
			if (Math.abs(1-Math.abs(edge.slope())) < 0.01)
			{
				return Color.gray;
			}
			else
			if (0.001 > Math.abs(edge.Left().X() - edge.Right().X()))
			{
				if ((int)(2*(Math.max(edge.Left().Y(), edge.Right().Y()) % 1)) == 0)
				{
					return Color.blue;
				}
				else
				{
					return Color.yellow;
				}
			}
			else
			{
				if ((int)(2*(Math.max(edge.Left().X(), edge.Right().X()) % 1)) == 0)
				{
					return Color.green;
				}
				else
				{
					return Color.red;
				}
			}
		}
		else
		if (coloring == 5)  // Dungeon
		{
			float squareddist, difx, dify;

			difx = edge.Left().X() - edge.Right().X();
			dify = edge.Left().Y() - edge.Right().Y();
			squareddist = difx*difx + dify*dify;

			if (squareddist > 0.3)
			{
				return Color.red;
			}
			else
			if (squareddist > 0.2)
			{
				return Color.blue;
			}
			else
			{
				return Color.green;
			}
		}
		return Color.gray;
	}
	
	public int Minimize(int figure)
	{
		int height = 0;
		int raise;
		int i;
		Vector region = (Vector)m_vTiles.elementAt(figure);
		int vertices = m_vVertices.size();
		Vertex test;
		
		do
		{
			raise = 0;
			for (i = 0; i < vertices; i++)
			{
				test = (Vertex)m_vVertices.elementAt(i);
				if (test.Rotatable(figure))
				{
					if (!test.Raisable(figure))
					{
						test.Lower(figure);
						Update(figure, test);
						raise--;
					}
				}
			}
			height += raise;
		}
		while (raise != 0);
		return height;
	} 
		
	public int Maximize(int figure)
	{
		int height = 0;
		int raise;
		int i;
		Vector region = (Vector)m_vTiles.elementAt(figure);
		int vertices = m_vVertices.size();
		Vertex test;
		
		do
		{
			raise = 0;
			for (i = 0; i < vertices; i++)
			{
				test = (Vertex)m_vVertices.elementAt(i);
				if (test.Rotatable(figure))
				{
					if (test.Raisable(figure))
					{
						test.Raise(figure);
						Update(figure, test);
						raise++;
					}
				}
			}
			height += raise;
		}
		while (raise != 0);
		return height;
	} 	
}