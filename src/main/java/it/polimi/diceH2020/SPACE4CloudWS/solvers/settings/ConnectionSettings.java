/*
Copyright 2016-2017 Eugenio Gianniti
Copyright 2016 Michele Ciavotta

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
package it.polimi.diceH2020.SPACE4CloudWS.solvers.settings;

public interface ConnectionSettings extends ShallowCopyable<ConnectionSettings> {
    String getAddress();

    String getPassword();

    Integer getPort();

    String getUsername();

    String getPrivateKeyFile();

    String getKnownHosts();

    boolean isForceClean();

    String getRemoteWorkDir();

    double getAccuracy();

    String getSolverPath();

    Integer getMaxDuration();

    void setAccuracy(double accuracy);

    void setMaxDuration(Integer duration);

    boolean isCleanRemote();
}
