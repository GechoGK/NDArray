package gss.nnet.layers;

import gss.arr.*;
import gss.nnet.*;
import java.util.*;

public class Sequential extends Module
{
	private List<Module> modules=new ArrayList<>();
	private boolean paramsChached=false;

	public Sequential(Module...mds)
	{
		for (Module m:mds)
			if (!modules.contains(m))
				modules.add(m);
	}
	public Module add(Module m)
	{
		modules.add(m);
		paramsChached = false;
		return m;
	}
	@Override
	public NDArray forward(NDArray input)
	{
		if (modules.size() == 0)
			return null;
		NDArray out=input;
		for (Module m:modules)
			out = m.forward(out);
		return out;
	}
	@Override
	public ArrayList<NDArray> getParameters()
	{
		if (!paramsChached)
		{
			params.clear();
			for (Module m:modules)
				params.addAll(m.getParameters());
			paramsChached = true;
		}
		return params;
	}
}
