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

	public abstract NDArray backward(NDArray host, NDArray...childs)
	@Override
	public String toString()
	{
		return this.getClass().getSimpleName() + "[" + hashCode() + "]";
	}
	public static GradFunc additionGradient = new GradFunc()
	{
		@Override
		public NDArray backward(NDArray host, NDArray[] childs)
		{
			for (NDArray arr:childs)
			{
				if (arr.getLength() != host.getLength())
					throw new RuntimeException("");
				for (int i=0;i < arr.getLength();i++)
				{
					arr.setFlatGrad(i, host.getFlatGrad(i));
				}
			}
			return null;
		}
	};
	public static GradFunc subtractionGradient = new GradFunc()
	{
		@Override
		public NDArray backward(NDArray host, NDArray[] childs)
		{
			// for child 1.
			NDArray arr=childs[0]; // child 0 pass value.
			if (arr.getLength() != host.getLength())
				throw new RuntimeException("");
			for (int i=0;i < arr.getLength();i++)
				arr.setFlatGrad(i, host.getFlatGrad(i));
			// for child 2.
			arr = childs[1]; // child 1 pass value bu mulyiplying with -1.
			if (arr.getLength() != host.getLength())
				throw new RuntimeException("");
			for (int i=0;i < arr.getLength();i++)
				arr.setFlatGrad(i, -host.getFlatGrad(i));

			return null;
		}
	};
	public static GradFunc multiplicationGradient = new GradFunc()
	{
		@Override
		public NDArray backward(NDArray host, NDArray[] childs)
		{
			NDArray arr1=childs[0]; // first operand..
			NDArray arr2=childs[1]; // second operand.
			if (arr1.getLength() != arr2.getLength() && arr2.getLength() != host.getLength())
				throw new RuntimeException("");
			/* for operand 1.
			 a * b = c
			 2 * 3 = 6;
			 gradient calculaion for multiplication.
			 c.grad = 5;
			 a.grad = c.grad * b.value
			 a.grad = 5 * 3 = 15;
			 b.grad = c.grad * a.value;
			 b.grad = 5 * 2 = 10
			 */
			for (int i=0;i < host.getLength();i++)
			{
				float grad=host.getFlatGrad(i);
				arr1.setFlatGrad(i, grad * arr2.getFlat(i));
				arr2.setFlatGrad(i, grad * arr1.getFlat(i));
			}
			return null;
		}
	};
	public static GradFunc divisionGradient = new GradFunc()
	{
		@Override
		public NDArray backward(NDArray host, NDArray[] childs)
		{
			NDArray arr1=childs[0]; // == a  first operand.
			NDArray arr2=childs[1]; // == b  second operand.
			// host  ==  c
			if (arr1.getLength() != arr2.getLength() && arr2.getLength() != host.getLength())
				throw new RuntimeException("");
			/* for operand 1.
			 a / b = c
			 6 / 3 = 2;
			 gradient calculaion for division.
			 c.grad = 5;
			 a.grad = c.grad * 1 / b.value
			 a.grad = 5 * 1 / 3 = 1.6666...
			 b.grad = -c.grad * a.value / (b.value^2);
			 b.grad = -5 * 6 / (3 * 3 ) = -3.3333...

			 */
			for (int i=0;i < host.getLength();i++)
			{
				float grad = host.getFlatGrad(i); // c
				float bVal = arr2.getFlat(i);
				arr1.setFlatGrad(i, grad * 1 / bVal);
				arr2.setFlatGrad(i, -grad * arr1.getFlat(i) / (bVal * bVal));
			}
			return null;
		}
	};
}
