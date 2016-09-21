/*
Copyright 2016 Jacopo Rigoli

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
package it.polimi.diceH2020.SPACE4CloudWS.ml;

import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.JobMLProfile;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.JobProfile;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.SVRFeature;
import it.polimi.diceH2020.SPACE4Cloud.shared.solution.SolutionPerJob;
import it.polimi.diceH2020.SPACE4CloudWS.services.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class MLPredictor {

	@Autowired
	private DataService dataService;

	private Map<String,MLPrediction> predictedSPJ = new HashMap<>();//caches SPJ, in order to recalculate only xi TODO @Cacheable 

	/**
	 * Precondition:
	 * DataService has M,V (received from JSON or retrieved from DB)
	 * SolutionPerJob has h,m,v,D and all the required parameters in JobMLProfile
	 *
	 * @param spj
	 * @return
	 */
	public Optional<BigDecimal> approximate(SolutionPerJob spj) {
		if(predictedSPJ.containsKey(spj.getId())){
			return retrievePrediction(spj);
		}else{
			return calculatePrediction(spj);
		}
	}


	private Optional<BigDecimal> calculatePrediction(SolutionPerJob spj){
		JobMLProfile features = dataService.getMLProfile(spj.getId());
		JobProfile profile = spj.getProfile();

		double deadline = spj.getJob().getD();
		double chi_c = calculateChi_c(features);
		double chi_h = calculateChi_h(features);
		double chi_0 = calculateChi_0(profile,features);
		int h = spj.getNumberUsers();

		double duration = deadline; //TODO
		double xi = calculateXi(spj);
		int c = (int) Math.ceil((double)(chi_c / (deadline - chi_h*h - chi_0)));

		spj.setXi(xi);
		spj.setDuration(duration);
		spj.setNumberContainers(c); //TODO

		predictedSPJ.put(spj.getId(), new MLPrediction(deadline,chi_c,chi_h,chi_0));
		return Optional.of(BigDecimal.valueOf(duration));
	}

	private Optional<BigDecimal> retrievePrediction(SolutionPerJob spj){
		MLPrediction prediction = predictedSPJ.get(spj.getId());

		double deadline = prediction.getDeadline();
		double chi_c = prediction.getChi_c();
		double chi_h = prediction.getChi_h();
		double chi_0 = prediction.getChi_0();

		int h = spj.getNumberUsers();

		double duration = deadline; //TODO
		double xi = calculateXi(spj);
		int c = (int) Math.ceil((double)(chi_c / (deadline - chi_h*h - chi_0)));

		spj.setXi(xi);
		spj.setDuration(duration);
		spj.setNumberContainers(c); //TODO

		return Optional.of(BigDecimal.valueOf(duration));
	}

	private double calculateDefaultParametersContribution(JobMLProfile features){
		//mu_t + b*sigma_t - (sigma_t/sigma_x)*w_x*mu_x - (sigma_t/sigma_h)*w_h*mu_h 
		double mu_t = features.getMu_t();
		double sigma_t = features.getSigma_t();
		double b = features.getB();
		double sigma_x = features.getClassFeature("x").getSigma();
		double mu_x = features.getClassFeature("x").getMu();
		double w_x = features.getClassFeature("x").getW();
		double sigma_h = features.getClassFeature("h").getSigma();
		double mu_h = features.getClassFeature("h").getMu();
		double w_h = features.getClassFeature("h").getW();

		return mu_t + b*sigma_t - (sigma_t/sigma_x)*w_x*mu_x - (sigma_t/sigma_h)*w_h*mu_h;
	}


	private double calculateChi_c(JobMLProfile features){
		double sigma_t = features.getSigma_t();
		double sigma_x = features.getClassFeature("x").getSigma();
		double w_x = features.getClassFeature("x").getW();

		return  (sigma_t/sigma_x)*w_x;
	}

	private double calculateChi_h(JobMLProfile features){
		double sigma_t = features.getSigma_t();
		double sigma_h = features.getClassFeature("h").getSigma();
		double w_h = features.getClassFeature("h").getW();

		return  (sigma_t/sigma_h)*w_h;
	}

	private double calculateXi(SolutionPerJob spj){
		double M = dataService.getMemory(spj.getTypeVMselected().getId());
		double m = spj.getJob().getM();
		double V = dataService.getNumCores(spj.getTypeVMselected().getId());
		double v = spj.getJob().getV();
		double xi = Math.min(M/m,V/v);

		return xi;
	}

	private double calculateChi_0(JobProfile profile,JobMLProfile features){

		double defaultParametersContribution = calculateDefaultParametersContribution(features);
		double featureContribution = 0;

		try {
			for (Map.Entry<String,SVRFeature> entry : features.getMlFeatures().entrySet()) {
				if(entry.getKey().equals("h")||entry.getKey().equals("x")) continue;

				double valueOfEntry = profile.get(entry.getKey());
				featureContribution += (features.getSigma_t()/entry.getValue().getSigma())*entry.getValue().getW()*(valueOfEntry-entry.getValue().getMu());
			}
		} catch (IllegalArgumentException e) {
			System.out.println("[MLPredictor] Missing a MLProfile feature parameter in Profile.");
		}

		return defaultParametersContribution + featureContribution ;
	}

	public void reinitialize() {
		predictedSPJ = new HashMap<>();
	}

}