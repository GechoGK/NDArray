package gss.math;

import java.util.*;

import static gss.math.Util.*;

public class Storage implements AbsStorage
{
	public Data base;
	public int[] shape;
	public int[] baseShape;
	public int dim;
	public int offset;
	public int length;
	public int[] sum;

	public Storage(int...shape)
	{
		this(new Data(shape), shape, 0);
	}
	public Storage(Data data, int...shape)
	{
		this(data, shape, 0);
	}
	public Storage(Data data, int[]shape, int offset)
	{
		init(data, shape, offset);
	}
	private void init(Data dt, int[]sh, int off)
	{
		this.base = dt;
		this.shape = Arrays.copyOf(sh, sh.length);
		// this.data.shape = Arrays.copyOf(shape, shape.length);
		// this.data.length = sum(shape, 0);
		prepare(sh, off);
	}
	private void prepare(int[]sh, int off)
	{
		this.baseShape = new int[sh.length];
		Arrays.fill(baseShape, 1);
		overlap(base.shape, baseShape);
		// System.out.println("====== base = " + Arrays.toString(base.shape) + ",\nbaseb= " + Arrays.toString(baseShape) + ",\ncurr = " + Arrays.toString(shape));
		this.sum = sumShapes(baseShape, sum); // sum shapes according to baseShape.
		// System.out.println(Arrays.toString(sum));
		this.dim = sh.length;
		this.offset = off;
		this.length = length(this.shape);//  sum.length == 0 ?1: sum[0];
		// System.out.println(Arrays.toString(sum));
	}
	public Storage get(int...index)
	{
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
				int shapeInd=index[i]; // Math.min(index[i], baseShape[i]);
				// check if the index at i is not out o bound.
				if (shapeInd >= shape[i])
					throw new IndexOutOfBoundsException();
				newPos += shapeInd * (i == index.length - 1 ?1: sum[i + 1]); // sum[i+1] -> maybe a problem.
				// System.out.println("== " + newPos);
			}
			// System.out.println("final pos to return " + newPos);
			return new Storage(base, new int[]{1}, offset + newPos);
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
					int shapeInd = index[i]; // Math.min(index[i], baseShape[i]);
					if (shapeInd >= shape[i])
						throw new IndexOutOfBoundsException();

					newPos += shapeInd * sum[i + 1];
				}
				// System.out.println("== " + (sm));
			}
			// System.out.println("new pos " + newPos);
			// System.out.println("new length " + newLength);
			// System.out.println("new Shape " + Arrays.toString(newShape));
			// System.out.println("returning array.");
			return new Storage(base, newShape, offset + newPos);
		}
		// return null;
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
		if (index.length > shape.length)
			throw new IndexOutOfBoundsException();
		if (index.length < shape.length)
		{
			// if the index length is less than the shape length. we fill the rest with (0). eg.
			// eg index =[5] -> we change into [0,0,5]  assume if the shape was [2,3,6];
			// todo.
		}
		int newPos=0;
		for (int i=0;i < index.length;i++)
		{
			int shapeInd =  Math.min(index[i], baseShape[i] - 1);
			// baseShape[i] -1 ; because . the baseShape minimum value is 1, but 1 means it's acess index is 0, so to make it zero we need to -1;
			// the big issue for 2 days;
			// check if the index at i is not out of bound.
			if (shapeInd >= shape[i])
				throw new IndexOutOfBoundsException();
			newPos += shapeInd * (i == index.length - 1 ?1: sum[i + 1]);
		}
		int finalIndex=(offset + newPos);
		// System.out.println("off = " + offset + ", newP = " + newPos + ", final pos = " + finalIndex + ", len= " + length);
		// System.out.print("--" + finalIndex + "--" + offset + "--");
		return base.values[(offset + (finalIndex % length)) % base.length];
	}
	@Override
	public float getFlat(int index)
	{
		if (index >= length || index < 0)
		 	throw new IndexOutOfBoundsException();
		int ind=index;
		// competiionally expensive. avoid it u can.
		// System.out.println("getting value at index =" + index);
		int[] indShape=new int[this.shape.length];
		for (int i=this.shape.length - 1;i >= 0;i--) // count down starts from shape.length -1 down to 0.
		{
			indShape[i] = index % this.shape[i];
			index = index / this.shape[i];
		}
		// System.out.println("shape at index (" + ind + ") = " + Arrays.toString(indShape));
		/*
		 merge the getFloat loop inside this method.
		 in that case we can use one loop instead o two loops iterate through the same shape.
		 // TO-DO merge loops to this method and getride of getFloat(indShape); call.
		 */
		return getFloat(indShape);
		/*
		 the code down below is just for access index without considering shape and broadcasting.
		 */
		// System.out.println("input index " + index);
		// System.out.println("computed index " + index);

		// index = offset + index;
		// System.out.println("real index " + index);
		// return base.values[index % base.length];
		// throw new IndexOutOfBoundsException();
	}
	/*
	 the above function is used for many functions.
	 like view,reshape,...
	 */
	public Storage brodcast(int...newShape)
	{
		// check if the new shape is brodcastable with the older one.
		// if not throw an error. if i is brodcastable shape then move old shape to original shape,
		// then make this shape to thi.shape;
		if (!isBrodcastable(this.shape, newShape))
			throw new IndexOutOfBoundsException("not brodcastable shape " + Arrays.toString(newShape) + " with " + Arrays.toString(this.shape));
		if (this.shape.length == newShape.length)
			for (int i=0;i < shape.length;i++)
				this.shape[i] = newShape[i];
		else
			this.shape = Arrays.copyOf(newShape, newShape.length);
		prepare(shape, offset);
		return this;
	}
	public boolean isBrodcastable(int[]orgShape, int[]tarShape)
	{
		if (tarShape.length < orgShape.length)
			return false;
		int len=tarShape.length - orgShape.length;
		// System.out.println("ch3cking... len =" + len);
		for (int i=0;i < orgShape.length;i++)
			if (!(orgShape[i] == tarShape[len + i] || (orgShape[i] == 1 && tarShape[len + i] > 0)))
				return false;
		// System.out.println("brodcastable shape " + Arrays.toString(tarShape) + " with " + Arrays.toString(orgShape));
		return true;
	}
	@Override
	public String toString()
	{
		return "storage(dim :" + dim + ", length :" + length + ", shape :" + Arrays.toString(shape) + ", baseShape " + Arrays.toString(baseShape) + ", offset :" + offset + ")";
	}
}
