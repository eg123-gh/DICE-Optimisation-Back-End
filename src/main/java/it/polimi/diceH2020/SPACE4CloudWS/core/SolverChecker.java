/*
Copyright 2016-2017 Eugenio Gianniti

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

import it.polimi.diceH2020.SPACE4Cloud.shared.settings.Scenario;
import it.polimi.diceH2020.SPACE4Cloud.shared.settings.Settings;
import it.polimi.diceH2020.SPACE4Cloud.shared.settings.PerformanceSolverType;
import it.polimi.diceH2020.SPACE4Cloud.shared.settings.Technology;
import it.polimi.diceH2020.SPACE4Cloud.shared.solution.SolutionPerJob;
import it.polimi.diceH2020.SPACE4CloudWS.services.DataService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class SolverChecker {
    @Setter(onMethod = @__(@Autowired))
    private DataProcessor dataProcessor;

    @Setter(onMethod = @__(@Autowired))
    private DataService dataService;

    Technology enforceSolverSettings (List<SolutionPerJob> solutionsPerJob) {
        Scenario scenario = dataService.getScenario();
        Technology technology = scenario.getTechnology();
        Settings override = new Settings();

        switch (technology) {
            case STORM:
                override.setPerformanceSolverType(PerformanceSolverType.SPNSolver);
                dataProcessor.changeSettings(override);
                break;
            case HADOOP:
            case SPARK:
                boolean needsSPN = hasModelInputFiles (solutionsPerJob, ".net");
                if (needsSPN)
                   override.setPerformanceSolverType (PerformanceSolverType.SPNSolver);

                boolean needsQN = hasModelInputFiles (solutionsPerJob, ".jsimg");
                if (needsQN)
                   override.setPerformanceSolverType (PerformanceSolverType.QNSolver);

                boolean needDAGSIM = hasModelInputFiles (solutionsPerJob, ".txt");
                if (needDAGSIM)
                   override.setPerformanceSolverType (PerformanceSolverType.DagSimSolver);
                if (needsQN || needsSPN || needDAGSIM)
                   dataProcessor.changeSettings (override);
                break;
            default:
                throw new RuntimeException ("The required technology is still not implemented");
        }

        return scenario.getTechnology ();
    }

    private boolean hasModelInputFiles (List<SolutionPerJob> solutionsPerJob, String extension) {
        long modelFilesCount = solutionsPerJob.stream ().mapToLong (
                solutionPerJob ->
                        dataProcessor.retrieveInputFiles (extension, solutionPerJob.getParentID (),
                                solutionPerJob.getId (), dataProcessor.getProviderName (),
                                solutionPerJob.getTypeVMselected ().getId ()).size ()
        ).sum ();

        return modelFilesCount > 0;
    }
}
