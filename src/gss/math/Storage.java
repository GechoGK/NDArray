package gss.math;

import java.util.*;

import static gss.math.Util.*;

public class Storage
{
	/*
	 broadcast, reshape, and view needs modifiction.
	 all of them return the current storage by altering to a specific need.
	 especually (view), also changes the baseShape.
	 .:. the methods need to return the copy of the storage. inorder to preserve the original one.
	 in that case we can retrive keep the base shape untouched.
	 */
	/* bug.1
	 // to solve view Set|Change problem.
	 */
	// ------------
	/* // bug.3
	 problem. broadcast on subdim
	 Storage str=new Storage(2,2);
	 fillRand(str);
	 str  = [[5, 10],
	 ....... [7, 15]] 
	 Storage str2 = str.get(1);
	 str2  = [7, 15] str at index 1

	 Storage str3 = str2.broadcast(20, 2);
	 str3  = [[7, 15],
	 ......   [7, 15],
	 ......     ...
	 .......  [7, 15]]  (Expected output) the output should be this,
	 but instead it outputs like this --v
	 str3  = [[7, 15],
	 .......  [5. 10],
	 .......  [5, 10],
	 .......    ...
	 .......  [5, 10]]  this is due to data access bug,
	 which is the line
	 that maps the broadcasted shape into original one.
	 // int shapeInd =  Math.min(index[i], baseShape[i] - 1);
	 // TO-DO fix later.
	 // solved by passing the base shape as an argument to the constructor.
	 */
	// adding gradient track.
	/*
	 modify -- copy. to adjust the graidient data aswell.
	 // new methods needed. for gradient.
	 getFloatGrad(...); ✓
	 getFlatGrad(...);  ✓
	 setExactGrad(...); ✓
	 setFlatGrad(...);  ✓
	 setGrad(...);      ✓  

	 */
	public Data base;
	public int[] shape;
	public int[] baseShape;
	public int dim;
	public int offset;
	public int length;
	public int[] baseSum;
	public boolean broadcasted=false;

	public Storage(int...shape)
	{
		this(new Data(shape), shape, 0, false, null);
	}
	public Storage(int[]shape, boolean requireGrad)
	{
		this(new Data(shape, requireGrad), shape, 0, false, null);
	}
	private Storage(Data data, int[]shape, int offset, boolean isBroadcasted, int[]bsShape)
	{
		this.broadcasted = isBroadcasted;
		// this.requiresGradient = requiresGrad;
		init(data, shape, offset, bsShape);
	}
	private void init(Data dt, int[]sh, int off, int[]bsShape)
	{
		this.base = dt;
		if (bsShape == null)
			bsShape = base.shape;
		this.shape = Arrays.copyOf(sh, sh.length);
		prepare(sh, off, bsShape);
	}
	private void prepare(int[]sh, int off, int[]bsShape)
	{
		// this.sum = new int[sh.length];
		this.baseShape = new int[sh.length];
		Arrays.fill(baseShape, 1);
		overlap(bsShape, baseShape);
		// System.out.println("====== base = " + Arrays.toString(base.shape) + ",\nbaseb= " + Arrays.toString(baseShape) + ",\ncurr = " + Arrays.toString(shape));
		// this.sum = sumShapes(sh, sum);
		this.baseSum = sumShapes(baseShape, baseSum); // sum shapes according to baseShape.
		// System.out.println(Arrays.toString(baseSum));
		this.dim = sh.length;
		this.offset = off;
		this.length = length(sh);//  sum.length == 0 ?1: sum[0];
		// System.out.println(Arrays.toString(sum));
	}
	public Storage get(int...index)
	{
		// fix Vie Set|Change problem.
		// by keep tracking parent shapes.
		// reverse order.
		if (index.length > shape.length)
			throw new IndexOutOfBoundsException();
		// System.out.println(Arrays.toString(sum));
		if (index.length == shape.length)
		{
			// System.out.println("equal index");
			// the output value would be 1 dim. with 1 length.
			int newPos=0;
			for (int i=0;i < index.length;i++)
			{
				if (index[i] >= shape[i])
					throw new IndexOutOfBoundsException();

				int shapeInd=Math.min(index[i], baseShape[i] - 1);
				// check if the index at i is not out o bound.
				newPos += shapeInd * (i == index.length - 1 ?1: baseSum[i + 1]); // sum[i+1] -> is a problem.(IndexOutOfBoundException)
				// System.out.println("== " + newPos);
			}
			// System.out.println("final pos to return " + newPos);
			return new Storage(base, new int[]{1}, offset + newPos, broadcasted, baseShape);
		}
		else
		{
			int newPos=0;
			// int newLength=sum[index.length];
			int[] newShape=new int[shape.length - index.length];
//			for (int i=0;i < index.length;i++)
//			{
//				if (index[i] >= shape[i])
//					throw new IndexOutOfBoundsException();
//				newPos += index[i] * sum[i + 1];
//				// System.out.println(pos);
//			}
			int strPos=index.length;
			// int lpMax=;
			for (int i=0;i < Math.max(newShape.length, index.length);i++)
			{
				if (i < newShape.length)
				{
					int sm=strPos + i;
					newShape[i] = shape[sm];
				}
				if (i < index.length)
				{
					if (index[i] >= shape[i])
						throw new IndexOutOfBoundsException();
					int shapeInd = Math.min(index[i], baseShape[i] - 1); // index[i];

					newPos += shapeInd * baseSum[i + 1];
				}
				// System.out.println("== " + (sm));
			}
			// System.out.println("new pos " + newPos);
			// System.out.println("new length " + newLength);
			// System.out.println("new Shape " + Arrays.toString(newShape));
			// System.out.println("returning array.");
			return new Storage(base, newShape, offset + newPos, broadcasted, baseShape);
		}
		// return null;
	}
	public Storage set(int[]sh, Storage val)
	{
		// this function sets a value to the array from array.
		// !! two arrays must be equal inorder to fit the vakues perfectly.
		Storage str=get(sh);
		if (!isBrodcastable(val.shape, str.shape)) // chech the incoming storage can be broadcasted to the placement storage.
			throw new IndexOutOfBoundsException("set array faild, due to mismatch shapes.(value can't be broadcasted)");
		val.broadcast(str.shape);
		for (int i=0;i < str.length;i++)
			str.setFlat(i, val.getFlat(i)); // lazy assign.
		return this;
	}
//	public float getFloat3(int...index)
//	{
//		if (index.length != shape.length)
//			throw new IndexOutOfBoundsException();
//		// System.out.print("==== index= " + Arrays.toString(index));
//		int newPos=0;
//		for (int i=0;i < index.length;i++)
//		{
//			int shapeInd=index[i]; // Math.min(index[i],baseShape[i]);
//			// check if the index at i is not out of bound.
//			if (shapeInd >= shape[i])
//				throw new IndexOutOfBoundsException();
//			newPos += shapeInd * (i == index.length - 1 ?1: sum[i + 1]);
//			// System.out.print("..ps " + newPos + ".." + index[i] + " ~ " + shapeInd + ".");
//		}
//		int finalInd=offset + newPos;
//		// System.out.println(", pos=" + newPos + ", off= " + offset + ", len= " + length + ", dataLen= " + base.length + "==");
//		// if (finalIndex < offset || finalIndex >= length + offset) // error prune. because the offset can be bigger than the actual data length when brodcasted.
//		//  	throw new IndexOutOfBoundsException();
//		return base.values[((offset + finalInd % length)) % base.length];
//	}
	public float getFloat(int...index) // this method works.
	{
		return getFloatInt(index, base.values);
	}
	public float getFloatGrad(int...index)
	{
		return getFloatInt(index, base.grads);
	}
	// internal method.
	public float getFloatInt(int[] index,  float[] array)
	{
		if (index.length != shape.length) // change this " != " to " > " and implement the if block. or use backward loop.
			throw new IndexOutOfBoundsException();
		// if (index.length < shape.length)
		// {
		// if the index length is less than the shape length. we fill the rest with (0). eg.
		// eg index =[5] -> we change into [0,0,5]  assume if the shape was [2,3,6]; this also adds overhead.
		// todo.
		// }
		int newPos=0;
		// the loop have error when we use index.length < shape.length, so use backward looping.
		for (int i=0;i < index.length;i++)
		{
			int shapeInd =  Math.min(index[i], baseShape[i] - 1);
			// baseShape[i] -1 ; because . the baseShape minimum value is 1, but 1 means it's acess index is 0, so to make it zero we need to -1;
			// the big issue for 2 days;
			// check if the index at i is not out of bound.
			if (shapeInd >= shape[i])
				throw new IndexOutOfBoundsException();
			newPos += shapeInd * (i == index.length - 1 ?1: baseSum[i + 1]);
		}
		int finalIndex=(offset + newPos);
		// System.out.println("off = " + offset + ", newP = " + newPos + ", final pos = " + finalIndex + ", len= " + length);
		// System.out.print("--" + finalIndex + "--" + offset + "--");
		return array[finalIndex % base.length]; // base.values[(offset + (finalIndex % length)) % base.length];
	}
	public Storage setExact(int[]index, float val) // this method works.
	{
		return setExactInt(index, val, base.values, false);
	}
	public Storage setExactGrad(int[]index, float val)
	{
		return setExactInt(index, val, base.grads, true);
	}
	// internal method.
	private Storage setExactInt(int[] index, float val, float[]array, boolean append)
	{
		// this method sets a value (one value) to the array.
		// if the index is not match the shaoe of the storage. it fails.
		// the base for setFlat(int ind,float bal);
		if (index.length != shape.length) // change this " != " to " > " and implement the if block. or use backward loop.
			throw new IndexOutOfBoundsException();
		// if (index.length < shape.length)
		// {
		// if the index length is less than the shape length. we fill the rest with (0). eg.
		// eg index =[5] -> we change into [0,0,5]  assume if the shape was [2,3,6]; this also adds overhead.
		// todo.
		// }
		int newPos=0;
		// the loop have error when we use index.length < shape.length, so use backward looping.
		for (int i=0;i < index.length;i++)
		{
			int shapeInd =  Math.min(index[i], baseShape[i] - 1);
			// baseShape[i] -1 ; because . the baseShape minimum value is 1, but 1 means it's acess index is 0, so to make it zero we need to -1;
			// the big issue for 2 days;
			// check if the index at i is not out of bound.
			if (shapeInd >= shape[i])
				throw new IndexOutOfBoundsException();
			newPos += shapeInd * (i == index.length - 1 ?1: baseSum[i + 1]);
		}
		int finalIndex=(offset + newPos);
		// System.out.println("off = " + offset + ", newP = " + newPos + ", final pos = " + finalIndex + ", len= " + length);
		// System.out.print("--" + finalIndex + "--" + offset + "--");
		if (append)
			array[finalIndex % base.length] += val; // base.values[(offset + (finalIndex % length)) % base.length];
		else
			array[finalIndex % base.length] = val;
		return this;
	}
	public Storage set(int[] shape, float val)
	{
		Storage str=get(shape);
		for (int i=0;i < str.length;i++)
		{
			str.setFlat(i, val);
		}
		return this;
	}
	public Storage setGrad(int[] shape, float val)
	{
		Storage str=get(shape);
		for (int i=0;i < str.length;i++)
		{
			str.setFlatGrad(i, val);
		}
		return this;
	}
	public float getFlat(int index)
	{
		return getFlatInt(index, base.values);
	}
	public float getFlatGrad(int index)
	{
		return getFlatInt(index, base.grads);
	}
	// internal method.
	private float getFlatInt(int index, float[]array)
	{
		/*
		 // this implementation is costy(slow). find a better way if posible.
		 -- one thing we can do is we can check whether the shape is brodcasted or not.
		 if it is broadcasted then
		 ----- we stick with this implementation.
		 otherwise
		 ----- we can use the old modules method, that would be a bit faster.
		 ....
		 although working with broadcasted shape doesn't affect that shape that are not broadcasted.so we stick with this implementation for now.

		 +++ always check bemchmarks before deciding which one to use.

		 */
		int[] shp=getShape(index);
		// System.out.println("shape at index (" + ind + ") = " + Arrays.toString(indShape));
		/*
		 merge the getFloat loop inside this method.
		 in that case we can use one loop instead o two loops iterate through the same shape.
		 // TO-DO merge loops to this method and getride of getFloat(indShape); call.
		 */
		return getFloatInt(shp, array);
		/*
		 the code down below is just for access index without considering shape and broadcasting.
		 */
		//  !!!!! the code below can be used if ths shape is not broadcasted.

		// System.out.println("input index " + index);
		// System.out.println("computed index " + index);

		// index = offset + index;
		// System.out.println("real index " + index);
		// return base.values[index % base.length];
		// throw new IndexOutOfBoundsException();
	}
	public Storage setFlat(int index, float val)
	{
		int[] sh=getShape(index);
		setExact(sh, val);
		return this;
	}
	public Storage setFlatGrad(int index, float val)
	{
		int[] sh=getShape(index);
		setExactGrad(sh, val);
		return this;
	}
	public void zeroGrad()
	{
		if (base.requiresGrad)
			Arrays.fill(base.grads, 0);
	}
	// public float getFlatNotBroadcasted(int index)
	// {
	// System.out.println("input index " + index);
	// System.out.println("computed index " + index);

	// index = offset + index;
	// System.out.println("real index " + index);
	// return base.values[index % base.length];
	// throw new IndexOutOfBoundsException();
	// }
	/*
	 the above function is used for many functions.
	 like view,reshape,...
	 */
	public int[] getShape(int index)
	{
		// this function used to convert index (0-n) into shaps. by iterating all posible combination of shapes, and it returns the combination at the speciic index.
		if (index >= length || index < 0)
			throw new IndexOutOfBoundsException();
		// int ind=index;
		// computationally expensive. avoid it, if u can.
		// System.out.println("getting value at index =" + index);
		int[] indShape=new int[this.shape.length];
		for (int i=this.shape.length - 1;i >= 0;i--) // count down starts from shape.length -1 down to 0.
		{
			indShape[i] = index % this.shape[i];
			index = index / this.shape[i];
		}
		return indShape;
	}
//	public Storage broadcastOld(int...newShape)
//	{
//		// !!! problem position reversed.
//		// check if the new shape is brodcastable with the older one.
//		// if not throw an error. if i is brodcastable shape then move old shape to original shape,
//		// then make new shape to this.shape;
//		if (!broadcasted && !isBrodcastable(this.shape, newShape))
//			throw new IndexOutOfBoundsException("not brodcastable shape " + Arrays.toString(newShape) + " with " + Arrays.toString(this.shape));
//		if (this.shape.length == newShape.length)
//			for (int i=0;i < shape.length;i++)
//				this.shape[i] = newShape[i];
//		else
//			this.shape = Arrays.copyOf(newShape, newShape.length);
//		prepare(shape, offset);
//		broadcasted = true;
//		return this; // for now it changes itself, but for feature return the copy of Storage with the same data.
	//	}
	public Storage broadcast(int...newShape)
	{
		// !!! problem position reversed.
		// check if the new shape is brodcastable with the older one.
		// if not throw an error. if i is brodcastable shape then move old shape to original shape,
		// then make new shape to this.shape;
		if (!broadcasted && !isBrodcastable(this.shape, newShape))
			throw new IndexOutOfBoundsException("not brodcastable shape " + Arrays.toString(newShape) + " with " + Arrays.toString(this.shape));
		Storage str=new Storage(base, newShape, offset, broadcasted, baseShape);
		str.broadcasted = true;
		return str; // for now it changes itself, but for feature return the copy of Storage with the same data.
	}
	public boolean isBrodcastable(int[]orgShape, int[]tarShape)
	{
		if (tarShape.length < orgShape.length)
			return false;
		int len=tarShape.length - orgShape.length;
		// System.out.println("checking... len =" + len);
		for (int i=0;i < orgShape.length;i++)
			if (!(orgShape[i] == tarShape[len + i] || (orgShape[i] == 1 && tarShape[len + i] > 0)))
				return false;
		// System.out.println("brodcastable shape " + Arrays.toString(tarShape) + " with " + Arrays.toString(orgShape));
		return true;
	}
	// methods to implement.
	public boolean requiresGradient()
	{
		return base.requiresGrad;
	}
	public void enbaleGradient()
	{
		base.enableGradient();
	}
	public void disableGradient()
	{
		base.disableGradient();
	}
	public boolean isBroadcastedShape()
	{

		// new code to check if it is broadcasted.
		// experimental, -- uncomment the line below.
		if (this.shape.length > base.shape.length)
			return true; // if the storage shape.length is grrate than base.shape.length, it is due to broadcasting. if not ...
		// eg1. base [2,1] , storage [2,2,2] == it is broadcasted. returns true.
		// eg2. base [5,1] , storage [5,5]   == the condition (this.shape.length > base.shape.length) doesn't satissfy. si ut needs extra check.
		// now check element by element if they are equal.
		// to address subdims use minimum length to check equality Math.min(this.shape.length,base.shape.length); start from the end.
		for (int i=0;i < Math.min(this.shape.length, base.shape.length);i++)
			if  (Util.getAtR(this.shape, i) != Util.getAtR(base.shape, i))
				return true;
		// eg1. base [5,1]   , storage [5,3] == return true. b/c it found d/t array elements 1 and 3 are not the same.
		// eg2. base [2,5,1] , storage [5,3] subdim get(0) == return true. also have different items ignoring the first 1 and check others 1 and 3 again different element.
		return false;
		// return broadcasted; // check whether the shape is original or not.
	}
//	public Storage viewOld(int...newShape)
//	{
//		// have problem when storage is subdim
//		// broadcastable shape not allowed.
//		// videwing this array into other type of shape.
//		// the length must be equal;
//		// if the shape is already broadcasted it doesn't work.
//		if (isBroadcastedShape()) // length will check if
//			throw new RuntimeException("broadcaste shape doesn't allowed viewing into another shape., or copy it before broadcasted.");
//		int len=length(newShape);
//		// System.out.println(len + " == " + length);
//		if (len != length)
//		{
//			throw new IndexOutOfBoundsException("different shape length");
//		}
//		base.setShape(newShape); // problem.
//		init(base, newShape, offset);
//		return this;
	//	}
	public Storage view(int...newShape)
	{
		// have problem when storage is subdim
		// the problem is when base.setShape(int[]) is called.
		// it overwrite the old shape, and also the length of base(data) wil be cliped.

		// !!!
		// broadcastable shape not allowed.
		// videwing this array into other type of shape.
		// the length must be equal;
		// if the shape is already broadcasted it doesn't work.
		if (isBroadcastedShape()) // length will check if
			throw new RuntimeException("broadcaste shape doesn't allowed viewing into another shape., or copy it before broadcasted.");
		int len=length(newShape);
		// System.out.println(len + " == " + length);
		if (len != length)
		{
			throw new IndexOutOfBoundsException("different shape length");
		}
		Data data=base.copy(base.shape);
		data.changeShape(newShape, this.shape); // problem fix it.
		/*
		 --- view Set|Change problem.
		 choose whether data.setShape() or data.changeShape()
		 --- setShape
		 // this is good for dimension reduction. and it works.
		 // but when the array is subdim which is the storage shape and base shape doesn't match , it have errors.
		 example.
		 Storage str=new Storage(3,4);
		 Storage str2=str.view(12); // 2dim aray changes into 1dim array. ✓
		 ----------
		 Storage str=new Storage(2,3,4);
		 str=str.get(1); // len = 12 off = 12, but when we view occurs the offset will chnage into 0, becauae the base.shaoe also chnges (not modified accordinglly).
		 Storage str2=str.view(12); // trying to reduce dim. 
		 // str2.base.shape == [12] because setShape changes all.
		 // expected = [2,12] // the changeShape can selectivelly do that,
		 // although the implementation of chsngeShape can't produce this output instead.
		 // changeShape output = [2,3,12] // which is wrong.
		 */
		Storage str=new Storage(data, newShape, offset, broadcasted, baseShape);
		return str;
	}
	public Storage reshape(int...newShape)
	{
		// !!! the returnin calue must be a new copy of storage.
		// TO-DO  fix return this. instead create a new Storage(...);

		// try to broadcast if posible, if not copy the array.
		// reshape chnges the shape, also the underlaying data shape.
		int len=length(newShape);
		if (length != len)
			throw new RuntimeException("different type of shape is not allowed.");
		if (isBroadcastedShape())
		{
			// System.out.println("reshaping broadcasted shape");
			Storage str=copy();
			str.base.setShape(newShape);
			str.init(str.base, newShape, str.offset, baseShape); // maybe a problem offset should be 0. because the new array is being created. or it is subdim so offset is neded.
			return str;
		}

		base.changeShape(newShape, this.shape); 
		init(base, newShape, offset, baseShape);
		return this;
	}
	public Storage copy()
	{
		// it returns the shape in this class, but the data is copied,
		// if broadcasted shape found it handles data class accordinglly.
		Storage str=new Storage(this.shape, base.requiresGrad);
		for (int i=0;i < str.base.values.length;i++)
			str.base.values[i] = getFlat(i);
//		if (str.requiresGradient)
//		{
//			for (int i=0;i < str.base.grads.length;i++)
//				str.base.grads[i] = getGradFlat(i);
		//		}
		return str;
	}
	@Override
	public String toString()
	{
		return(broadcasted ?"Broadcasted ": "") + "storage(dim :" + dim + ", length :" + length + ", shape :" + Arrays.toString(shape) + ", baseShape " + Arrays.toString(baseShape) + ", offset :" + offset + ") reuqireGrad = " + base.requiresGrad + ".";
	}
}
