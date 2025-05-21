package gss.nnet.layers;

import gss.nnet.*;
import gss.arr.*;
import java.util.*;

import static gss.math.Util.*;

public class Conv1d extends Module
{
	private int n_channels;
	private int n_kernels;
	private int kernel_size;
	private int output_size=0;
	private int input_size=0;

	private NDArray kernels,biase;

	public Conv1d(int input_size, int n_channels, int n_kernels, int kernel_size)
	{
		this.input_size = input_size;
		this.n_channels = n_channels;
		this.n_kernels = n_kernels;
		this.kernel_size = kernel_size;
		init();
	}
	private void init()
	{
		output_size = input_size - kernel_size + 1;
		kernels = newParam(NDIO.rand(n_kernels, n_channels, kernel_size));
		biase = newParam(NDIO.ones(n_kernels, output_size));
	}
	@Override
	public NDArray forward(NDArray input)
	{
		if (getAtR(input.getShape(), 0) != input_size)
			throw new IllegalArgumentException("input data size must be equal.");
		if (getAtR(input.getShape(), 1) != n_channels)
			throw new IllegalArgumentException("input data feature size must be equal.");
		NDArray[]outs=new NDArray[n_kernels];
		for (int i=0;i < n_kernels;i++)
		{
			NDArray kd=null;
			for (int c=0;c < n_channels;c++)
			{
				NDArray k=kernels.get(i, c);
				NDArray crs=input.convolve1d(k);
				kd = kd == null ?crs: kd.add(crs);
			}
			outs[i] = kd;
		}
		NDArray out=NDArray.merge(outs);
		// System.out.println(Arrays.toString(out.getShape()) + ", " + Arrays.toString(biase.getShape()));
		return out.add(biase);
	}
}
