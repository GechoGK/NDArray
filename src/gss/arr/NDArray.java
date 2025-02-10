package gss.arr;

import gss.math.*;
import java.util.*;

import static gss.math.Util.*;

public class NDArray
{

	public Shape base;
	public List<NDArray> childs = new ArrayList<>();
	public GradFunc gradientFunction;

	public NDArray(Shape shp)
	{
		this.base = shp;
	}
	public NDArray(int...shape)
	{
		this.base = new Shape(shape);
	}
	public NDArray(float[] data)
	{
		this.base = new Shape(new int[]{data.length}, data);
		// base.data.data = data;
//		for (int i=0;i < data.length;i++)
//			base.data.setData(i, data[i]); 
		//storage.base.values = Arrays.copyOf(data, data.length);
	}
	public NDArray(int[]shape,  float[] data)
	{
		this.base = new Shape(shape, data);
		// base.data.data = data;
//		for (int i=0;i < data.length;i++)
//			base.data.setData(i, data[i]); 
		//storage.base.values = Arrays.copyOf(data, data.length);
	}
	public NDArray(float[][] data)
	{
		float[] dt=Util.flatten(data);
		// base.data.data = dt;
		this.base = new Shape(new int[]{data.length, data[0].length}, dt);
//		for (int i=0;i < data.length;i++)
//			base.data.setData(i, dt[i]);
		// storage.base.values = Util.flatten(data);
	}
	public NDArray(int[]shape, float[][] data)
	{

		float[] dt=Util.flatten(data);
		// base.data.data = dt;
		this.base = new Shape(shape, dt);
//		for (int i=0;i < data.length;i++)
//			base.data.setData(i, dt[i]);
		// storage.base.values = Util.flatten(data);
	}
	public NDArray setEnableGradient(boolean enable)
	{
		base.setEnableGradient(enable);
		return this;
	}
	public boolean requiresGradient()
	{
		return base.data.requireGradient;
	}
	public int getLength()
	{
		return base.length;
	}
	public int[] getShape()
	{
		return base.shape;
	}
	public int getDim()
	{
		return base.dim;
	}
	public NDArray get(int...sh)
	{
		if (sh.length == 0)
			throw new RuntimeException("index can't be empty :" + Arrays.toString(sh));
		return fromShape(base.get(sh));
	}
	public float getFloat(int...sh)
	{
		if (sh.length == 0)
			throw new RuntimeException("index can't be empty :" + Arrays.toString(sh));
		return base.getFloat(sh);
	}
	public float getFlat(int p)
	{
		return base.getFlat(p);
	}
	public void set(int...sh, float v)
	{
		base.set(sh, v);
	}
	public void setGrad(int...sh, float v)
	{
		base.setGrad(sh, v);
	}
	public void setFloat(int...sh, float v)
	{
		base.setExact(sh, v);
	}
	public void setFlat(int p, float v)
	{
		base.setFlat(p, v);
	}
	// // // gradient
	// gradient set end get functions.

	/////// below not tested.

	public Value getExactValue(int...index)
	{
		return base.getExactValue(index);
	}
	public Value getFlatValue(int p)
	{
		return base.getFlatValue(p);
	}
	public float getExactGrad(int...index)
	{
		return base.getExactGrad(index);
	}
	public float getFlatGrad(int pos)
	{
		return base.getFlatGrad(pos);
	}
	public void setExactValue(Value v, int...index)
	{
		base.setExactValue(v, index);
	}
	public void setFlatValue(Value v, int p)
	{
		base.setFlatValue(v, p);
	}
	public void setExactGrad(int[]index, float val)
	{
		base.setExactGrad(index, val);
	}
	public void setFlatGrad(int pos, float val)
	{
		base.setFlatGrad(pos, val);
	}
	// end set and get functions.
	// // // end gradient.
	public NDArray fromShape(Shape str)
	{
		NDArray arr=new NDArray(str);
		arr.childs.addAll(this.childs);
		arr.gradientFunction = this.gradientFunction;
		return arr;
	}
	// shape functions
	public NDArray broadcast(int...newShape)
	{
		Shape sh=base.broadcast(newShape);
		if (sh == this.base)
			return this;
		NDArray str = fromShape(sh);
		str.setGradientFunction(GradFunc.stepGradient, this);
		return str;
	}
	// view into another shape.
	public NDArray view(int...newShape)
	{
//		if (base.length != Util.length(newShape))
//			throw new RuntimeException("invalid array length");
		NDArray arr = fromShape(base.view(newShape));
		arr.setGradientFunction(GradFunc.stepGradient, this);
		return arr;
	}
	public NDArray reshape(int...newShape)
	{
		NDArray arr = fromShape(base.reshape(newShape));
		arr.setGradientFunction(GradFunc.stepGradient, this);
		return arr;
	}
	public NDArray transpose(int...order)
	{
		NDArray arr = fromShape(base.transpose(order));
		arr.setGradientFunction(GradFunc.stepGradient, this);
		return arr;
	}
	public NDArray transpose()
	{
		NDArray arr = fromShape(base.transpose());
		arr.setGradientFunction(GradFunc.stepGradient, this);
		return arr;
	}
	public void setGradientFunction(GradFunc func, NDArray...chlds)
	{
		this.gradientFunction = func;
		this.childs.clear();
		for (NDArray ar:chlds)
			this.childs.add(ar);
	}
	public void backward()
	{
		if (gradientFunction == null)
			return;
		// throw new RuntimeException("gradient function not found = " + gradientFunction);
		// System.out.println("backward " + gradientFunction);
		gradientFunction.backward(this, childs.toArray(new NDArray[0]));
		for (NDArray arr:childs)
			arr.backward();
	}
	/*
	 // this function returns the broadcasted shape of the two array.
	 // the commom shape for two arrays.
	 // the functions take the longer array as a result.
	 example.
	 1.  shape.1 = [2,1,4,3]
	 ..  shape.2 = [2,5,4,1]  these shapes can be broadcasted. one into another.
	 ==== result = [2,5,4,3]

	 2.  shape.1 = [4,3,2,6,1,6]
	 ..  shape.2 =         [3,6] also the se two shapes can be broadcast one into another.
	 ===  result = [4,3,2,6,3,6] ...

	 3.  sh1  =        [1, 2, 3]
	 ..  sh2  =     [5, 4, 2, 1] taking the broadcastable shape between them.
	 ..  newShape = [5, 4, 2, 3]
	 // when computing a loop start from the end and down to 0.
	 */
	public static int[] getCommonShape(int[] shape1, int[] shape2)
	{
		// System.out.println("finding common broadcastable shape for");
		// System.out.println(Arrays.toString(shape1) + ", " + Arrays.toString(shape2));
		int[] newShape1=Arrays.copyOf(shape1.length > shape2.length ?shape1: shape2, Math.max(shape1.length, shape2.length));
		int[] newShape2=shape1.length > shape2.length ?shape2: shape1;
		// System.out.println("temporary result shape ");
		// System.out.println("== " + Arrays.toString(newShape1));
		// System.out.println("== " + Arrays.toString(newShape2));
		for (int i=0;i < Math.min(shape1.length, shape2.length);i++)
		{
			int sh1=Util.getAtR(newShape1, i);
			int sh2=Util.getAtR(newShape2, i);
			// System.out.println("... [" + sh1 + ", " + sh2 + "]");
			if (sh1 != sh2 && (sh1 != 1 && sh2 != 1))
				throw new RuntimeException("not broadcastable shape at. ( " + sh1 + " != " + sh2 + " )");
			newShape1[newShape1.length - 1 - i] = sh1 == 1 ?sh2: sh1;
		}
		return newShape1;
	}
	// end shape functions
	public NDArray copy()
	{
		return fromShape(base.copy());
	}
	// operator implementation
	public NDArray add(float f)
	{
		return add(new NDArray(new float[]{f}));
	}
	public NDArray add(NDArray other)
	{
		int[] shp=getCommonShape(this.base.shape, other.base.shape);
		NDArray a1=broadcast(shp);
		NDArray a2=other.broadcast(shp);
		NDArray arrOut=new NDArray(shp).setEnableGradient(a1.requiresGradient() || a2.requiresGradient());
		arrOut.setGradientFunction(GradFunc.additionGradient, a1, a2);
		// System.out.println("length " + a1.getLength() + " == " + a2.getLength());
		if (a1.getLength() != a2.getLength())
			throw new RuntimeException("can't make operation with two different array length(" + a1.getLength() + " != " + a2.getLength() + ")");
		for (int i=0;i < a1.getLength();i++)
		{
			float v1=a1.getFlat(i);
			float v2=a2.getFlat(i);
			arrOut.setFlat(i, v1 + v2);
		}
		return arrOut;
	}
	// test addition.
	// this method is for only testing purpose dont use it.
	// to check vakue gradient we used this method.
//	public NDArray add2(NDArray other)
//	{
//		int[] shp=getCommonShape(this.base.shape, other.base.shape);
//		NDArray a1=broadcast(shp);
//		NDArray a2=other.broadcast(shp);
//		NDArray arrOut=new NDArray(shp).setEnableGradient(a1.requiresGradient() || a2.requiresGradient());
//		arrOut.setGradientFunction(GradFunc.itemGradient, a1, a2);
//		// System.out.println("length " + a1.getLength() + " == " + a2.getLength());
//		if (a1.getLength() != a2.getLength())
//			throw new RuntimeException("can't make operation with two different array length(" + a1.getLength() + " != " + a2.getLength() + ")");
//		for (int i=0;i < a1.getLength();i++)
//		{
//			Value v1=a1.getFlatValue(i);
//			Value v2=a2.getFlatValue(i);
//			// System.out.println(v1 + " ,,,, " + v2);
//			arrOut.setFlatValue(v1.add(v2), i);
//		}
//		return arrOut;
//	}
	public NDArray sub(float f)
	{
		return sub(new NDArray(new float[]{f}));
	}
	public NDArray sub(NDArray other)
	{
		int[] shp=getCommonShape(this.base.shape, other.base.shape);
		NDArray a1=broadcast(shp);
		NDArray a2=other.broadcast(shp);
		NDArray arrOut=new NDArray(shp).setEnableGradient(a1.requiresGradient() || a2.requiresGradient());
		arrOut.setGradientFunction(GradFunc.subtractionGradient, a1, a2);
		// System.out.println("length " + a1.getLength() + " == " + a2.getLength());
		if (a1.getLength() != a2.getLength())
			throw new RuntimeException("can't make operation with two different array length(" + a1.getLength() + " != " + a2.getLength() + ")");
		for (int i=0;i < a1.getLength();i++)
		{
			float v1=a1.getFlat(i);
			float v2=a2.getFlat(i);
			arrOut.setFlat(i, v1 - v2);
		}
		return arrOut;
	}
	public NDArray mul(float f)
	{
		return mul(new NDArray(new float[]{f}));
	}
	public NDArray mul(NDArray other)
	{
		int[] shp=getCommonShape(this.base.shape, other.base.shape);
		NDArray a1=broadcast(shp);
		NDArray a2=other.broadcast(shp);
		NDArray arrOut=new NDArray(shp).setEnableGradient(a1.requiresGradient() || a2.requiresGradient());
		arrOut.setGradientFunction(GradFunc.multiplicationGradient, a1, a2);
		// System.out.println("length " + a1.getLength() + " == " + a2.getLength());
		if (a1.getLength() != a2.getLength())
			throw new RuntimeException("can't make operation with two different array length(" + a1.getLength() + " != " + a2.getLength() + ")");
		for (int i=0;i < a1.getLength();i++)
		{
			float v1=a1.getFlat(i);
			float v2=a2.getFlat(i);
			arrOut.setFlat(i, v1 * v2);
		}
		return arrOut;
	}
	// test multiplication
	// this method is for only testing purpose dont use it.
	// to check vakue gradient we used this method.
//	public NDArray mul2(NDArray other)
//	{
//		int[] shp=getCommonShape(this.base.shape, other.base.shape);
//		NDArray a1=broadcast(shp);
//		NDArray a2=other.broadcast(shp);
//		NDArray arrOut=new NDArray(shp).setEnableGradient(a1.requiresGradient() || a2.requiresGradient());
//		arrOut.setGradientFunction(GradFunc.itemGradient, a1, a2);
//		// System.out.println("length " + a1.getLength() + " == " + a2.getLength());
//		if (a1.getLength() != a2.getLength())
//			throw new RuntimeException("can't make operation with two different array length(" + a1.getLength() + " != " + a2.getLength() + ")");
//		for (int i=0;i < a1.getLength();i++)
//		{
//			Value v1=a1.getFlatValue(i);
//			Value v2=a2.getFlatValue(i);
//			arrOut.setFlatValue(v1.mul(v2), i);
//		}
//		return arrOut;
//	}
	public NDArray div(float f)
	{
		return div(new NDArray(new float[]{f}));
	}
	public NDArray div(NDArray other)
	{
		int[] shp=getCommonShape(this.base.shape, other.base.shape);
		NDArray a1=broadcast(shp);
		NDArray a2=other.broadcast(shp);
		NDArray arrOut=new NDArray(shp).setEnableGradient(a1.requiresGradient() || a2.requiresGradient());
		arrOut.setGradientFunction(GradFunc.divisionGradient, a1, a2);
		// System.out.println("length " + a1.getLength() + " == " + a2.getLength());
		if (a1.getLength() != a2.getLength())
			throw new RuntimeException("can't make operation with two different array length(" + a1.getLength() + " != " + a2.getLength() + ")");
		for (int i=0;i < a1.getLength();i++)
		{
			float v1=a1.getFlat(i);
			float v2=a2.getFlat(i);
			arrOut.setFlat(i, v1 / v2);
		}
		return arrOut;
	}
	public NDArray pow(float f)
	{
		return pow(new NDArray(new float[]{f}));
	}
	public NDArray pow(NDArray exp)
	{
		int[] shp=getCommonShape(this.base.shape, exp.base.shape);
		NDArray a1=broadcast(shp);
		NDArray a2=exp.broadcast(shp);
		NDArray arrOut=new NDArray(shp).setEnableGradient(a1.requiresGradient() || a2.requiresGradient());
		arrOut.setGradientFunction(GradFunc.powGradient, a1, a2);
		// System.out.println("length " + a1.getLength() + " == " + a2.getLength());
		if (a1.getLength() != a2.getLength())
			throw new RuntimeException("can't make operation with two different array length(" + a1.getLength() + " != " + a2.getLength() + ")");
		for (int i=0;i < a1.getLength();i++)
		{
			float v1=a1.getFlat(i);
			float v2=a2.getFlat(i);
			arrOut.setFlat(i, (float)Math.pow(v1 , v2));
		}
		return arrOut;
	}
	public NDArray vStack()
	{
		int[] shp=getVStackShape(this.base.shape);
		NDArray tmp= this.view(shp);
		NDArray out=tmp.copy();
		out.setGradientFunction(GradFunc.vStackGradient, tmp);
		return out;
	}
	private int[] getVStackShape(int[]shp)
	{
		if (shp.length == 1)
			return new int[]{shp[0],1};
		else if (shp.length == 2)
			return shp;
		else
		{
			int[]s=Arrays.copyOfRange(shp, 1, shp.length);
			s[0] = shp[0] * shp[1];
			// System.out.println("org " + Arrays.toString(shp));
			// System.out.println("new " + Arrays.toString(s));
			return s;
		}
	}
	public NDArray hStack()
	{
		int[] shp=getHStackShape(this.base.shape);
		int[] tp=range(getShape().length);
		tp[0] = 1;
		tp[1] = 0;
		NDArray tmp=this.transpose(tp);
		tmp = tmp.view(shp);
		NDArray out=tmp.copy();
		out.setGradientFunction(GradFunc.hStackGradient, tmp);
		return out;
	}
	private int[] getHStackShape(int[]shp)
	{
		if (shp.length == 1)
			return shp;
		else if (shp.length == 2)
		{
			return new int[shp[0] * shp[1]];
		}
		else
		{
			int[]s=Arrays.copyOfRange(shp, 1, shp.length);
			s[0] = shp[1];
			s[1] = shp[0] * shp[2];
			// System.out.println("org " + Arrays.toString(shp));
			// System.out.println("new " + Arrays.toString(s));
			return s;
		}
	}
	private int[] range(int end)
	{
		int[] v=new int[end];
		for (int i=0;i < end;i++)
			v[i] = i;
		return v;
	}
	public NDArray dot(NDArray other)
	{
		/// fix. dot product is made using 2d array.
		/// this function doesn't work. works 10%
//		if (this.getLength() != other.getLength())
//			throw new RuntimeException("incompatable arrays for dot product :: the two arrays must be the same length.");
		// if (getDim() < 2 || other.getDim() < 2)
		// 	throw new RuntimeException("incompatable dimention for dot product");
		if (other.getDim() == 1)
			other = other.reshape(other.getShape()[0], 1);
		NDArray a1=this;
		if (this.getDim() == 1)
			a1 = a1.reshape(1, a1.getShape()[0]);
		if (getAtR(a1.getShape(), 0) != (other.getDim() == 1 ?getAtR(other.getShape(), 0): getAtR(other.getShape(), 1)))
			throw new RuntimeException("shape not equal(" + getAtR(a1.getShape(), 0) + " != " + getAtR(other.getShape(), 1) + ")");
		int[]outputShape=getShapeForDot(a1.getShape(), other.getShape());

		NDArray[] prps=prepareArrayForDotProduct(a1, other);
		NDArray x=prps[0];
		NDArray y=prps[1];
//		System.out.println("-----  x data  -----");
//		Util.print(x);
//		System.out.println("-----  y data  -----");
//		Util.print(y);
		// preparing...
		int xr=x.getShape()[0];
		int yr=y.getShape()[0];
		int xc=x.getShape()[1];
		// System.out.println("copying...");
		float[][] xx=x.base.to2DArray(null);
		float[][] yy=y.base.to2DArray(null);
		// System.out.println("copy done");
		float[] out=new float[xr * yr];
		int ps=0;
		// System.out.println("output =" + out.length);
		for (int i=0;i < xr;i++)
			for (int j=0;j < yr;j++)
			{
				float sm=0;
				for (int k=0;k < xc;k++)
					sm += xx[i][k] * yy[j][k];
				out[ps] = sm;
				ps++;
			}
		NDArray arrOut=new NDArray(outputShape, out).setEnableGradient(this.requiresGradient() || other.requiresGradient());
		// we dont't know the gradient function for dot product so we use itemGradient to it automatically calculate for us.
		arrOut.setGradientFunction(GradFunc.dotGradient, x, y);
		// System.out.println("length " + a1.getLength() + " == " + a2.getLength());

		return arrOut;
	}
	public NDArray dot2(NDArray other)
	{
		/// fix. dot product is made using 2d array.
		/// this function doesn't work. works 10%
//		if (this.getLength() != other.getLength())
//			throw new RuntimeException("incompatable arrays for dot product :: the two arrays must be the same length.");
		if (getDim() < 2 || other.getDim() < 2)
			throw new RuntimeException("incompatable dimention for dot product");
		if (getAtR(getShape(), 0) != getAtR(other.getShape(), 1))
			throw new RuntimeException("shape not equal(" + getAtR(getShape(), 0) + " != " + getAtR(other.getShape(), 1) + ")");
		int[]outputShape=getShapeForDot(getShape(), other.getShape());

//		NDArray[] prps=prepareArrayForDotProduct(this, other);
		NDArray x=this.view(-1, getAtR(getShape(), 0));
		NDArray y=other.hStack();
		// preparing...
		int xr=x.getShape()[0];
		int yr=y.getShape()[0];
		int xc=x.getShape()[1];
		// System.out.println("copying...");
		Value[][] xx=x.base.to2DValueArray();
		Value[][] yy=y.base.to2DValueArray();
		// System.out.println("copy done");
		// float[] out=new float[xr * yr];
		NDArray arrOut=new NDArray(outputShape).setEnableGradient(this.requiresGradient() || other.requiresGradient());
		int ps=0;
		// System.out.println("output =" + out.length);
		for (int i=0;i < xr;i++)
			for (int j=0;j < yr;j++)
			{
				Value sm=null;
				for (int k=0;k < xc;k++)
				{
					Value m=xx[i][k].mul(yy[j][k]);
					if (sm == null)
						sm = m;
					else
						sm = sm.add(m);
				}
				arrOut.setFlatValue(sm, ps);
				ps++;
			}
		// we dont't know the gradient function for dot product so we use itemGradient to it automatically calculate for us.
		arrOut.setGradientFunction(GradFunc.itemGradient, x, y);
		// System.out.println("length " + a1.getLength() + " == " + a2.getLength());

		return arrOut;
	}
	private int[] getShapeForDot(int[]s1, int[]s2)
	{
		/*
		 output shape is determined by the given array's shape.
		 example  a.shape = (x,y,z)
		 ..       b.shape = (h,i,j) then
		 ..
		 ..       c = a.dot(b)
		 ..
		 .. first we need to check if "z" and "i" are equal if true.
		 the output shape(c.shape) would be (x,y,h,j) it increase by 1 dimension.

		 */
		if (getAtR(s1, 0) != getAtR(s2, 1))
			throw new RuntimeException("dimensions not equal to compute dot product (" + getAtR(s1, 0) + " != " + getAtR(s2, 1) + ")");
		int[] newShape=new int[s1.length + s2.length - 2];
		// System.out.println("finding shape for dot. ===" + newShape.length);
		// System.out.println(Arrays.toString(s1) + ", " + Arrays.toString(s2));
		// System.out.println("new array length =" + newShape.length);

		for (int i=0;i < s1.length - 1;i++)
			newShape[i] = s1[i];
		int str=s1.length - 1;
		for (int i=0;i < s2.length - 1;i++)
			newShape[str + i] = s2[i];
		newShape[newShape.length - 1] = s2[s2.length - 1];
		// System.out.println("new Shape =" + Arrays.toString(newShape));
		return newShape;
	}
	public NDArray[]prepareArrayForDotProduct(NDArray a1, NDArray a2)
	{
		NDArray x=a1.view(-1, Util.getAtR(a1.getShape(), 0));
		// don't transpose because transposing and hview is computationally expensive, so we use only view.
		// and also we use anothe toe of dot product. by considering no transpose.
		NDArray y=a2.view(-1, Util.getAtR(a2.getShape(), 0));
		return new NDArray[]{x,y};
	}
	public NDArray dot3(NDArray b)
	{
		// peefect for dot product wothout transposing.
		if (b.getDim() == 1)
			b = b.reshape(b.getShape()[0], 1);
		int[] newShape=getShapeForDot(this.getShape(), b.getShape());
		// print("new shape =" + Arrays.toString(newShape));
		NDArray[] arrs=prepareArrayForDotProduct(this, b);
		NDArray a = arrs[0];
		b = arrs[1];
		float[][] af=a.base.to2DArray(null);
		float[][] bf=b.base.to2DArray(null);

		float[][] fout=sdot(af, bf);
		// print(fout.length + ", " + fout[0].length);
		NDArray out=new NDArray(newShape, fout).setEnableGradient(a.requiresGradient() | b.requiresGradient());
		out.setGradientFunction(GradFunc.dotGradient, a, b);
		return out;
	}
	private float[][] sdot(float[][] a1, float[][] a2)
	{
		// print("calculating specialized do product.");
		// print(a1.length + ", " + a1[0].length + " :: " + a2.length + ", " + a2[0].length);
		if (a2.length % a1[0].length != 0)
			throw new RuntimeException("unable to compute dot product");
		int cnt=a2.length / a1[0].length;
		float[][] m=new float[a1.length][a2[0].length * cnt];
		// print("output =" + m.length + ", " + m[0].length);
		// print("count =" + cnt);
		for (int xr=0;xr < a1.length;xr++)
		{
			for (int yc=0;yc < a2[0].length;yc++)
			{
				for (int cn=0;cn < cnt;cn++)
				{
					float s=0;
					for (int xc=0;xc < a1[0].length;xc++)
					{
						int ps=cn * a1[0].length + xc;
						s += a1[xr][xc] * a2[ps][yc];
					}
					int ps=a2[0].length * cn + yc;
					m[xr][ps] = s;	
				}
			}
		}
		// print(m);
		return m;
	}
//	public NDArray dot4(NDArray b)
//	{
//		// peefect for dot product wothout transposing.
//		if (b.getDim() == 1)
//			b = b.reshape(b.getShape()[0], 1);
//		int[] newShape=getShapeForDot(this.getShape(), b.getShape());
//		// print("new shape =" + Arrays.toString(newShape));
//		NDArray[] arrs=prepareArrayForDotProduct(this, b);
//		NDArray a = arrs[0];
//		b = arrs[1];
//		Value[][] af=a.base.to2DValueArray();
//		Value[][] bf=b.base.to2DValueArray();
//
//		Value[][] fout=sdotv(af, bf);
//		// print(fout.length + ", " + fout[0].length);
//		NDArray out=new NDArray(newShape, fout).setEnableGradient(a.requiresGradient() | b.requiresGradient());
//		out.setGradientFunction(GradFunc.dotGradient, a, b);
//		return out;
//	}
//	private float[][] sdotv(float[][] a1, float[][] a2)
//	{
//		// print("calculating specialized do product.");
//		// print(a1.length + ", " + a1[0].length + " :: " + a2.length + ", " + a2[0].length);
//		if (a2.length % a1[0].length != 0)
//			throw new RuntimeException("unable to compute dot product");
//		int cnt=a2.length / a1[0].length;
//		float[][] m=new float[a1.length][a2[0].length * cnt];
//		// print("output =" + m.length + ", " + m[0].length);
//		// print("count =" + cnt);
//		for (int xr=0;xr < a1.length;xr++)
//		{
//			for (int yc=0;yc < a2[0].length;yc++)
//			{
//				for (int cn=0;cn < cnt;cn++)
//				{
//					float s=0;
//					for (int xc=0;xc < a1[0].length;xc++)
//					{
//						int ps=cn * a1[0].length + xc;
//						s += a1[xr][xc] * a2[ps][yc];
//					}
//					int ps=a2[0].length * cn + yc;
//					m[xr][ps] = s;	
//				}
//			}
//		}
//		// print(m);
//		return m;
//	}
	// end operator implementation.
}
