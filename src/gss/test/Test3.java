package gss.test;

import gss.arr.*;
import gss.math.*;
import gss.nnet.activations.*;
import gss.nnet.lossfunctions.*;
import gss.nnet.optimizers.*;
import java.util.*;

import static gss.math.Util.*;
import gss.nnet.layers.*;
import gss.nnet.*;
import gss.*;

public class Test3
{
	public static void main(String[]args) throws Exception
	{

		new Test3().test();

	}
	void test() throws Exception
    {

//		Test2.main2(null);
//		test1();
//		test2();
//		test3();
//		test4();  // uses storage access.
//		test5();
//		test6();
//		test7();  // uses storage access.
//		test8();
//		test9();
//		test10();
//		test11();
//		test12();

		a();

	}
	void a()
	{
		/*
		 test the following packages.
		 -- Linear            ✓
		 -- MaxPool1d         ✓
		 -- Sequential        ✓
		 -- Module parameters ✓
		 -- Dropout           ✓
		 */
	}
	void dropoutTest()
	{
		print("Dropout test");
		NDArray in=NDIO.rand(2, 5).setEnableGradient(true);
		Dropout d=new Dropout(0.35f);
		NDArray out=d.forward(in);
		print(in);
		print(out);
		print(Util.genString("+", 40));
		out.setGrad(5);
		out.backward();
		printGrad(out);
		print(Util.genString("+", 40));
		printGrad(in);

	}
	void maxPool1dTest()
	{
		print("MaxPool1d Test.");
		NDArray arr=new NDArray(new float[]{1,2,1,3,3,5,5,2,4,4,2,5,4,7,9}).setEnableGradient(true);
		NDArray tar=new NDArray(new float[]{0,1,0,0,0,1,1,0,0,0,0,1,0,0,1}).mul(5);
		NDArray arr2=new MaxPool1d(3).forward(arr);

		print(arr);
		arr2.setGrad(5);
		printGrad(arr2);
		arr2.backward();
		printGrad(arr);

		Test1.test(Util.equals(arr.getGradient(), tar), "max pool forward and backward pass equals.");
	}
	void XORTest()
	{
		NDArray x=new NDArray(new float[][]{{0,1},{0,0},{1,0},{1,1}});
		NDArray y=new NDArray(new float[]{1,0,1,0});

		Linear l1=new Linear(2, 3);
		Linear l2=new Linear(3, 1);

		Activation a2=new Sigmoid();

		LossFunc lossFunc=new BCE();

		Optimizer optim=new Adam(l1.getParameters(), l2.getParameters());
		// sometimes when we use Adam optimizer it stuck to local minima, or unable to fit the dataset. so keep try again.
		optim = new GradientDescent(l1.getParameters(), l2.getParameters());

		NDArray output=null;

		Controll c=new Controll("stop", "print", "debug");
		c.set("debug");
		c.start();

		int ps=0;
		float lsv=1000;
		while (lsv >= 0.02f && !c.get("stop"))
		{
			NDArray X = l1.forward(x);
			X = a2.forward(X);
			X = l2.forward(X);
			X = a2.forward(X);
			output = X;

			NDArray loss=lossFunc.forward(X, y);
			lsv = loss.base.data.getData(0);

			if (c.get("debug"))
				System.out.println(ps + " :: " + lsv);

			loss.setGrad(1);
			loss.backward();

			optim.update();
			optim.zeroGrad();

			if (c.get("print"))
			{
				print(output);
				c.toggle("print");
				c.set("debug", false);
			}
			ps++;
		}
		print(output);
	}
	void controllTest()
	{
		Controll c=new Controll("start", "stop", "print", "debug");
		c.start();
		boolean[] strF=c.get("start", "stop", "print", "debug"); 
		print(Arrays.toString(strF));

		while (true)
		{
			boolean[] str=c.get("start", "stop", "print", "debug");
			if (!Arrays.equals(strF, str))
			{
				print(Arrays.toString(str));
				strF = str;
			}
		}
	}
	void approximationWithLossFunctions()
	{
		print("approximation test with different loss functions.");

		int input=2;
		int output=3;
		NDArray w1=NDIO.rand(input, 5).setEnableGradient(true);
		NDArray w2=NDIO.rand(5, output).setEnableGradient(true);
		NDArray b1=NDIO.ones(5).setEnableGradient(true);
		NDArray b2=NDIO.ones(output).setEnableGradient(true);

		NDArray in=NDIO.rand(2, input);
		NDArray tr=new NDArray(new float[][]{{1,0,1},{0,1,0}});

		// trainMSE(w1, w2, b1, b2, in, tr); // ≈ 19755, 24330, 10205, 15488, 7940, 6515, 6515, 6817 millis
		// trainMAE(w1, w2, b1, b2, in, tr); // ≈ 80709, 33369, 27102, 19464, 15508, 22978  millis
		trainBCE(w1, w2, b1, b2, in, tr); // ≈ 79235, 74982, 32427, 15759, 16387, 13459, 16758 millis
		// trainMCCE(w1, w2, b1, b2, in, tr); // slow and inaccurate // ≈ 79272, 20064, 22044, 30453, 7360, 7421, 5817, 7141, 4452, 5733   millis

		System.out.println("completed!");

	}
	void trainMSE(NDArray w1, NDArray w2, NDArray b1, NDArray b2, NDArray in, NDArray tr)
	{
		NDArray output=null;

		GradientDescent gd=new GradientDescent(w1, w2, b1, b2);

		float loss=Float.MAX_VALUE;
		long time=System.currentTimeMillis();
		while (loss >= 0.001f)
		{
			NDArray out = in.dot(w1).add(b1);
			// out = new Sigmoid().forward(out);
			out = out.dot(w2).add(b2);
			out = new Sigmoid().forward(out);

			output = out;
			out = new MSE().forward(out, tr);

			// int counter=counter();
			// if (counter % 50 == 0)
			//  	System.out.println(counter + ". loss : " + Arrays.toString(out.base.data.data) + " >> " + Arrays.toString(output.base.data.data));

			loss = out.base.data.data[0];

			out.setGrad(1);
			out.backward();

			gd.update();
			gd.zeroGrad();
		}
		time = System.currentTimeMillis() - time; // ≈ 80768 millis.
		print("total time taken : " + time + " millis");
		print("final output");
		print(output);
	}
	private static int cnt=0;
	static int counter()
	{
		return cnt++;
	}
	void trainMAE(NDArray w1, NDArray w2, NDArray b1, NDArray b2, NDArray in, NDArray tr)
	{
		NDArray output=null;

		GradientDescent gd=new GradientDescent(w1, w2, b1, b2);

		float loss=Float.MAX_VALUE;
		long time=System.currentTimeMillis();
		while (loss >= 0.001f)
		{
			NDArray out = in.dot(w1).add(b1);
			// out = new Sigmoid().forward(out);
			out = out.dot(w2).add(b2);
			out = new Sigmoid().forward(out);

			output = out;
			out = new MAE().forward(out, tr);

			// int counter=counter();
			// if (counter % 50 == 0)
			//  	System.out.println(counter + ". loss : " + Arrays.toString(out.base.data.data) + " >> " + Arrays.toString(output.base.data.data));


			loss = out.base.data.data[0];

			out.setGrad(1);
			out.backward();

			gd.update();
			gd.zeroGrad();
		}
		time = System.currentTimeMillis() - time; // ≈ ... millis.
		print("total time taken : " + time + " millis");
		print("final output");
		print(output);
	}
	void trainBCE(NDArray w1, NDArray w2, NDArray b1, NDArray b2, NDArray in, NDArray tr)
	{

		NDArray output=null;

		GradientDescent gd=new GradientDescent(w1, w2, b1, b2);

		float loss=Float.MAX_VALUE;
		long time=System.currentTimeMillis();
		while (loss >= 0.001f)
		{
			NDArray out = in.dot(w1).add(b1);
			// out = new Sigmoid().forward(out);
			out = out.dot(w2).add(b2);
			out = new Sigmoid().forward(out);

			output = out;
			out = new BCE().forward(out, tr);

			int counter=counter();
			if (counter % 100 == 0)
			// System.out.println(counter + ". loss : " + Arrays.toString(out.base.data.data) + " >> " + Arrays.toString(output.base.data.data));
				print(output);

			loss = out.base.data.data[0];

			out.setGrad(1);
			out.backward();

			gd.update();
			gd.zeroGrad();
		}
		time = System.currentTimeMillis() - time; // ≈ 80768 millis.
		print("total time taken : " + time + " millis");
		print("final output");
		print(output);
	}
	void trainMCCE(NDArray w1, NDArray w2, NDArray b1, NDArray b2, NDArray in, NDArray tr)
	{
		NDArray output=null;

		GradientDescent gd=new GradientDescent(w1, w2, b1, b2);

		float loss=Float.MAX_VALUE;
		long time=System.currentTimeMillis();
		while (loss >= 0.001f)
		{
			NDArray out = in.dot(w1).add(b1);
			// out = new Sigmoid().forward(out);
			out = out.dot(w2).add(b2);
			// out = new Sigmoid().forward(out);

			output = out;
			out = new MCCE().forward(out, tr);

			// int counter=counter();
			// if (counter % 50 == 0)
			//  	System.out.println(counter + ". loss : " + Arrays.toString(out.base.data.data) + " >> " + Arrays.toString(output.base.data.data));


			loss = out.base.data.data[0];

			out.setGrad(1);
			out.backward();

			gd.update();
			gd.zeroGrad();
		}
		time = System.currentTimeMillis() - time; // ≈ 80768 millis.
		print("total time taken : " + time + " millis");
		print("final output");
		print(output);
	}
	void test12()
	{
		System.out.println("Test 12. relu, sigmoid, and tanh test with backpropagation");

		NDArray arr1=NDIO.arange(-9, 11).reshape(-1, 5).setEnableGradient(true);
		NDArray arr2=NDIO.arange(-9, 11).reshape(-1, 5).setEnableGradient(true);
		NDArray lg1=new Relu().forward(arr1);
		NDArray lg2=TestND.relu(arr2);
		// print(lg1);
		Test1.test(Util.equals(lg1, lg2), "relu data equals");
		lg1.setGrad(2);
		lg2.setGrad(2);
		lg1.backward();
		lg2.backward();
		// printGrad(arr1);
		Test1.test(Util.equals(arr1, arr2, true), "relu data and gradient equals");
		print("-----------");

		arr1 = NDIO.arange(-9, 11).reshape(-1, 5).setEnableGradient(true);
		arr2 = NDIO.arange(-9, 11).reshape(-1, 5).setEnableGradient(true);
		lg1 = new Sigmoid().forward(arr1);
		lg2 = TestND.sigmoid(arr2);
		// print(lg1);
		// print(lg2);
		Test1.test(Util.equals(lg1, lg2), "sigmoid data equals");
		lg1.setGrad(5);
		lg2.setGrad(5);
		lg1.backward();
		lg2.backward();
		// printGrad(arr1);
		// printGrad(arr2);
		Test1.test(Util.equals(arr1, arr2, true, true), "sigmoid data and gradient equals");
		print("-----------");

		arr1 = NDIO.arange(-9, 11).reshape(-1, 5).setEnableGradient(true);
		arr2 = NDIO.arange(-9, 11).reshape(-1, 5).setEnableGradient(true);
		lg1 = new Tanh().forward(arr1);
		lg2 = TestND.tanh(arr2);
		// print(lg1);
		// print(lg2);
		Test1.test(Util.equals(lg1, lg2), "tanh data equals");
		lg1.setGrad(2);
		lg2.setGrad(2);
		lg1.backward();
		lg2.backward();
		// printGrad(arr1);
		// printGrad(arr2);
		// print("------");
		// print(arr1.getGradient().sub(arr2.getGradient()));
		Test1.test(Util.equals(arr1, arr2, true), "tanh data and gradient equals");
		print("-----------");

	}
	void test11()
	{
		System.out.println("Test 11. log/ log10/ exp gradients test.");
		NDArray arr1=NDIO.arange(1, 11).reshape(2, 5).setEnableGradient(true);
		NDArray arr2=NDIO.arange(1, 11).reshape(2, 5).setEnableGradient(true);
		NDArray lg1=arr1.log();
		NDArray lg2=TestND.log(arr2);
		// print(lg1);
		Test1.test(Util.equals(lg1, lg2), "log data equals");
		lg1.setGrad(2);
		lg2.setGrad(2);
		lg1.backward();
		lg2.backward();
		// printGrad(arr1);
		Test1.test(Util.equals(arr1, arr2, true), "log data and gradient equals");
		// print("log10 tests done");

		arr1 = NDIO.arange(1, 11).reshape(2, 5).setEnableGradient(true);
		arr2 = NDIO.arange(1, 11).reshape(2, 5).setEnableGradient(true);
		lg1 = arr1.log10();
		lg2 = TestND.log10(arr2);
		// print(lg1);
		Test1.test(Util.equals(lg1, lg2), "log10 data equals");
		lg1.setGrad(2);
		lg2.setGrad(2);
		lg1.backward();
		lg2.backward();
		// printGrad(arr1);
		Test1.test(Util.equals(arr1, arr2, true), "log10 data and gradient equals");
		// print("log10 tests done ");

		arr1 = NDIO.arange(1, 11).reshape(2, 5).setEnableGradient(true);
		arr2 = NDIO.arange(1, 11).reshape(2, 5).setEnableGradient(true);
		lg1 = arr1.exp();
		lg2 = TestND.exp(arr2);
		// print(lg1);
		Test1.test(Util.equals(lg1, lg2), "exp data equals");
		lg1.setGrad(2);
		lg2.setGrad(2);
		lg1.backward();
		lg2.backward();
		// printGrad(arr1);
		// printGrad(arr2);
		Test1.test(Util.equals(arr1, arr2, true), "exp data and gradient equals");
		// print("exp tests done");
	}
	void test10()
	{
		System.out.println("Test 10. log gradient using Value class.");

		Value v=new Value(10); 
		Value v2=v.log();
		System.out.println(v.getData() + " =log= " + v2.getData() + " == " + v2);
		v2.setGrad(2);
		v2.backward();
		System.out.println(v2.getGrad() + " => " + v.getGrad());
	}
	void test9()
	{
		System.out.println("Test 9. min/max and log test");

		NDArray arr=NDIO.rand(10);
		print(arr);
		print("max ", arr.max());
		print("max index " + arr.argMax());

		print("min ", arr.min());
		print("min index " + arr.argMin());

		print(arr.log10());
		print(arr.log());
		print(arr.ln());

		print(arr.exp());

	}
	void test8()
	{
		System.out.println("Test 8. Converge test");
		NDArray tr=NDIO.value(new int[]{5}, 3);
		NDArray w1=NDIO.rand(5).setEnableGradient(true);
		NDArray b=NDIO.ones(5).setEnableGradient(true);
		MSE mse=new MSE();
		GradientDescent gd=new GradientDescent(w1, b);

		NDArray in=NDIO.fromArray(new int[]{5}, new float[]{1,2,3,4,5});

		System.out.print("training for 1000 iterations");
		int count=0;
		while (count < 1000)
		{
			NDArray rs=w1.dot(in).add(b);
			NDArray loss=mse.forward(rs, tr);

			loss.setGrad(1);
			loss.backward();

			// print(loss);
			gd.update();

			gd.zeroGrad();

			count++;
			if (count % 300 == 0)
				System.out.print(".");
		}
		print("\ndone training");
		NDArray result=w1.dot(in).add(b);

		NDArray loss=mse.forward(result, tr);

		print("final result with ---v   loss " + loss.getFloat(0));
		print(result);
		print("target ---v");
		print(tr);
		print(genString("=", 45));
		print("weights");
		print(w1);
		print(genString("-", 20));
		print(b);
		print(genString("=", 10) + " loss = " + loss.getFloat(0));
		Test1.test(loss.getFloat(0) < 0.001f, "loss less than 0.001");

	}
	void test7()
	{
		test4();

		System.out.println("Test 7. load ndarray from file.");

		NDArray orig=NDIO.arange(24).reshape(2, 3, 4).setEnableGradient(false);
		try
		{
			NDArray arJson1=NDIO.loadJSON("/sdcard/test/arrayJson1.json");	
			Test1.test(Util.equals(orig, arJson1), "load json array 1 equals");
			NDArray arJson2=NDIO.load("/sdcard/test/arrayJson2.json", NDIO.FileType.JSON);
			Test1.test(Util.equals(orig, arJson2), "load json array 2 equals");

			NDArray arText1=NDIO.load("/sdcard/test/arrayText.txt");
			Test1.test(Util.equals(orig, arText1), "load text array 1 equals");
			NDArray arText2=NDIO.load("/sdcard/test/arrayText.txt", NDIO.FileType.TEXT);
			Test1.test(Util.equals(orig, arText2), "load text array 2 equals");

			NDArray arBin1=NDIO.load("/sdcard/test/arrayBin.ndbin");
			Test1.test(Util.equals(orig, arBin1), "load binary array 1 equals");
			NDArray arBin2=NDIO.load("/sdcard/test/arrayBin.ndbin", NDIO.FileType.BINARY);
			Test1.test(Util.equals(orig, arBin2), "load binary array 2 equals");

		}
		catch (Exception e)
		{
			print("error " + e);
			e.printStackTrace();
		}

	}
	void test6()
	{
		System.out.println("Test 6. sum with axis backward pass test.");
		NDArray arr = NDIO.arange(24).reshape(2, 3, 4).setEnableGradient(true);
		NDArray ar = arr.sum(1);
//		print(arr);
//		print("---");
//		printGrad(arr);
//		print("----------");
//		print(ar);
//		printGrad(ar);
		//		print("----------");
		ar.setGrad(1);
		ar.backward();
//		print(ar);
//		printGrad(ar);
//		print("----------");
//		print(arr);
//		print("---");
		//		printGrad(arr);

		float[][] finalGd=
		{
			{ 1.0f, 1.0f, 1.0f, 1.0f},
			{ 1.0f, 1.0f, 1.0f, 1.0f}
		};
		Test1.test(Test1.equals(finalGd, ar.getGradient().base), "gradient after sum equals");
		float[][][] beforeSumGd=
		{
			{
				{ 1.0f, 1.0f, 1.0f, 1.0f},
				{ 1.0f, 1.0f, 1.0f, 1.0f},
				{ 1.0f, 1.0f, 1.0f, 1.0f}
			},
			{
				{ 1.0f, 1.0f, 1.0f, 1.0f},
				{ 1.0f, 1.0f, 1.0f, 1.0f},
				{ 1.0f, 1.0f, 1.0f, 1.0f}
			}
		};

		Test1.test(Test1.equals(beforeSumGd, arr.getGradient().base), "gradient before sum equals");
	}
	void test5()
	{
		System.out.println("Test 5. fill grad test.");
		NDArray gdr = NDIO.arange(8).reshape(2, 1, 4).setEnableGradient(true);
		NDArray arr=NDIO.arange(24).reshape(2, 3, 4).setEnableGradient(true);

//		print(gdr);
//		print("---------");
//		printGrad(arr);
		//		print("---------");
		arr.fillGrad(gdr);
		//		printGrad(arr);
		float[][][] gd=
		{
			{
				{0.0f, 1.0f, 2.0f, 3.0f},
				{0.0f, 1.0f, 2.0f, 3.0f},
				{0.0f, 1.0f, 2.0f, 3.0f}
			},
			{
				{4.0f, 5.0f, 6.0f, 7.0f},
				{4.0f, 5.0f, 6.0f, 7.0f},
				{4.0f, 5.0f, 6.0f, 7.0f}
			}
		};

		Test1.test(Test1.equals(gd, arr.getGradient().base), "gradient equals");

	}
	void test4()
	{
		System.out.println("Test 4. save array to file test.");
		NDArray arr = NDIO.arange(24).reshape(2, 3, 4).setEnableGradient(false);
		NDIO.toFile(arr, "/sdcard/test/arrayJson1.json"); // default json type will choosen.
		NDIO.toFile(arr, "/sdcard/test/arrayJson2.json", NDIO.FileType.JSON); // the same as above.
		NDIO.toFile(arr, "/sdcard/test/arrayText.txt", NDIO.FileType.TEXT); // save as text file.
		NDIO.toFile(arr, "/sdcard/test/arrayBin.ndbin", NDIO.FileType.BINARY); // save as binary file.
		// the name can be any name.
		/*
		 result file comparision
		 file size. with the above array.

		 json = 140B | 141B
		 text = 173B | 174B
		 bin  = 114B

		 this is relativelly small in size but when the array size grows the difference gap also gets bigger.
		 */
	}
	void test3()
	{
		System.out.println("Test 3. trimShape test.");
		NDArray ar=NDIO.arange(6).reshape(1, 1, 2, 1, 3);

		print(Arrays.toString(ar.getShape()));
		ar = ar.trimShape();
		print(Arrays.toString(ar.getShape()));
		Test1.test(Arrays.equals(ar.getShape(), new int[]{2,3}), "trim shape.");
	}
	void test2()
	{
		System.out.println("Test 2. sum test.");
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
		float[][] sum0={
			{12,14,16,18},
			{20,22,24,26},
			{28,30,32,34}
		};
		Test1.test(Test1.equals(sum0, ar.base), "sum with axes 0");
		ar = arr.sum(1);
		print(ar);
		float[][]sum1={
			{12,15,18,21},
			{48,51,54,57}
		};
		Test1.test(Test1.equals(sum1, ar.base), "sum with axes 1");
		ar = arr.sum(2);
		print(ar);
		float[][]sum2={
			{6,22,38},
			{54,70,86}
		};
		Test1.test(Test1.equals(sum2, ar.base), "sum with axes 2");

	}
	void test1()
	{
		System.out.println("Test 1. convolution 1d and correlation 1d test.");
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
