/*
Copyright 2016 Jacopo Rigoli
Copyright 2016 Eugenio Gianniti

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package it.polimi.diceH2020.SPACE4CloudWS.core;

import it.polimi.diceH2020.SPACE4Cloud.shared.settings.CloudType;
import it.polimi.diceH2020.SPACE4Cloud.shared.settings.Technology;
import it.polimi.diceH2020.SPACE4Cloud.shared.solution.*;
import it.polimi.diceH2020.SPACE4CloudWS.services.DataService;
import it.polimi.diceH2020.SPACE4CloudWS.services.MINLPSolverProxy;
import it.polimi.diceH2020.SPACE4CloudWS.solvers.MINLPSolver;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
class Selector {

	@Autowired
	private MINLPSolverProxy minlpSolverProxy;

	@Autowired
	private DataService dataService; //TODO dataProcessor

	private final Logger logger = Logger.getLogger(getClass());

	/**
	 * Perform the selection of matrix cells to retrieve the best combination.
	 * One and only one cell per row (one H for each Job).
	 * Dimensionality reduction due to Admission Control: 
	 * for each class only one cell is selected, 
	 * one H for each job is chosen in order to maximize the cost. 
	 */
	void selectMatrixCells(Matrix matrix, Solution solution) {
		Instant first = Instant.now();
		Phase phase = new Phase();
		try {
			if(dataService.getScenario().getCloudType().equals(CloudType.PUBLIC)) {
				throw new RuntimeException("The required scenario does not require optimization");
			}
			phase.setId(PhaseID.SELECTION_KN);
			minlpSolverProxy.getMinlpSolver().evaluate(matrix, solution);
		} catch (MatrixHugeHoleException e) {
			logger.error("The matrix has too few feasible alternatives", e);
			solution.setFeasible(false);
         logger.trace("Feasibility of solution is " + solution.getFeasible());
			minlpSolverProxy.getMinlpSolver().initializeSpj(solution, matrix);
         logger.trace("Feasibility of solution is " + solution.getFeasible());
			return;
		}
		Instant after = Instant.now();
		phase.setDuration(Duration.between(first, after).toMillis());
		solution.addPhase(phase);
	}
}
