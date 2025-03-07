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
			/*
			 protocol...
			 type = float -> string
			 requireGradient = false -> boolean
			 shape = [2,3] -> array
			 array = [1,2,3,...] -> array

			 */
			JSONObject obj=new JSONObject("{}");
			obj.put("type", "float");
			obj.put("requiresGradient", ar.requiresGradient());
			JSONArray arr=new JSONArray();
			int[]sh=ar.getShape();
			for (int s:sh)
				arr.put(s);
			obj.put("shape", arr);
			float[]dt=ar.base.data.data;
			arr = new JSONArray();
			for (float f:dt)
				arr.put(f);
			obj.put("array", arr);
			String jsonString=obj.toString(0);
			FileOutputStream fos=new FileOutputStream(path);
			fos.write(jsonString.getBytes());
			fos.flush();
			fos.close();
			// Util.print("array saved as json file");
		}
		catch (Exception e)
		{
			Util.print("error :" + e);
			e.printStackTrace();
		}
	}
	public static void saveTEXT(NDArray ar, String path)
	{
		/*
		 protocols
		 type = float
		 requiresGradient = false
		 shape = [1,2,3]
		 array = [1,2,3,...]
		 */
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
			// Util.print("array saved as text file");
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
			String ext=".ndbin";
			if (!path.endsWith(ext))
				path += ext;
			DataOutputStream dos=new DataOutputStream(new FileOutputStream(path));
			int tpgrd=0b10000000;
			if (ar.requiresGradient())
				tpgrd |= 0b01000000; // requiresGrad true
			else
				tpgrd |= 0b00000000; // requoresGrad false.
			dos.writeInt(tpgrd); // write type and requiresGradient -> int
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
			// Util.print("array saved as binary file");
		}
		catch (Exception e)
		{
			Util.print("error :" + e);
			e.printStackTrace();
		}
	}
	public static NDArray load(String path) throws IOException,JSONException
	{
		/*
		 // the type of file is determined poorly by the extension of the file.
		 .txt = textFile (FileType.TEXT)
		 .json = jsonFile (FileType.JSON)
		 .bin = binaryFile (FileType.BINARY)
		 */
		String ext=path.substring(path.lastIndexOf(".")).toLowerCase();
		if (ext.equals(".txt"))
			return loadText(path);
		else if (ext.equals(".json"))
			return loadJSON(path);
		else if (ext.equals(".ndbin"))
			return loadBinary(path);
		else throw new RuntimeException("unknown file type with extension :" + ext + ",  or pass FileType to determine the typeFile. load(String,FileType);"); 
	}
	public static NDArray load(String path, FileType type) throws IOException,JSONException
	{
		if (type == FileType.JSON)
			return loadJSON(path);
		else if (type == FileType.TEXT)
			return loadText(path);
		else if (type == FileType.BINARY)
			return loadBinary(path);
		else throw new RuntimeException("unknown FileType :" + type);
	}
	public static NDArray loadJSON(String path) throws JSONException,IOException
	{
		/*
		 protocol...
		 type = float -> string
		 requireGradient = false -> boolean
		 shape = [2,3] -> array
		 array = [1,2,3,...] -> array

		 */
		String jsonText=Util.readString(path);
		JSONObject obj=new JSONObject(jsonText);
		// ignore type for now.
		boolean grad=obj.getBoolean("requiresGradient"); // get requiresGrad -> string
		JSONArray arr=obj.getJSONArray("shape"); // getShape ---v parse it. -> int
		int[]shape=new int[arr.length()];
		for (int i=0;i < shape.length;i++)
			shape[i] = arr.getInt(i);
		arr = obj.getJSONArray("array"); // get the array data. ---v parse it. -> float
		float[] arrayData=new float[arr.length()];
		for (int i=0;i < arrayData.length;i++)
			arrayData[i] = (float)arr.getDouble(i);
		// done prepare the NDArray.
		NDArray arOut=new NDArray(shape, arrayData).setEnableGradient(grad);
		return arOut;
	}
	public static NDArray loadText(String path) throws IOException
	{
		/*
		 protocols
		 type = float
		 requiresGradient = false
		 shape = [1,2,3]
		 array = [1,2,3,...]
		 */
		String txt=Util.readString(path);
		// split the text by new line.
		String[] lines=txt.split("\n");
		// iterate over each line and parse the result.
		boolean grad=false;
		int[] shape=null;
		float[] arrData=null;
		for (String line:lines)
		{
			// each line is key and value separated using "=", if the line doesn't contain "=" oass to next.
			if (!line.contains("="))
				continue;
			String key=line.substring(0, line.indexOf("=")).trim();
			String val= line.substring(line.indexOf("=") + 1).trim();
			// Util.print("key =" + key + "||| value =" + val);
			if (key.equals("type"))
			{/*ignored for now*/}
			else if (key.equals("requiresGradient"))
			{
				// parse value as boolean.
				grad = Boolean.valueOf(val);
			}
			else if (key.equals("shape"))
			{
				// the type of shape us array, we need to parde it.
				val = val.substring(1, val.length() - 1).trim(); // trying to remove "[" and "]"
				String[] shArrs=val.split(",");
				shape = new int[shArrs.length];
				for (int i=0;i < shArrs.length;i++)
				{
					if (shArrs[i].trim().length() == 0) // sometimes empty "," may found. so skip it.
						continue;
					shape[i] = Integer.parseInt(shArrs[i]);
				}
			}
			else if (key.equals("array")) // the same as shape, copied!
			{
				// the type of shape us array, we need to parde it.
				val = val.substring(1, val.length() - 1).trim(); // trying to remove "[" and "]"
				String[] shArrs=val.split(",");
				arrData = new float[shArrs.length];
				for (int i=0;i < shArrs.length;i++)
				{
					if (shArrs[i].trim().length() == 0) // sometimes empty "," may found. so skip it.
						continue;
					arrData[i] = Float.parseFloat(shArrs[i]);
				}
			}
		}
		if (shape == null) // if shape is null there is no way to construct the array. so throw an error.
			throw new RuntimeException("unable to read shape!!!");
		// finally prepare the array and return it.
		NDArray arrOut=null;
		if (arrData == null)// if arrayData is null we can construct the array with "0"s inside, bu5 inform the user that arrayData can't be read.
		{
			Util.print("unable to read array data returning with array filled with \"0\"s");
			arrOut = new NDArray(shape).setEnableGradient(grad);
		}
		else
			arrOut = new NDArray(shape, arrData).setEnableGradient(grad);
		return arrOut;
	}
	public static NDArray loadBinary(String path) throws IOException
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
		DataInputStream dis=new DataInputStream(new FileInputStream(path));
		boolean grad=false;
		int[] shape=null;
		float[] arrData=null;
		// read type but ignored.
		int flag=dis.readInt();
		int type=flag & 0b10000000; // type 0b10000000 = float 0b00000000 = int. atleast for now.
		//   ^--- type no use here.
		int gradFlag=flag & 0b01000000; // grad 0b01000000 = true 0b00000000 = false;
		grad = gradFlag == 0b01000000 ?true: false;
		byte shapeLen=dis.readByte(); // length of the shape;
		shape = new int[shapeLen];
		for (int i=0;i < shapeLen;i++)
			shape[i] = dis.readInt();
		// next we read array data length.
		int dataLen=dis.readInt();
		arrData = new float[dataLen];
		for (int i=0;i < dataLen;i++)
			arrData[i] = dis.readFloat();
		// done prepare array.
		if (shape == null) // if shape is null there is no way to construct the array. so throw an error.
			throw new RuntimeException("unabke to read shape");
		NDArray arrOut=null;
		if (arrData == null) // if arrayData is null we can construct the array with "0"s inside, bu5 inform the user that arrayData can't be read.
		{Util.print("unable to read array data returning with array filled with \"0\"s");
			arrOut = new NDArray(shape).setEnableGradient(grad);
		}
		else
			arrOut = new NDArray(shape, arrData).setEnableGradient(grad);
		return arrOut;
	}
	public enum FileType
	{
		JSON,
		TEXT,
		BINARY;
	}
}
