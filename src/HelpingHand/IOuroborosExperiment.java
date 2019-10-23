package HelpingHand;

import java.util.ArrayList;

import elasticnet.Genome;

public interface IOuroborosExperiment {
	
	public void RefreshDataSet();
	
	public void EvaluateGenomes(ArrayList<Genome> genomes);

	public void CheckNetEvalStatus();
	
	public void GetTrainDataSet(Object idx_start);
}
