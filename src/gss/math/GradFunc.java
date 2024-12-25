package gss.math;

import java.util.*;

public abstract class GradFunc
{
	/*
	 // Bug 1.
	 when the graidient is set on the array, it doesn't append the value.
	 the values are just overwritten,

	 --- Fix
	 --- enabling append mode. onece every zero graidient...

	 // Bug 2.
	 all gradient calculators diesn't check if the childs support gradient or not.
	 this causes error when setGrad called with requireGradient = false arrays.

	 --- Fix
	 --- check every child uf they require gradient or not.
	 for(NDArray arr:childs)
	 {
	 .   if(!arr[0].requiresGradient())
	 .       continue;
	 .   // do gradient calculation.
	 }
	 */

	// name for debugging purpose.
	private String name;
	public GradFunc()
	{this.name = "unknown";}
	public GradFunc(String name)
	{
		this.name = name;
	}
	public abstract NDArray backward(NDArray host, NDArray...childs)
	@Override
	public String toString()
	{
		return name + "Gradient[" + hashCode() + "]";
	}
	public static GradFunc additionGradient = new GradFunc("addition")
	{
		@Override
		public NDArray backward(NDArray host, NDArray[] childs)
		{
//			for (NDArray arr:childs)
//			{
//				if (arr.getLength() != host.getLength())
//					throw new RuntimeException("");
			/*
			 addition gradient
			 a + b = c
			 c grad = 2
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
			// }
			return null;
		}
	};
	public static GradFunc subtractionGradient = new GradFunc("subtraction")
	{
		@Override
		public NDArray backward(NDArray host, NDArray[] childs)
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
		public NDArray backward(NDArray host, NDArray[] childs)
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
		public NDArray backward(NDArray host, NDArray[] childs)
		{
			NDArray a1=childs[0]; // == a  first operand.
			NDArray a2=childs[1]; // == b  second operand.
			// host  ==  c
			if (a1.getLength() != a2.getLength() && a2.getLength() != host.getLength())
				throw new RuntimeException("");
			/* for operand 1.
			 a / b = c
			 6 / 3 = 2;
			 gradient calculaion for division.
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
		public NDArray backward(NDArray host, NDArray[] childs)
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
					a1.setFlatGrad(i, host.getFlatGrad(i) * b * (float)Math.pow(a, b - 1));
				if (a2.requiresGradient())
					a2.setFlatGrad(i, host.getFlatGrad(i) * (float)Math.pow(a, b) * (float)Math.log(a));
			}
			return null;
		}
	};
}
