package HelpingHand;
import elasticnet.*;
import java.util.ArrayList;

public interface IP2PExperiment {
	
	public void RefreshDataSet();
	
	public void EvaluateGenomes(ArrayList<Genome> genomes);

	public void CheckNetEvalStatus();
	
	public void GetTrainDataSet(Object idx_start);
}
