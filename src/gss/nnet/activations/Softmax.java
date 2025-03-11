package gss.nnet.activations;

import java.util.*;
import gss.arr.*;

public class Softmax
{
	public static NDArray softmax(NDArray arr)
	{
		/*
		 how to calculate?
		 1. calculate the sum of the array.
		 2. calculate exponent value for evenry element.
		 3. devide the exponent value with the sum.
		 !!! then you value will be softmaxes.
		 what is the use of max value in this calculation.
		 it is used for numerical stability. because of the function Math.exp(x).
		 it explodes when the value is large.
		 so what we need is we need to clip the values that means the largest value will be 1.
		 x - max will be < 0. the result will be the same.
		 */
		float[][] arr2d=arr.base.to2DArray(null);
		float[][] arrOut=new float[arr2d.length][arr2d[0].length];
		for (int d=0;d < arr2d.length;d++)
		{
			float[] ar=arr2d[d];	
			// Find the maximum value in the input vector
			double max = ar[0];
			for (int i = 1; i < ar.length; i++)
			{
				if (ar[i] > max)
				{
					max = ar[i];
				}
			}

			// Calculate exponentials
			float sum=0;
			float[] exponentials = new float[ar.length];
			for (int i = 0; i < ar.length; i++)
			{
				float exps=(float)Math.exp(ar[i] - max);
				exponentials[i] = exps;
				sum += exps;
			}

			// Calculate softmax probabilities
			float[] probabilities = new float[ar.length];
			for (int i = 0; i < ar.length; i++)
			{
				probabilities[i] = exponentials[i] / sum;
			}
			arrOut[d] = probabilities;
		}
		NDArray out= new NDArray(arrOut).reshape(arr.getShape()).setEnableGradient(arr.requiresGradient());
		// gradient calculator in progress.
		return out;
	}
	public static void main2(String[] args)
	{
		float[] input = {1.0f, 2.0f, 3.0f, 4.0f, 5.0f};
		NDArray in=new NDArray(input);
		NDArray out = softmax(in);
		float[] output=out.base.toArray();
		System.out.println(Arrays.toString(output)); // Example output (values may vary slightly)

	}

}
