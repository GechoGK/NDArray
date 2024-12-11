package gss.math;

import java.util.*;

import static gss.math.Util.*;

public class NDArray
{
	public Storage storage;
	public int[] shape;

	public NDArray(Storage str)
	{
		this.storage = str;
		this.shape = storage.shape;
	}
	public NDArray(int...shape)
	{
		this.storage = new Storage(shape);
		this.shape = storage.shape;
	}
}
