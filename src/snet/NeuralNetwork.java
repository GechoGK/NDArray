package snet;

import java.util.*;

public class NeuralNetwork
{
	public List<Layer> layers=new ArrayList<>();

	public NeuralNetwork(LayerDesc desc)
	{
		prepFromDesc(desc);
	}
	public NeuralNetwork(int input, int layersCount, int layerNeuronsCount, int outputSize)
	{
		init(input, layersCount, layerNeuronsCount, outputSize);
	}
	public NeuralNetwork()
	{}
	private void init(int inp, int layerC, int neuC, int out)
	{
		int before=inp;
		for (int i=0;i < layerC;i++)
		{
			layers.add(new Layer(neuC, before, 0));
			before = neuC;
		}
		layers.add(new Layer(before, out, 0));
	}
	private void prepFromDesc(LayerDesc d)
	{
		int[][] lrs=d.getLayers();
		for (int[] l:lrs)
		{
			layers.add(new Layer(l[0], l[1], l[2]));
		}
	}
	public void addLayer(Layer l)
	{
		layers.add(l);
	}
	public float[] forward(float[]input)
	{
		for (Layer l:layers)
			input = l.forward(input);
		return input;
	}
}
