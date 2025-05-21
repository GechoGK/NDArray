package gss.nnet;

import gss.arr.*;
import java.util.*;

public abstract class Module
{
	public abstract NDArray forward(NDArray input);
	public ArrayList<NDArray> params=new ArrayList<>();

	public ArrayList<NDArray> getParameters()
	{
		return params;
	}
	public NDArray newParam(NDArray arr)
	{
		if (!params.contains(arr))
			params.add(arr);
		if (!arr.requiresGradient())
			arr.setEnableGradient(true);
		return arr;
	}
}
