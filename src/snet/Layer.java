package snet;

public class Layer
{
	public int activation;
	public float[][]weights;
	public float[] biase;

	public Layer(int input, int output, int activation)
	{
		init(input, output);
		this.activation = activation;
	}
	public Layer(int input, int output)
	{
		init(input, output);
	}
	private void init(int input, int output)
	{
		this.weights = new float[input][output];
		NMath.randomize(weights);
		this.biase = new float[output];
		NMath.randomize(biase);
	}
	public float[] forward(float[] input)
	{
		float[] out=NMath.dot(input, weights);
		out = NMath.add(out, biase);
		if (activation == 0)
			out = NMath.sigmoid(out);
		else if (activation == 1)
			out = NMath.tanh(out);
		else if (activation == 2)
			out = NMath.relu(out);
		else out = NMath.sigmoid(out);
		return out;
	}
}
