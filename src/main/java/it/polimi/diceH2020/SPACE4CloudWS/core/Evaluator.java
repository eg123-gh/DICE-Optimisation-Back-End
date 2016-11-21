/*
Copyright 2016 Michele Ciavotta
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

import it.polimi.diceH2020.SPACE4Cloud.shared.settings.Models;
import it.polimi.diceH2020.SPACE4Cloud.shared.settings.Scenarios;
import it.polimi.diceH2020.SPACE4Cloud.shared.solution.*;
import it.polimi.diceH2020.SPACE4CloudWS.engines.EngineProxy;
import it.polimi.diceH2020.SPACE4CloudWS.services.DataService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class Evaluator implements IEvaluator {

	@Autowired
	private DataProcessor dataProcessor;

	@Autowired
	private DataService dataService;

	private Matrix matrix;

	private Solution solution;

	private int registeredSolutionsPerJob;

	private long executionTime;

	@Autowired
	private EngineProxy engineProxy;

	@Override
	public double evaluate(Solution solution) {
		Double cost = solution.getLstSolutions().parallelStream().mapToDouble(this::calculateCostPerJob).sum();
		solution.getLstSolutions().parallelStream().forEach(this::evaluateFeasibility);
		solution.setEvaluated(true);

		if (dataService.getScenario() == Scenarios.PrivateAdmissionControlWithPhysicalAssignment) {
			int activeNodes = 0;
			if(dataService.getScenario().getModel().equals(Models.BIN_PACKING)){
				if(solution.getActiveNodes()!=null && !solution.getActiveNodes().isEmpty()){
					for(Boolean b : solution.getActiveNodes().values()){
						if(b){
							activeNodes++;
						}
					}
				}
				cost = solution.getPrivateCloudParameters().get().getE() * activeNodes;
			}
		}

		solution.setCost(cost);
		return cost;
	}

	@Override
	public double evaluate(SolutionPerJob solutionPerJob) {
		double cost = calculateCostPerJob(solutionPerJob);
		evaluateFeasibility(solutionPerJob);
		return cost;
	}

	private double calculateCostPerJob(SolutionPerJob solPerJob) {
		double deltaBar = solPerJob.getDeltaBar();
		double rhoBar = solPerJob.getRhoBar();
		double sigmaBar = solPerJob.getSigmaBar();
		int currentNumberOfUsers = solPerJob.getNumberUsers();
		int maxNumberOfUsers =  solPerJob.getJob().getHup();
		System.out.println("Hup-H "+maxNumberOfUsers+"-"+currentNumberOfUsers);
		double cost = deltaBar * solPerJob.getNumOnDemandVM() + rhoBar * solPerJob.getNumReservedVM()
				+ sigmaBar * solPerJob.getNumSpotVM() +
				(maxNumberOfUsers - currentNumberOfUsers) * solPerJob.getJob().getPenalty();
		solPerJob.setCost(cost);
		return cost;
	}

	private boolean evaluateFeasibility(SolutionPerJob solPerJob) {
		if (solPerJob.getDuration() <= solPerJob.getJob().getD()) {
			solPerJob.setFeasible(true);
			return true;
		}
		solPerJob.setFeasible(false);
		return false;
	}

	void calculateDuration(@NonNull Solution sol) {
		long exeTime = dataProcessor.calculateDuration(sol);
		Phase phase = new Phase(PhaseID.EVALUATION, exeTime);
		sol.addPhase(phase);
	}

	void calculateDuration(@NonNull Matrix matrix, @NonNull Solution solution){
		this.matrix = matrix;
		this.solution = solution;
		executionTime = 0L;
		registeredSolutionsPerJob = 0;
		dataProcessor.calculateDuration(matrix);
	}

	public synchronized void register(SolutionPerJob spj, long executionTime){
		boolean finished = true;
		boolean error = false;
		this.executionTime += executionTime;
		//optimalNVMGivenH[spj.getJob().getId()-1][h-1] = nVM;
		matrix.get(spj.getId())[spj.getNumberUsers()-matrix.getHlow(spj.getId())] = spj;

		registeredSolutionsPerJob++;

		System.out.println("evaluated solution: "+ registeredSolutionsPerJob+" of "+matrix.getNumCells());//TODOd elete

		if(registeredSolutionsPerJob != matrix.getNumCells() ) finished = false;

		if(matrix.getAllSolutions().stream().map(SolutionPerJob::getNumberVM).anyMatch(s->s<0)){
			error = true;
			engineProxy.getEngine().error();
		}

		if(finished&&!error){
			solution.setEvaluated(false);
			Phase phase = new Phase(PhaseID.EVALUATION, this.executionTime);
			solution.addPhase(phase);
			engineProxy.getEngine().evaluated();
		}
	}
}
