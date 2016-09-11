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
package it.polimi.diceH2020.SPACE4CloudWS.fineGrainedLogicForOptimization;

public class ChannelInfo{

	private ReactorConsumer consumer;
	private States state;

	public ChannelInfo(ReactorConsumer consumer){
		this.consumer = consumer;
		this.state = States.READY;
	}

	public States getState(){
		return state;
	}

	public void setState(States state){
		this.state = state;
	}

	public ReactorConsumer getConsumer(){
		return consumer;
	}
}
