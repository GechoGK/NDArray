package gss.arr;

import java.util.*;
import gss.math.*;
import org.json.*;
import java.io.*;

public class NDIO
{
	public static NDArray arange(float end)
	{
		return arange(0, end, 1);
	}
	public static NDArray arange(float str, float end)
	{
		return arange(str, end, 1);
	}
	public static NDArray arange(float str, float end, float inc)
	{
		float[] f=Util.range(str, end, inc);
		NDArray ar=new NDArray(f);
		return ar;
	}
	public static NDArray zeros(int...shape)
	{
		return value(shape, 0, false);
	}
	public static NDArray zeros(int...shape, boolean requiresGrad)
	{
		return value(shape, 0, requiresGrad);
	}
	public static NDArray zerosAlike(NDArray arr)
	{
		return value(arr.getShape(), 0, false);
	}
	public static NDArray zerosAlike(NDArray arr, boolean requiresGrad)
	{
		return value(arr.getShape(), 0, requiresGrad);
	}
	// one new array.
	public static NDArray ones(int...shape)
	{
		return value(shape, 1, false);
	}
	public static NDArray ones(int...shape, boolean requiresGrad)
	{
		return value(shape, 1, requiresGrad);
	}
	public static NDArray onesAlike(NDArray arr)
	{
		return value(arr.getShape(), 1, false);
	}
	public static NDArray onesAlike(NDArray arr, boolean requiresGrad)
	{
		return value(arr.getShape(), 1, requiresGrad);
	}
	// array with custom value.
	public static NDArray value(int[]shape, float val)
	{
		return value(shape, val, false);
	}
	public static NDArray value(int[]shape, float val, boolean requiresGrad)
	{
		NDArray arr=new NDArray(shape).setEnableGradient(requiresGrad);
		for (int i=0;i < arr.getLength();i++)
			arr.base.data.setData(i, val);
		// Arrays.fill(arr.storage.base.values, val);
		return arr;
	}
	public static NDArray fromArray(int[] shape, float...arr)
	{
		return new NDArray(shape, arr);
	}
	// the seed value can be -1.
	public static NDArray rand(int...shape)
	{
		return rand(shape, false, 128); // change 128 to -1
	}
	public static NDArray rand(int[] shape, int seed)
	{
		return rand(shape, false, seed);
	}
	public static NDArray rand(int[]shape, boolean requiresGrad)
	{
		return rand(shape, requiresGrad, 128); // change 128 to -1
	}
	public static NDArray rand(int[]shape, boolean reqiresGrad, int seed)
	{
		NDArray arr=new NDArray(shape).setEnableGradient(reqiresGrad);
		Random r=null;
		if (seed != -1)
			r = new Random(seed);
		else
			r = new Random();
		for (int i=0;i < arr.getLength();i++)
			arr.base.data.setData(i, r.nextFloat());
		return arr;
	}
	public static void toFile(NDArray ar, String path)
	{
		saveJSON(ar, path);
	}
	public static void toFile(NDArray ar, String path, FileType type)
	{
		if (type == FileType.JSON)
			saveJSON(ar, path);
		else if (type == FileType.TEXT)
			saveTEXT(ar, path);
		else if (type == FileType.BINARY)
			saveBINARY(ar, path);
		else throw new RuntimeException("unknown File type (valid files are (JSON, TEXT, BINARY)) instead found ::" + type);
	}
	public static void saveJSON(NDArray ar, String path)
	{
		try
		{
			JSONObject obj=new JSONObject("{}");
			obj.put("type", "float");
			obj.put("requireGradient", ar.requiresGradient());
			JSONArray arr=new JSONArray();
			int[]sh=ar.getShape();
			for (int s:sh)
				arr.put(s);
			obj.put("shape", arr.toString());
			float[]dt=ar.base.data.data;
			arr = new JSONArray();
			for (float f:dt)
				arr.put(f);
			obj.put("array", arr.toString());
			String jsonString=obj.toString(0);
			FileOutputStream fos=new FileOutputStream(path);
			fos.write(jsonString.getBytes());
			fos.flush();
			fos.close();
			Util.print("array saved as json file");
		}
		catch (Exception e)
		{
			Util.print("error :" + e);
			e.printStackTrace();
		}
	}
	public static void saveTEXT(NDArray ar, String path)
	{
		try
		{
			StringBuilder sb=new StringBuilder();
			sb.append("type = float\n");
			sb.append("requiresGradient = ");
			sb.append(ar.requiresGradient());
			sb.append("\n");
			sb.append("shape = ");
			sb.append(Arrays.toString(ar.getShape()).replace(" ", ""));
			sb.append("\n");
			sb.append("array = ");
			sb.append(Arrays.toString(ar.base.data.data).replace(" ", ""));
			sb.append("\n");
			String textData=sb.toString();
			FileOutputStream fos=new FileOutputStream(path);
			fos.write(textData.getBytes());
			fos.flush();
			fos.close();
			Util.print("array saved as text file");
		}
		catch (Exception e)
		{
			Util.print("error :" + e);
			e.printStackTrace();
		}
	}
	public static void saveBINARY(NDArray ar, String path)
	{
		try
		{
			/*
			 binary format
			 —————————————
			 |  1  | 0|1 | ->type : 1 = float | requiresGrad : 0|1 false or true
			 —————————————
			 .. ^--- int
			 int -> length of shape.
			 -- 1,2,3.4,5.6,7...--
			 .. ^--- int
			 int -> length of data.
			 -- 1.0, 1.3, 4.8... --
			 .. ^--- float
			 */
			DataOutputStream dos=new DataOutputStream(new FileOutputStream(path));
			int tpgrd=0b10000000;
			if (ar.requiresGradient())
				tpgrd |= 0b01000000; // requiresGrad true
			else
				tpgrd |= 0b00000000; // requoresGrad false.
			dos.writeByte(tpgrd); // write type and requiresGradient -> byte
			int[] sh=ar.getShape();
			dos.writeByte(sh.length); // write shape length -> byte
			for (int s:sh)
				dos.writeInt(s); // write each individual shape items. -> int
			float[] dt=ar.base.data.data;
			dos.writeInt(dt.length); // write array data -> int
			for (float f:dt)
				dos.writeFloat(f); // write each individual array items. -> float
			dos.flush();
			dos.close(); // done saving.
			Util.print("array saved as binary file");
		}
		catch (Exception e)
		{
			Util.print("error :" + e);
			e.printStackTrace();
		}
	}
	public enum FileType
	{
		JSON,
		TEXT,
		BINARY;
	}
}
