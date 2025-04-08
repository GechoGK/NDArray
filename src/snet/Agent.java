package snet;

public class Agent implements Comparable<Agent>
{
	public NeuralNetwork net;
	public boolean alive=false;
	public float rank=0;

	public Agent()
	{
		alive = true;
	}
	public Agent setNeuralNet(NeuralNetwork nn)
	{
		this.net = nn;
		reset();
		return this;
	}
	public float[] process(Object param)
	{
		float[][][] dt=(float[][][])param;
		float[][] prm=dt[0];
		float[][] tar=dt[1];
		float[] out=null;
		float rnk=0;
		for (int i=0;i < prm.length;i++)
		{
			float[] f=prm[i];
			out = net.forward(f);
			rnk += 1 - Math.abs(tar[i][0] - out[0]);
		}
		rank = rnk;
		return out;
	}
	public void reset()
	{
		rank = 0;
		alive = true;
	}
	@Override
	public int compareTo(Agent p1)
	{
		return Float.compare(p1.rank, rank);
	}
	public void rank(float[] out){
		
	}
}
