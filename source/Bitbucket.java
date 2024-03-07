/*
 *
 * Bitbucket
 *
 */
class Bitbucket implements Runnable
{
	randomX	rand = new randomJava();  // Experiencing some weird effects with randomLEcuyer(), I reverted to Java's basic psuedorandom generator
	int mode = 0;   /* pseudorandom */
	int gountil, max;
	Thread m_Thread;

	public void init()
	{
		gountil = max = 0;
		System.out.println("Bitbucket::init()");
	}

	public void run()
	{
		try
		{
			while(true)
			{
				if (Tiling.available > gountil)
				{
					Tiling.flips[--Tiling.available] = rand.nextByte();
				}
				else
				{
					Tiling.available = 0;
					Thread.sleep(50);
				}
			}
		}
		catch (InterruptedException e)
		{				
		}
	}

	public void start()
	{
		m_Thread = new Thread(this);
		m_Thread.start();
	}

	public void stop()
	{
		m_Thread.stop();
		m_Thread = null;
	}

	public void reset(int number)
	{
		gountil = max;
		max = number;
	}

	public void newgenerator()
	{
		if (mode == 0)
		{
			mode = 1;
			rand = new randomHotBits();
		}
		else
		{
			mode = 0;
			rand = new randomJava();
		}
	}

	public void setmode(int setting)
	{
		mode = setting;
		if (mode == 0)
		{
			rand = new randomJava();
		}
		else
		{
			rand = new randomHotBits();
		}
	}
}

