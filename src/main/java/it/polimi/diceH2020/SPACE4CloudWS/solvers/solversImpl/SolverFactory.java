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
package it.polimi.diceH2020.SPACE4CloudWS.solvers.solversImpl;

import it.polimi.diceH2020.SPACE4Cloud.shared.settings.SolverType;
import it.polimi.diceH2020.SPACE4CloudWS.solvers.Solver;
import it.polimi.diceH2020.SPACE4CloudWS.solvers.settings.SettingsDealer;
import it.polimi.diceH2020.SPACE4CloudWS.solvers.solversImpl.QNSolver.QNSolver;
import it.polimi.diceH2020.SPACE4CloudWS.solvers.solversImpl.SPNSolver.SPNSolver;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by ciavotta on 11/02/16.
 */
@Component
public class SolverFactory {

    @Autowired
    private ApplicationContext ctx;
    @Autowired
    private SettingsDealer dealer;

    @Setter
    private SolverType type;

    @PostConstruct
    public void restoreDefaults() {
        type = dealer.getSolverDefaults().getType();
    }

    public Solver create() throws RuntimeException {
        switch (type) {
            case SPNSolver:
                return ctx.getBean(SPNSolver.class);
            case QNSolver:
                return ctx.getBean(QNSolver.class);
            default:
                throw new RuntimeException("Unrecognized solver type");
        }
    }
}
