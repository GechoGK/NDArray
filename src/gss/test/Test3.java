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

		a();

	}
	void a()
	{

		print("Hello world!");
		

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
