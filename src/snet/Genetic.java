package snet;

import java.util.*;
import java.util.stream.*;
import java.util.function.*;

public class Genetic
{
	public int popSize=100;
	public float selectionRatio=0.3f;
	public ArrayList<NeuralNetwork> networks;
	public ArrayList<NeuralNetwork> sortedNets;
	public ArrayList<Agent> agents;
	private LayerDesc desc;
	public float factor=0.1f;
	public int generationCount=0;

	public Genetic(float...args)
	{
		if (args.length >= 1)
			this.popSize = (int)args[0];
		if (args.length >= 2)
			this.selectionRatio = args[1];
		if (args.length >= 3)
			this.factor = args[2];
		else if (args.length >= 4)
		{
			throw new IllegalArgumentException("invalid argument length");
		}

	}
	public void generate(LayerDesc desc)
	{
		selectionRatio = Math.min(selectionRatio, 1);
		this.desc = desc;
		networks = new ArrayList<>(popSize);
		sortedNets = new ArrayList<>(popSize);
		agents = new ArrayList<>(popSize);
		for (int i=0;i < popSize;i++)
		{
			NeuralNetwork nn=new NeuralNetwork(desc);
			networks.add(nn);
			agents.add(new Agent().setNeuralNet(nn));
		}
		sortedNets.addAll(networks);
	}
	public void process(Object obj)
	{
		final float[][][]input={{{0,0},{0,1},{1,0},{1,1}},{{0},{1},{1},{0}}};
		agents.parallelStream().forEach(new Consumer<Agent>(){
				@Override
				public void accept(Agent p1)
				{
					if (p1.alive)
					{
						p1.process(input);
						// System.out.println(".");
					}
				}
				@Override
				public Consumer<Agent> andThen(Consumer<? super Agent> after)
				{
					return null;
				}
			});
		// System.out.println("breeding...");
		Collections.sort(agents);
		sortedNets.clear();
		for (Agent a:agents)
			sortedNets.add(a.net);
		System.out.println("rank ==" + agents.get(0).rank);
	}
	public void nextGen()
	{
		select();
		generationCount++;
	}
	public void select()
	{
		int selectionSize=(int)(popSize * selectionRatio);
		List<NeuralNetwork> nets=new ArrayList<>();
		for (int i=0;i < popSize;i++)
		{
			int p1=(int)(Math.random() * selectionSize);
			int p2=(int)(Math.random() * selectionSize);
			NeuralNetwork nn=crossBreed(agents.get(p1).net, agents.get(p2).net);
			nets.add(nn);
		}
		networks.clear();
		networks.addAll(nets);
		for (int i=0;i < networks.size();i++)
			agents.get(i).setNeuralNet(networks.get(i));
	}
	public NeuralNetwork crossBreed(NeuralNetwork...ns)
	{
		NeuralNetwork nn=new NeuralNetwork(desc);
		// cross Layer;
		for (int i=0;i < nn.layers.size();i++)
		{
			Layer l=nn.layers.get(i);
			float[] bs=ns[(int)(Math.random() * ns.length)].layers.get(i).biase;
			l.biase = Arrays.copyOf(bs, bs.length);
			for (int w=0;w < l.weights.length;w++)
			{
				int r=(int)(Math.random() * ns.length);
				float[] wc=ns[r].layers.get(i).weights[w];
				l.weights[w] = Arrays.copyOf(wc, wc.length);
			}
		}
		nn = mutuate(nn);
		return nn;
	}
	public NeuralNetwork mutuate(NeuralNetwork nn)
	{
		for (int l=0;l < nn.layers.size();l++)
		{
			// mutuate biase.
			NMath.mutuate(nn.layers.get(l).biase, factor);
			// mutuate weights.
			NMath.mutuate(nn.layers.get(l).weights, factor);
		}
		return nn;
	}
}
