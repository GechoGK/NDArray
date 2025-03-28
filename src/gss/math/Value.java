package gss.math;

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
		this.grad = v.grad;
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
	public static ValueGradFunc additionGrad =new ValueGradFunc("addition gradient"){
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
		v.setOP(subtractionGrad, this, other);
		return v;
	}
	public static ValueGradFunc subtractionGrad =new ValueGradFunc("subtraction gradient"){
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
		v.setOP(multiplicationGrad, this, other);
		return v;
	}
	public static ValueGradFunc multiplicationGrad =new ValueGradFunc("multiplication gradient"){
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
	public static ValueGradFunc divisionGrad =new ValueGradFunc("division gradient"){
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
	public static ValueGradFunc powerGrad =new ValueGradFunc("power gradient"){
		@Override
		public void backward(Value self, Value[] args)
		{
			Value a1=args[0];
			Value a2=args[1];
			a1.setGrad(self.getGrad() * a2.getData() * (float)Math.pow(a1.getData(), a2.getData() - 1));
			a2.setGrad(self.getGrad() * (float)Math.pow(a1.getData(), a2.getData()) * (float)Math.log(a1.getData()));
		}
	};
	public Value log()
	{
		Value v=new Value((float)Math.log(getData()));
		v.setOP(logGrad, this);
		return v;
	}
	public static ValueGradFunc logGrad =new ValueGradFunc("log gradient"){
		@Override
		public void backward(Value self, Value[] args)
		{
			Value a1=args[0];
			a1.setGrad(self.getGrad() * (1 / a1.getData()));
		}
	};
	public Value log10()
	{
		Value v=new Value((float)Math.log10(getData()));
		v.setOP(log10Grad, this);
		return v;
	}
	public static ValueGradFunc log10Grad =new ValueGradFunc("log10 gradient"){
		@Override
		public void backward(Value self, Value[] args)
		{
			Value a1=args[0];
			a1.setGrad(self.getGrad() * (1 / (a1.getData() * (float)Math.log(10))));
		}
	};
	public Value exp()
	{
		Value v=new Value((float)Math.exp(getData()));
		v.setOP(expGrad, this);
		return v;
	}
	public static ValueGradFunc expGrad =new ValueGradFunc("exp gradient"){

		// needs more accurate math.

		@Override
		public void backward(Value self, Value[] args)
		{
			Value a1=args[0];
			a1.setGrad(self.getGrad() * (float)Math.exp(a1.getData()));
		}
	};
	public Value step()
	{
		Value v=new Value(getData());
		v.setOP(stepGrad, this);
		return v;
	}
	// default data value.
	public static ValueGradFunc stepGrad=new ValueGradFunc("step"){
		@Override
		public void backward(Value self, Value[] args)
		{
			// if (args.length == 0)
			// 	return;
			Value a1=args[0];
			a1.setGrad(self.getGrad());
		}
	};
	public Value tanh()
	{
		Value v=new Value((float)Math.tanh(getData()));
		v.setOP(tanhGrad, this);
		return v;
	}
	public static ValueGradFunc tanhGrad =new ValueGradFunc("tanh gradient"){
		@Override
		public void backward(Value self, Value[] args)
		{
			Value a1=args[0];
			float th=(float)Math.tanh(a1.getData());
			a1.setGrad(self.getGrad() * (1f - th * th));
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
