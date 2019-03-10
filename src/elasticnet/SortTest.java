package elasticnet;

import java.util.ArrayList;
import java.util.Random;

public class SortTest {
	
	ArrayList<Genome> genomes;
	
	int[] index_array;
	
	SortTest(int num_genomes)
	{
		index_array = new int[num_genomes];
		genomes = new ArrayList<Genome>();
		for(int ix = 0; ix < num_genomes; ix++)
		{
			Random rnd = new Random();
			double new_fit = 0.0 + 20.0 * rnd.nextDouble();
			genomes.add(new Genome(new_fit));
			index_array[ix] = ix;
		}
	}

	public void quick_sort_big_dumb(int left, int right)
	{
		int left_start = left;
		int pivot = right;
		right--;
		while(left<right)
		{
			if(this.genomes.get(this.index_array[left]).fitness > this.genomes.get(this.index_array[pivot]).fitness)
			{
				if(this.genomes.get(this.index_array[right]).fitness < this.genomes.get(this.index_array[pivot]).fitness)
				{
					int t = this.index_array[left];
					this.index_array[left] = this.index_array[right];
					this.index_array[right] = t;
					right--;
					left++;
				}
				else
				{
					right--;
				}
			}
			else
			{
				if(this.genomes.get(this.index_array[right]).fitness < this.genomes.get(this.index_array[pivot]).fitness)
				{
					left++;
				}
				else
				{
					left++;
					right--;
				}
			}
		}
		if(this.genomes.get(this.index_array[left]).fitness > this.genomes.get(this.index_array[pivot]).fitness)
		{
			int t = this.index_array[left];
			this.index_array[left] = this.index_array[pivot];
			this.index_array[pivot] = t;
		}
		else
		{
			int t = this.index_array[left+1];
			this.index_array[left+1] = this.index_array[pivot];
			this.index_array[pivot] = t;
			left++;
		}
		if(left == right)
		{
			left++;
			right--;
		}
		if(right > left_start+1)
		{
			quick_sort_big_dumb(left_start, right);	
		}
		if(left < pivot-1)
		{
			quick_sort_big_dumb(left, pivot);	
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SortTest test = new SortTest(155);
		for(int x = 0; x < 155; x++)
		{
			System.out.println(test.index_array[x]);
		}
		System.out.println("starting sort");
		test.quick_sort_big_dumb(0, test.index_array.length-1);
		
		for(int x = 0; x < 155; x++)
		{
			System.out.println(test.genomes.get(test.index_array[x]).fitness);
		}
	}

}
