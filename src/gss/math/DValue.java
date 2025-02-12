package gss.math;

public class DValue extends Value
{
	private Data data;
	private int index;

	public DValue(Data d, int ind)
	{
		super(0);
		this.data = d;
		this.index = ind;
	}

	@Override
	public Value set(Value v)
	{
		super.set(v);
		data.setData(index, v.getData());
		return this;
	}

	@Override
	public float getGrad()
	{
		return data.getGrad(index);
	}
	@Override
	public void setGrad(float v)
	{
		data.setGrad(index, v);
	}
	@Override
	public float getData()
	{
		return data.getData(index);
	}

	@Override
	public String toString()
	{
		return super.toString() + " >> " + data;
	}
}
