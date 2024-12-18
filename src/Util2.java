import gss.math.*;
import java.util.*;

public class Util2
{
	void bench() throws Exception
	{
		// int[] dt={1,2, 3,4, 5,6,   7,8, 9,10, 11,12,13,14,15,16,17,18,19,20,21,22,23,24};
		int[] sh={4,4,4};
		int[] sm=new int[sh.length];
		for (int i=0;i < sh.length;i++)
			sm[i] = sum(sh, i);
		int[] dt=new int[sum(sh, 0)];
		for (int i=0;i < dt.length;i++)
		{
			dt[i] = i - 5;
		}
		System.out.println("sum =" + sum(sh, 0));

		for (int d=0;d < sh[0];d++)
		{
			for (int r=0;r < sh[1];r++)
			{
				for (int c=0;c < sh[2];c++)
				{
					int p=dt[get(sh, sm, d, r, c)]; // 147 millis in average.
					// int p=dt[q][j][k][d][r][c]; // 52 millis in average
					System.out.print(p + ", ");
				}
				System.out.println();
			}
			System.out.println("..");
		}
		System.out.println("done!");
	}
	public int get(int[]sh, int[] sum, int...idx)
	{
		int ps=idx[idx.length - 1];
		for (int i=1;i < idx.length;i++)
		{
			ps += idx[i - 1] * (sum == null ?sum(sh, i): sum[i]);
		}
		return ps; // dt[idx[0] * sh[1] + idx[1]];
	}
	int sum(int[] sh, int start)
	{
		int sm=1;
		for (int i=start;i < sh.length;i++)
			sm *= sh[i];
		return sm;
	}
}
