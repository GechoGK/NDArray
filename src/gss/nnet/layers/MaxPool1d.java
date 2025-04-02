package gss.nnet.layers;

import gss.nnet.*;
import gss.arr.*;

public class MaxPool1d extends Module
{
	private int poolSize;

	public MaxPool1d(int poolSize)
	{
		this.poolSize = poolSize;
	}
	@Override
	public NDArray forward(NDArray input)
	{
		int mds=input.getLength() % poolSize;
		if (mds != 0)
			throw new RuntimeException("unable to make maxPool! make sure the pool size is divisble by the total length of the array.");
		int newLen=input.getLength() / poolSize;
		// devide the last dim of the input by the pool size and then keep other dims and se that shape to output.
		NDArray out=new NDArray(new int[]{newLen});
		float[]outArr=out.base.data.getData();
		float[] in=input.base.data.getData();
		int[] index=new int[outArr.length];
		for (int n=0;n < outArr.length;n++)
		{
			int np=n * poolSize;
			float pmx=-Float.MIN_VALUE;
			for (int p=np;p < np + poolSize;p++)
				if (in[p] >= pmx)
				{
					pmx = in[p];
					index[n] = p;
				}
			outArr[n] = pmx;
		}
		int[] nsh=input.getShape();
		nsh[nsh.length - 1] = nsh[nsh.length - 1] / poolSize;
		out.reshape(nsh);
		out.setGradientFunction(maxPool1dGradient, input).setGradientParams(index);
		return out;
	}
	public static GradFunc maxPool1dGradient=new GradFunc("maxPool1d"){

		@Override
		public NDArray backward(NDArray host, NDArray[] childs, Object[] params)
		{
			int[] index=(int[])params[0];
			NDArray ch=childs[0];
			float[] grd=host.base.data.getData();
			for (int i=0;i < grd.length;i++)
				ch.base.data.setGrad(index[i], grd[i]);
			return null;
		}
	};
}
