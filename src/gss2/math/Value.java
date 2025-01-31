package gss2.math;

import java.util.*;

public class Value
{
	public float val,grad;
	public ValueGradFunc func;
	public List<Value> args=new ArrayList<>();

	public Value(float dt)
	{
		this.val = dt;
	}
	public void backward()
	{
		if (func != null)
			func.backward(this, args.toArray(new Value[0]));
	}
	public void setGrad(float v)
	{
		this.grad += v;
	}
	public Value setOP(ValueGradFunc fn, Value...arg)
	{
		this.func = fn;
		args.clear(); // new start.
		for (Value a:arg)
		{
			args.add(a);
		}
		return this;
	}
	public Value set(Value v)
	{
		this.func = v.func;
		this.args = v.args;
		this.val = v.val;
		return this;
	}
	public float getData()
	{
		return val;
	}
	public float getGrad()
	{
		return grad;
	}
	public Value add(Value other)
	{
		// System.out.println(getData());
		// System.out.println(other.getData());
		Value v=new Value(this.getData() + other.getData());
		v.setOP(additionGrad, this, other);
		// System.out.println("adding " + v);
		return v;
	}
	public static ValueGradFunc additionGrad =new ValueGradFunc(){
		@Override
		public void backward(Value self, Value[] args)
		{
			Value a1=args[0];
			Value a2=args[1];
			a1.setGrad(self.getGrad());
			a2.setGrad(self.getGrad());
		}
	};
	public Value sub(Value other)
	{
		Value v=new Value(getData() - other.getData());
		v.setOP(subtractGrad, this, other);
		return v;
	}
	public static ValueGradFunc subtractGrad =new ValueGradFunc(){
		@Override
		public void backward(Value self, Value[] args)
		{
			Value a1=args[0];
			Value a2=args[1];
			a1.setGrad(self.getGrad());
			a2.setGrad(-self.getGrad());
		}
	};
	public Value mul(Value other)
	{
		Value v=new Value(getData() * other.getData());
		v.setOP(multiplyGrad, this, other);
		return v;
	}
	public static ValueGradFunc multiplyGrad =new ValueGradFunc(){
		@Override
		public void backward(Value self, Value[] args)
		{
			Value a1=args[0];
			Value a2=args[1];
			a1.setGrad(self.getGrad() * a2.getData());
			a2.setGrad(self.getGrad() * a1.getData());
		}
	};
	public Value div(Value other)
	{
		Value v=new Value(getData() / other.getData());
		v.setOP(divisionGrad, this, other);
		return v;
	}
	public static ValueGradFunc divisionGrad =new ValueGradFunc(){
		@Override
		public void backward(Value self, Value[] args)
		{
			Value a1=args[0];
			Value a2=args[1];
			a1.setGrad(self.getGrad() * 1 / a2.getData());
			a2.setGrad(-self.getGrad() * a1.getData() / (a2.getData() * a2.getData()));
		}
	};
	public Value pow(Value other)
	{
		Value v=new Value((float)Math.pow(getData() , other.getData()));
		v.setOP(powerGrad, this, other);
		return v;
	}
	public static ValueGradFunc powerGrad =new ValueGradFunc(){
		@Override
		public void backward(Value self, Value[] args)
		{
			Value a1=args[0];
			Value a2=args[1];
			a1.setGrad(self.getGrad() * a2.getData() * (float)Math.pow(a1.getData(), a2.getData() - 1));
			a2.setGrad(self.getGrad() * (float)Math.pow(a1.getData(), a2.getData()) * (float)Math.log(a1.getData()));
		}
	};
	public static abstract class ValueGradFunc
	{
		// /* uncomment the when funushed debugging.
		private String name;
		public ValueGradFunc()
		{this.name = "unknown";}
		public ValueGradFunc(String nm)
		{this.name = nm;}
		@Override
		public String toString()
		{
			return "value grad " + name;
		}
		// */
		public abstract void backward(Value self, Value...args);
	}
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " :: " + func;
	}
}
