package gss.arr;

import gss.math.*;
import java.util.*;

import static gss.math.Util.*;

public abstract class GradFunc
{
	// name for debugging purpose.
	private String name;

	public GradFunc()
	{
		this.name = "unknown";
	}
	public GradFunc(String name)
	{
		this.name = name;
	}
	public abstract NDArray backward(NDArray host, NDArray...childs, Object...params)
	@Override
	public String toString()
	{
		return name + "Gradient[" + hashCode() + "]";
	}
	public static GradFunc additionGradient = new GradFunc("addition")
	{
		@Override
		public NDArray backward(NDArray host, NDArray[] childs, Object...params)
		{
			/*
			 addition gradient
			 a + b = c
			 c.grad = 2
			 a.grad = c.grad * 1
			 b.gead = c.grad * 1
			 */
			NDArray a1=childs[0]; // a
			NDArray a2=childs[1]; // b
			for (int i=0;i < host.getLength();i++)
			{
				if (a1.requiresGradient())
					a1.setFlatGrad(i, host.getFlatGrad(i));
				if (a2.requiresGradient())
					a2.setFlatGrad(i, host.getFlatGrad(i));
			}
			return null;
		}
	};
	public static GradFunc subtractionGradient = new GradFunc("subtraction")
	{
		@Override
		public NDArray backward(NDArray host, NDArray[] childs, Object...params)
		{
			/*
			 subtraction gradient
			 a - b = c
			 c grad = 2
			 a.grad = c.grad * 1
			 b.gead = -c.grad * 1
			 */
			NDArray a1=childs[0]; // a
			NDArray a2=childs[1]; // b
//			if (arr.getLength() != host.getLength())
//				throw new RuntimeException("");
			for (int i=0;i < host.getLength();i++)
			{
				if (a1.requiresGradient())
					a1.setFlatGrad(i, host.getFlatGrad(i));
				if (a2.requiresGradient())
					a2.setFlatGrad(i, -host.getFlatGrad(i));
			}
			return null;
		}
	};
	public static GradFunc multiplicationGradient = new GradFunc("multiplication")
	{
		@Override
		public NDArray backward(NDArray host, NDArray[] childs, Object...params)
		{
			NDArray a1=childs[0]; // first operand..
			NDArray a2=childs[1]; // second operand.
			if (a1.getLength() != a2.getLength() && a2.getLength() != host.getLength())
				throw new RuntimeException("");
			/* for operand 1.
			 a * b = c
			 2 * 3 = 6;
			 gradient calculaion for multiplication.
			 c.grad = 5;
			 a.grad = c.grad * b.data
			 a.grad = 5 * 3 = 15;
			 b.grad = c.grad * a.data;
			 b.grad = 5 * 2 = 10
			 */
			for (int i=0;i < host.getLength();i++)
			{
				float grad=host.getFlatGrad(i);
				if (a1.requiresGradient())
					a1.setFlatGrad(i, grad * a2.getFlat(i));
				if (a2.requiresGradient())
					a2.setFlatGrad(i, grad * a1.getFlat(i));
			}
			return null;
		}
	};
	public static GradFunc divisionGradient = new GradFunc("division")
	{
		@Override
		public NDArray backward(NDArray host, NDArray[] childs, Object...params)
		{
			NDArray a1=childs[0]; // == a  first operand.
			NDArray a2=childs[1]; // == b  second operand.
			// host  ==  c
			if (a1.getLength() != a2.getLength() && a2.getLength() != host.getLength())
				throw new RuntimeException("");
			/* for operand 1.
			 a / b = c
			 6 / 3 = 2;
			 gradient calculation for division.
			 c.grad = 5;
			 a.grad = c.grad * 1 / b.data
			 a.grad = 5 * 1 / 3 = 1.6666...
			 b.grad = -c.grad * a.value / (b.value^2);
			 b.grad = -5 * 6 / (3 * 3 ) = -3.3333...

			 */
			for (int i=0;i < host.getLength();i++)
			{
				float grad = host.getFlatGrad(i); // c
				float bVal = a2.getFlat(i);
				if (a1.requiresGradient())
					a1.setFlatGrad(i, grad * 1 / bVal);
				if (a2.requiresGradient())
					a2.setFlatGrad(i, -grad * a1.getFlat(i) / (bVal * bVal));
			}
			return null;
		}
	};
	public static GradFunc powGradient = new GradFunc("pow"){
		@Override
		public NDArray backward(NDArray host, NDArray[] childs, Object...params)
		{
			// this.childs[0].setGrad(this.grad * this.childs[1].data * Math.pow(this.childs[0].data, childs[1].data - 1));
			// this.childs[1].setGrad(this.grad * (Math.pow(this.childs[0].data, this.childs[1].data) * Math.log(childs[0].data)));
			/*
			 pow gradient
			 example.
			 c = a ** b  // a the power of b.
			 // grad ==
			 c.grad = 2;
			 a.grad = c.grad * b.data * ( a.data ** (b.data - 1))
			 b.grad = c.grad * ( a.data ** b.data ) * log(a.data)
			 */
			NDArray a1=childs[0];
			NDArray a2=childs[1];
			for (int i=0;i < host.getLength();i++)
			{
				float a=a1.getFlat(i); // a.data
				float b=a2.getFlat(i); // b.data
				if (a1.requiresGradient())
					a1.setFlatGrad(i, b * host.getFlatGrad(i) * (float)Math.pow(a, b - 1));
				if (a2.requiresGradient())
					a2.setFlatGrad(i, host.getFlatGrad(i) * (float)Math.pow(a, b) * (float)Math.log(a));
			}
			return null;
		}
	};
	public static GradFunc vStackGradient =new GradFunc("vstack"){
		@Override
		public NDArray backward(NDArray host, NDArray[] childs, Object...params)
		{
			// System.out.println("VStack---host length =" + host.getLength() + ", childs length =" + childs[0].getLength());
			NDArray a=childs[0];
			if (a.getLength() != host.getLength())
				throw new RuntimeException("two array length doesn't match.");
			for (int i=0;i < host.getLength();i++)
				a.setFlatGrad(i, host.getFlatGrad(i));
			return null;
		}
	};
	public static GradFunc hStackGradient =new GradFunc("hstack"){
		@Override
		public NDArray backward(NDArray host, NDArray[] childs, Object...params)
		{
			// System.out.println("HStack---host length =" + host.getLength() + ", childs length =" + childs[0].getLength());
			NDArray a=childs[0];
			if (a.getLength() != host.getLength())
				throw new RuntimeException("two array length doesn't match.");
			for (int i=0;i < host.getLength();i++)
				a.setFlatGrad(i, host.getFlatGrad(i));
			return null;
		}
	};
	public static GradFunc dotGradient=new GradFunc("dot"){
		@Override
		public NDArray backward(NDArray host, NDArray[] childs, Object...params)
		{
			// System.out.println("performing dot product backpropagation");
			// System.out.println("host = " + host.getDim() + ": " + Arrays.toString(host.getShape()));
			// System.out.println("x = " + childs[0].getDim() + ": " + Arrays.toString(childs[0].getShape()));
			// System.out.println("y = " + childs[1].getDim() + ": " + Arrays.toString(childs[1].getShape()));

			NDArray x=childs[0];
			NDArray y=childs[1];
//			print("----------");
//			print(x);
//			print("----------");
//			print(y);
//			print("------");
			int xr=x.getShape()[0];
			int xc=x.getShape()[1];
			int yr=y.getShape()[0];
			int yc=y.getShape()[1];
			int count=yr / xc;
			NDArray g=host.reshape(-1, count * getAtR(host.getShape(), 0));
			// print(g);
			// print("count =" + count);
			// print("(" + xr + ", " + xc + " :: " + yr + ", " + yc + ")");
			for (int rx=0;rx < xr;rx++)
				for (int cnt=0;cnt < count;cnt++)			
					for (int cy=0;cy < yc;cy++)				
						for (int cx=0;cx < xc;cx++)
						{
							int p=cnt * xc + cx;
							int pg=yc * cnt + cy;
							// gv = g[rx][p]
							// xgrad[rx][cx] =  y[p][cy] * grad
							// ygrad[p][cy]  =  x[rx][cx] * grad
							float grd=g.getExactGrad(rx, pg); // ensure g is 2d array.
							if (x.requiresGradient())
							{
								float yv = y.getFloat(p, cy);
								x.setExactGrad(new int[]{rx,cx}, yv * grd);
							}
							if (y.requiresGradient())
							{
								float xv = x.getFloat(rx, cx);
								y.setExactGrad(new int[]{p,cy}, xv * grd);
							}
							// print(grd + " = " + xv + " : " + yv);
						}
			return null;
		}
	};
	public static GradFunc stepGradient = new GradFunc("step"){
		@Override
		public NDArray backward(NDArray host, NDArray[] childs, Object...params)
		{
			if (host.base.data != childs[0].base.data)
				throw new RuntimeException("unknown step gradient.");
			return null;
		}
	};
	public static GradFunc convolve1dGradient = new GradFunc("conv1d"){
		@Override
		public NDArray backward(NDArray host, NDArray[] childs, Object...params)
		{
			// print("convolution 1d backward");
			NDArray a1=childs[0]; // input.
			NDArray a2=childs[1]; // kernel.
			float[][]hostGrad=host.base.to2DGradArray();
			float[][] hout=host.base.to2DArray(null);
			float[][] inp=a1.base.to2DArray(null);
			float[] kern=a2.base.toArray();
			// gradients;
			float[][] inpGrad=null;
			boolean inpgrad=a1.requiresGradient();
			boolean kerngrad=a2.requiresGradient();
			if (inpgrad)
				inpGrad = a1.base.to2DGradArray(); // new float[inp.length][inp[0].length]; // don't create array!, grab from input(a1).
			float[] kgrad=null;
			if (kerngrad)
				kgrad = a2.base.toGradArray(); // new float[kern.length]; // don't create array, grad from kernel(a2).
			if (inpgrad || kerngrad)
				for (int or=0;or < hout.length;or++) // output row.
				{	
					for (int oc=0;oc < hout[or].length;oc++) // output column.
					{
						float fout=hostGrad[or][oc];
						// reverse kernel.
						int kpos=kern.length - 1;
						for (int kc=0;kc < kern.length;kc++)
						{
							if (inpgrad)
								inpGrad[or][oc + kc] += fout * kern[kpos];
							if (kerngrad)
								kgrad[kc] += fout * inp[or][oc + kc];
							kpos--;
						}
					}
				}
			if (inpgrad)
				a1.base.setGrad(inpGrad);
			if (kerngrad)
				a2.base.setGrad(kgrad);
			return null;
		}
	};
	public static GradFunc correlate1dGradient = new GradFunc("correlate1d"){
		@Override
		public NDArray backward(NDArray host, NDArray[] childs, Object...params)
		{
			// print("correlation 1d backward");
			NDArray a1=childs[0]; // input.
			NDArray a2=childs[1]; // kernel.
			float[][]hostGrad=host.base.to2DGradArray();
			float[][] hout=host.base.to2DArray(null);
			float[][] inp=a1.base.to2DArray(null);
			float[] kern=a2.base.toArray();
			// gradients;
			float[][] inpGrad=null;
			boolean inpgrad=a1.requiresGradient();
			boolean kerngrad=a2.requiresGradient();
			if (inpgrad)
				inpGrad = a1.base.to2DGradArray(); // new float[inp.length][inp[0].length]; // don't create!, grab from input(a1).
			float[] kgrad=null;
			if (kerngrad)
				kgrad = a2.base.toGradArray(); // new float[kern.length]; // don't create, grad from kernel(a2).
			if (inpgrad || kerngrad)
				for (int or=0;or < hout.length;or++) // output row.
				{	
					for (int oc=0;oc < hout[or].length;oc++) // output column.
					{
						float fout=hostGrad[or][oc];
						// kernel not reversed.
						for (int kc=0;kc < kern.length;kc++)
						{
							if (inpgrad)
								inpGrad[or][oc + kc] += fout * kern[kc];
							if (kerngrad)
								kgrad[kc] += fout * inp[or][oc + kc];
						}
					}
				}
			if (inpgrad)
				a1.base.setGrad(inpGrad);
			if (kerngrad)
				a2.base.setGrad(kgrad);
			return null;
		}
	};
	public static GradFunc itemGradient = new GradFunc("item"){
		@Override
		public NDArray backward(NDArray host, NDArray[] childs, Object...params)
		{
			// iterate over each stores Value classes and then call backward on them.
			Value[] vls=host.base.data.getValues();
			if (vls == null || vls.length == 0)
			 	return null;
			// System.out.println("== .." + host);
			HashSet<Value> tmpLst=new HashSet<>();
			HashSet<Value> lst=new HashSet<>();
			lst.addAll(Arrays.asList(vls));
			while (lst.size() != 0)
			{
				for (Value v:lst)
				{
					v.backward();
					tmpLst.addAll(v.args);
				}
				lst.clear();
				lst.addAll(tmpLst);
				tmpLst.clear();
			}
			return null;
		}
	};
	public static GradFunc sumGradient=new GradFunc("sum"){
		@Override
		public NDArray backward(NDArray host, NDArray[] childs, Object...params)
		{
			// print("sum backward pass.");
			float g=host.getExactGrad(0);
			NDArray ar=childs[0];
			ar.setGrad(g);
			return null;
		}
	};
	public static GradFunc sumAxesGradient=new GradFunc("sumAxes"){
		@Override
		public NDArray backward(NDArray host, NDArray[] childs, Object...params)
		{
			// print("sum axes backward pass.");
			NDArray org=childs[0];
			org.fillGrad(host.getGradient());
			return null;
		}
	};
	public static GradFunc positionGradient=new GradFunc("min/max"){
		@Override
		public NDArray backward(NDArray host, NDArray[] childs, Object[] params)
		{
			if (params == null || params.length == 0)
				throw new RuntimeException("unable to comput positionGradient b/c params not found.");
			NDArray arr=childs[0];
			arr.base.data.setGrad((int)params[0], host.getFlatGrad(0));
			return null;
		}
	};
	public static GradFunc logEGradient=new GradFunc("logE/ln"){
		@Override
		public NDArray backward(NDArray host, NDArray[] childs, Object[] params)
		{
			NDArray arr=childs[0];
			float[] gd=host.base.data.getGrads(); // gradient comes from chain rule.
			float[] dt=arr.base.data.getData(); // use as x.
			// float[] tr=arr.base.data.getGrads(); // trget to store gradient.
			for (int i=0;i < dt.length;i++)
			{
				arr.base.data.setGrad(i, gd[i] * (1 / dt[i]));
			}
			// arr.base.setGrad(tr);
			return null;
		}
	};
	public static GradFunc log10Gradient=new GradFunc("log10"){
		@Override
		public NDArray backward(NDArray host, NDArray[] childs, Object[] params)
		{
			NDArray arr=childs[0];
			float[] gd=host.base.data.getGrads(); // gradient comes from chain rule.
			float[] dt=arr.base.data.getData(); // use as x.
			// float[] tr=arr.base.data.getGrads(); // trget to store gradient.
			for (int i=0;i < dt.length;i++)
			{
				arr.base.data.setGrad(i, gd[i] * (1 / (dt[i] * (float)Math.log(10))));
			}
			return null;
		}
	};
	public static GradFunc expGradient=new GradFunc("exp"){
		@Override
		public NDArray backward(NDArray host, NDArray[] childs, Object[] params)
		{
			float[] grd=host.base.data.getGrads();
			NDArray ar=childs[0];
			float[] dt=ar.base.data.getData();
			for (int i=0;i < grd.length;i++)
			{
				ar.base.data.setGrad(i, grd[i] * (float)Math.exp(dt[i]));
			}
			return null;
		}
	};
	public static GradFunc mergeGradient=new GradFunc("merge"){
		@Override
		public NDArray backward(NDArray host, NDArray[] childs, Object[] params)
		{
			NDArray grd=host.getGradient();
			for (int i=0;i < childs.length;i++)
			{
				childs[i].fillGrad(grd.get(i));
			}
			return null;
		}
	};
	public static GradFunc absGradient=new GradFunc("ab"){
		@Override
		public NDArray backward(NDArray host, NDArray[] childs, Object[] params)
		{
			// do nothing.
			throw new RuntimeException("gradient not implemented for abs function.");
		}
	};
}
