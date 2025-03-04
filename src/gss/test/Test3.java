package gss.test;

import gss.arr.*;
import gss.math.*;
import java.util.*;

import static gss.math.Util.*;

public class Test3
{
	public static void main(String[]args) throws Exception
	{

		new Test3().test();

	}
	void test() throws Exception
    {

		// Test2.main2(null);
		// test1();
		// test2();

		a();

	}
	void a()
	{

	}
	void test2()
	{
		System.out.println("Test 15. sum test.");
		NDArray ar=NDIO.arange(48).reshape(2, 4, 3, 2);
		// print(ar);
		ar = ar.sum();
		print(ar);
		float[] f=ar.base.toArray();
		float sum=0;
		for (float s:f)
			sum += s;
		Test1.test(Arrays.equals(new float[]{sum}, ar.base.toArray()), "sum along all axis.");
		NDArray arr = NDIO.arange(24).reshape(2, 3, 4);
		ar = arr.sum(0);
		print(ar);
		// original array ---v
//		float[][][] or={
//			{
//				{ 0.0f, 1.0f, 2.0f, 3.0f},
//				{ 4.0f, 5.0f, 6.0f, 7.0f},
//				{ 8.0f, 9.0f, 10.0f, 11.0f}
//			},
//			{
//				{ 12.0f, 13.0f, 14.0f, 15.0f},
//				{ 16.0f, 17.0f, 18.0f, 19.0f},
//				{ 20.0f, 21.0f, 22.0f, 23.0f}
//			}
//		};
		float[][][] sum0={
			{
				{12,14,16,18},
				{20,22,24,26},
				{28,30,32,34}
			}
		};
		Test1.test(Test1.equals(sum0, ar.base), "sum with axes 0");
		ar = arr.sum(1);
		print(ar);
		float[][][] sum1={
			{
				{12,15,18,21}
			},
			{
				{48,51,54,57}
			}
		};
		Test1.test(Test1.equals(sum1, ar.base), "sum with axes 1");
		ar = arr.sum(2);
		print(ar);
		float[][][] sum2={
			{
				{6},
				{22},
				{38}
			},
			{
				{54},
				{70},
				{86}
			}
		};
		Test1.test(Test1.equals(sum2, ar.base), "sum with axes 2");

	}
	void test1()
	{
		System.out.println("Test 14. convolution 1d and correlation 1d test.");
		// next test convolve gradient. not tested!.
		NDArray a1=NDIO.arange(5).setEnableGradient(true); // [0,1,2,3,4] -> [1,1,1,1,0],[0,1,1,1,1] conv,corre.. grad.
		NDArray a2=NDIO.arange(2).setEnableGradient(true); // [0,1] -> [0,1,,1,2,,2,3,,3,4] -> [6,10] grad.

		NDArray rs1=a1.convolve1d(a2); // [0,1,2,3] -> [1,1,1,1]
		NDArray rs2=a1.correlate1d(a2); // [1,2,3,4] -> [1,1,1,1]

		print(a1);
		print(a2);
		print(rs1);
		print(rs2);

		Test1.test(Arrays.equals(rs1.base.toArray(), new float[]{0,1,2,3}), "convolution result equals.");
		Test1.test(Arrays.equals(rs2.base.toArray(), new float[]{1,2,3,4}), "correlation result equals.");

		System.out.println("convolution gradient test");
		rs1.setGrad(new int[]{}, 1);
		rs1.backward();
		printGrad(a1);
		printGrad(a2);
		Test1.test(Arrays.equals(a1.base.toGradArray(), new float[]{1,1,1,1,0}), "convolution input gradient result equals.");
		Test1.test(Arrays.equals(a2.base.toGradArray(), new float[]{6,10}), "convolution kernel gradient result equals.");

		a1.zeroGrad();
		a2.zeroGrad();

		System.out.println("correlation gradient test");
		rs2.setGrad(new int[]{}, 1);
		rs2.backward();
		printGrad(a1);
		printGrad(a2);
		Test1.test(Arrays.equals(a1.base.toGradArray(), new float[]{0,1,1,1,1}), "correlation input gradient result equals.");
		Test1.test(Arrays.equals(a2.base.toGradArray(), new float[]{6,10}), "correlation kernel gradient result equals.");

	}
	void ndArrayApproximation2()
	{
		// gradient descent example.
		float lr=0.0001f;

		NDArray t=NDIO.fromArray(new int[]{2}, 12, 15);
		NDArray in=NDIO.fromArray(new int[]{3}, 2, 3, 4);

		NDArray w=NDIO.rand(new int[]{3, 2}).setEnableGradient(true);
		NDArray b=NDIO.value(new int[]{2}, 1).setEnableGradient(true);

		while (true)
		{
			NDArray o=in.dot(w).add(b);

			print(o);

			o = t.sub(o).pow(2);
			o.setGrad(1);
			o.backward();

			NDArray wgr=w.sub(w.getGradient().mul(lr));
			w.set(wgr);

			NDArray bgr=b.sub(b.getGradient().mul(lr));
			b.set(bgr);

			w.zeroGrad();
			b.zeroGrad();

		}
	}
	void gradientDetach()
	{

		NDArray ar=NDIO.rand(3, 2).setEnableGradient(true);
		ar.setGrad(5);

		print(ar);
		print("---------");
		printGrad(ar);
		print("------------------");

		NDArray gar=ar.getGradient();
		gar.setFloat(new int[]{1,1}, 30);

		print(gar);
		print("--------");
		printGrad(ar);

		print("---------");
		gar = ar.detachGradient();

		ar.setExactGrad(new int[]{0,1}, 50);

		print(gar);
		printGrad(ar);

	}
	void ndarrayApproximaion()
	{
		float lr=0.001f;

		NDArray t=NDIO.value(new int[]{1}, 12);
		NDArray in=NDIO.value(new int[]{1}, 2);

		NDArray w=NDIO.rand(new int[]{1}).setEnableGradient(true);
		NDArray b=NDIO.value(new int[]{1}, 1).setEnableGradient(true);

		while (true)
		{
			NDArray o=in.dot(w).add(b);

			print(o);

			o = t.sub(o).pow(2);
			o.setGrad(1);
			o.backward();
			// print(w.getExactGrad(0));

			float wv=w.getFloat(0) - w.getExactGrad(0) * lr;
			w.setFloat(new int[]{0}, wv);

			float bv=b.getFloat(0) - b.getExactGrad(0) * lr;
			b.setFloat(new int[]{0}, bv);


			w.zeroGrad();
			b.zeroGrad();
		}
	}
	void valueApproxmate()
	{
		float lr=0.0001f;

		Value t=new Value(12);
		Value in=new Value(2);

		Value w=new Value((float)Math.random());
		Value b=new Value(1);

		while (true)
		{
			// for (int i=0;i < 20;i++)
			{
				Value o=in.mul(w).add(b);

				print(t.val + " == " + o.val + " ~ " + Math.round(o.val));

				o = t.sub(o).pow(new Value(2));

				o.grad = 1;
				backward(o);

				w.val -= w.grad * lr;
				b.val -= b.grad * lr;

				w.grad = 0;
				b.grad = 0;
			}
			// print("-------------------------");
			// new Scanner(System.in).nextLine();
		}
	}
	void backward(Value v)
	{
		// System.out.println("== .." + host);
		v.backward();
		if (v.args != null)
			for (Value vv:v.args)
				backward(vv);
	}
	void rawApproximate()
    {
		float lr=0.001f;
		float t=10;
		float in=5;
		float w=0.5f;
		float wg=0;
		float b=1;
		float bg=0;

		while (true)
		{

			float r=w * in + b;
			print(r);
			float m=t - r;
			float s=(float)Math.pow(m, 2);
			// print(s);

			float g=2 * m;
			bg = g;
			wg = g * t;
			// print(wg + " , " + bg);
			w += wg * lr;
			b += bg * lr;
		}

	}
	void tree(NDArray v, String t)
    {
		System.out.println(t + v + " ::: " + v.gradientFunction);
		// printGrad(v);
		// print("-----------");
		if (v.childs != null)
			for (NDArray vv:v.childs)
				tree(vv, t + "    ");
	}
}
