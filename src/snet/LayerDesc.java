package snet;

import java.util.*;

public class LayerDesc
{
	public ArrayList<int[]>layers=new ArrayList<>();

	public void addLayer(int in, int out, int activation)
	{
		layers.add(new int[]{in,out,activation});
	}
	public void addLayer(int in, int out)
	{
		addLayer(in, out, 0);
	}
	public int[][]getLayers()
	{
		return layers.toArray(new int[0][]);
	}
}
