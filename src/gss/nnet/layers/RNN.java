package gss.nnet.layers;

import gss.arr.*;
import gss.nnet.*;
import gss.nnet.activations.*;
import gss.test.*;

import static gss.math.Util.*;

public class RNN extends Module
{
	/*
	 this RNN class is always sequence first (not batchFirst);
	 batchFirst=false;
	 */
	private int hiddedSize,inputSize,layerSize;
	// private boolean batchFirst;

	private NDArray weight_ih,weight_hh,biase;

	private NDArray hidden_state;

	public RNN(int inputSize, int hiddenSize, int layerSize) //, boolean batchFirst)
	{
		this.inputSize = inputSize;
		this.hiddedSize = hiddenSize;
		this.layerSize = layerSize;
		// this.batchFirst = batchFirst;
		init();
	}
	public RNN(int inputSize, int hiddenSize)
	{
		this(inputSize, hiddenSize, 1);
		// this(inputSize, hiddenSize, 1, false);
	}
	private void init()
	{
		this.weight_ih = NDIO.rand(new int[]{inputSize, hiddedSize}, 123).setEnableGradient(true);
		this.weight_hh = NDIO.rand(new int[]{hiddedSize, hiddedSize}, 93).setEnableGradient(true);
		this.biase = NDIO.ones(hiddedSize);
		this.hidden_state = new NDArray(hiddedSize);
	}
	@Override
	public NDArray forward(NDArray input)
	{
		// batchFirst not implemented.
		int[]shp=input.getShape();
		if (shp.length < 2)
			shp = new int[]{1,shp[shp.length - 1]};
		input = input.reshape(-1, shp[shp.length - 2], shp[shp.length - 1]);
		// shape = (sequenceLength, batchSize, inputSize);
		shp = input.getShape();
		for (int sql=0;sql < shp[0];sql++) // sql(sequenceLength) 
			hidden_state = new Tanh().forward(input.get(sql).dot(weight_ih).add(hidden_state.dot(weight_hh)).add(biase));
		return hidden_state;
	}
	public static void test()
	{
		RNN r=new RNN(3, 1);

		NDArray input=NDIO.rand(new int[]{2,2,3}, 147);

		print(decString("Input", 35));
		print(input);
		print(decString("Result", 30));
		NDArray out=r.forward(input);

		print(out);
		print(decString("weights", 40));
		printGrad(r.weight_ih);
		printGrad(r.weight_hh);
		out.setGrad(1);
		out.backward();
		printGrad(r.weight_ih);
		printGrad(r.weight_hh);

		print(decString("Gradient Tree", 70));
		Test3.tree(out, "");

	}
}
