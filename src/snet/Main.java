package snet;

import java.util.*;
import java.io.*;

public class Main
{
	public static void main(String[]a)
	{
		new Main().a();
	}
	void a()
	{
		LayerDesc ld=new LayerDesc();
		ld.addLayer(2, 3);
		ld.addLayer(3, 10);
		ld.addLayer(10, 3);
		ld.addLayer(3, 1);
		// ^---- network description.

		Genetic gen=new Genetic(100, 0.75f);
		gen.generate(ld);
		int count=1;
		while (count > 0)
		{
			gen.process(null);
			gen.nextGen();
			// System.out.println("done!");
			// acc();
			if (count % 20 == 0)
			{
				int r=saveNet(gen.sortedNets.get(0));
				if (r == 0)
					System.out.println("saved generation " + gen.generationCount + "'s best");
			}
			count++;
		}

	}
	String acc()
	{
		return new Scanner(System.in).nextLine();
	}
	public int saveNet(NeuralNetwork nn)
	{
		int[]desc=new int[nn.layers.size() * 3];
		for (int l=0;l < nn.layers.size();l++)
		{
			int p=l * 3;
			desc[p + 0] = nn.layers.get(l).weights.length;
			desc[p + 1] = nn.layers.get(l).weights[0].length;
			desc[p + 2] = nn.layers.get(l).activation;
		}
		try
		{
			DataOutputStream dos=new DataOutputStream(new FileOutputStream("/sdcard/AppProjects/genNetBest.gsn"));
			dos.writeBytes("gss neural network");
			dos.writeByte(10);
			dos.writeInt(desc.length);
			for (int i:desc)
				dos.writeInt(i);
			for (int i=0;i < nn.layers.size();i++)
			{
				Layer l=nn.layers.get(i);
				// writing weight.
				for (float[] ff:l.weights)
					for (float f:ff)
						dos.writeFloat(f);
				// writing biase.
				for (float f:l.biase)
					dos.writeFloat(f);
			}
			return 0;
		}
		catch (Exception e)
		{
			System.out.println("unable to save :" + e);
		}
		return 1;
	}
	void nndesc()
	{
		LayerDesc ld=new LayerDesc();
		ld.addLayer(2, 5);
		ld.addLayer(5, 1);
		NeuralNetwork nn=new NeuralNetwork(ld);

		test(nn);
	}
	void nprep()	
	{
		int inputSize=2;
		int output=1;
		NeuralNetwork nn=new NeuralNetwork();
		nn.addLayer(new Layer(inputSize, 5));
		nn.addLayer(new Layer(5, output));

		test(nn);
	}
	void test(NeuralNetwork nn)
	{
		float[][] input={{0,0},{0,1},{1,0,},{1,1}};
		while (true)
		{
			float[] out=null;
			for (float[] in:input)
				out = nn.forward(in);
			System.out.println("== " + Arrays.toString(out));
		}
	}
}
