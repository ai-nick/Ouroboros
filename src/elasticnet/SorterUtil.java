package elasticnet;

import java.util.HashMap;

public class SorterUtil {
	//quick sort on fitness 
	public void quick_sort_big_dumb(int[] sort_array, HashMap<Integer, Double> sort_dict, int left, int right)
	{
		int left_start = left;
		int pivot = right;
		right--;
		while(left<right)
		{
			if(sort_dict.get(sort_array[left]) > sort_dict.get(sort_array[pivot]))
			{
				if(sort_dict.get(sort_array[right]) < sort_dict.get(sort_array[pivot]))
				{
					int t = sort_array[left];
					sort_array[left] = sort_array[right];
					sort_array[right] = t;
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
				if(sort_dict.get(sort_array[right]) < sort_dict.get(sort_array[pivot]))
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
		if(sort_dict.get(sort_array[left]) > sort_dict.get(sort_array[pivot]))
		{
			int t = sort_array[left];
			sort_array[left] = sort_array[pivot];
			sort_array[pivot] = t;
		}
		else
		{
			int t = sort_array[left+1];
			sort_array[left+1] = sort_array[pivot];
			sort_array[pivot] = t;
			left++;
		}
		if(left == right)
		{
			left++;
			right--;
		}
		if(right > left_start+1)
		{
			quick_sort_big_dumb(sort_array, sort_dict, left_start, right);	
		}
		if(left < pivot-1)
		{
			quick_sort_big_dumb(sort_array, sort_dict, left, pivot);	
		}
	}

}
