package heuristics;

import heuristics.IHeuristic.Solution;
import heuristics.IHeuristic.LevelHeuristics;
import model.Grid;

public interface RegroupHeuristics {
	
    Grid getGrid();
    
    String[][] getSolution();
    
    Solution UniqueCandidate();
    
    Solution oneCandidate();
    
    Solution twinsAndTriplet();
    
    Solution interactionsBetweenRegion();
    
    Solution identicalCandidates();
    
    Solution isolatedGroups();
    
    Solution mixedGroups();
    
    Solution XWing();
    
    Solution XYWing();
    
    Solution XYZWing();
    
    Solution getEasiestHeuristic();
    
    Solution getSolution(LevelHeuristics heuristic);
    
    int getDifficulty();
    
    int getMinDifficulty();
    
    int getMaxDifficulty();
    
    void resetDifficultyMemory();

	IHeuristic getEasiestLevel();
}
