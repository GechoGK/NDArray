package gss.math;

import java.util.*;

public class Value
{
	public Data data;
	public int index;
	public float val,grad;
	public ValueGradFunc func;
	public List<Value> args=new ArrayList<>();

	public Value(Data d, int ind)
	{
		this.data = d;
		this.index = ind;
	}
	public Value(float dt)
	{
		this.val = dt;
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
	public Value add(Value other)
	{
		Value v=new Value(val + other.val);
		v.setOP(additionGrad, this, other);
		return v;
	}
	public static ValueGradFunc additionGrad =new ValueGradFunc(){
		@Override
		public void backward(Value self, Value[] args)
		{
			Value a1=args[0];
			Value a2=args[1];
			a1.setGrad(self.grad);
			a2.setGrad(self.grad);
		}
	};
	public Value sub(Value other)
	{
		Value v=new Value(val - other.val);
		v.setOP(subtractGrad, this, other);
		return v;
	}
	public static ValueGradFunc subtractGrad =new ValueGradFunc(){
		@Override
		public void backward(Value self, Value[] args)
		{
			Value a1=args[0];
			Value a2=args[1];
			a1.setGrad(self.grad);
			a2.setGrad(-self.grad);
		}
	};
	public Value mul(Value other)
	{
		Value v=new Value(val * other.val);
		v.setOP(multiplyGrad, this, other);
		return v;
	}
	public static ValueGradFunc multiplyGrad =new ValueGradFunc(){
		@Override
		public void backward(Value self, Value[] args)
		{
			Value a1=args[0];
			Value a2=args[1];
			a1.setGrad(self.grad * a2.val);
			a2.setGrad(self.grad * a1.val);
		}
	};
	public Value div(Value other)
	{
		Value v=new Value(val / other.val);
		v.setOP(divisionGrad, this, other);
		return v;
	}
	public static ValueGradFunc divisionGrad =new ValueGradFunc(){
		@Override
		public void backward(Value self, Value[] args)
		{
			Value a1=args[0];
			Value a2=args[1];
			a1.setGrad(self.grad * 1 / a2.val);
			a2.setGrad(-self.grad * a1.val / (a2.val * a2.val));
		}
	};
	public Value pow(Value other)
	{
		Value v=new Value((float)Math.pow(val , other.val));
		v.setOP(powerGrad, this, other);
		return v;
	}
	public static ValueGradFunc powerGrad =new ValueGradFunc(){
		@Override
		public void backward(Value self, Value[] args)
		{
			Value a1=args[0];
			Value a2=args[1];
			a1.setGrad(self.grad * a2.val * (float)Math.pow(a1.val, a2.val - 1));
			a2.setGrad(self.grad * (float)Math.pow(a1.val, a2.val) * (float)Math.log(a1.val));
		}
	};
	public static abstract class ValueGradFunc
	{
		public abstract void backward(Value self, Value...args);
	}
}
