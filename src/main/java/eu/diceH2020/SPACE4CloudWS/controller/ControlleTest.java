package eu.diceH2020.SPACE4CloudWS.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eu.diceH2020.SPACE4CloudWS.jpaRepositories.JobRepository;
import eu.diceH2020.SPACE4CloudWS.jpaRepositories.ProviderRepository;
import eu.diceH2020.SPACE4CloudWS.jpaRepositories.TypeVMRepository;
import eu.diceH2020.SPACE4CloudWS.messages.InstanceData;
import eu.diceH2020.SPACE4CloudWS.model.Job;
import eu.diceH2020.SPACE4CloudWS.model.Key;
import eu.diceH2020.SPACE4CloudWS.model.Provider;
import eu.diceH2020.SPACE4CloudWS.model.TypeVM;
import eu.diceH2020.SPACE4CloudWS.service.DataService;

@RestController
@Profile("test")
public class ControlleTest {

	@Autowired
	DataService dataService;
	
	// I could use the daoService, but this class is only for testing purposes
	@Autowired
	JobRepository jobRepository;
	@Autowired
	TypeVMRepository typeVMRepository;
	@Autowired
	ProviderRepository providerRepository;

	@RequestMapping(method = RequestMethod.GET, value = "appldata")
	@Profile("test")
	public @ResponseBody InstanceData appldata(){
		return dataService.getData();
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "job")
	@Profile("test")
	public @ResponseBody Job postJob(@RequestBody Job jb){
		jobRepository.saveAndFlush(jb);
		Job job = jobRepository.findOne(jb.getIdjob());
		return job;
	}
	@RequestMapping(method = RequestMethod.POST, value = "typeVM")
	@Profile("test")
	public @ResponseBody TypeVM postTypeVM(@RequestBody TypeVM typeVM){
		typeVMRepository.saveAndFlush(typeVM);
		Provider provider = new Provider();
		provider.setName("Amazon");
		Key key = new Key("T1", provider);
		TypeVM tVM = typeVMRepository.findOne(key);
		return tVM;
	}
	@RequestMapping(method = RequestMethod.GET, value = "providers")
	@Profile("test")
	public @ResponseBody List<Provider> getProvider(){
		return providerRepository.findAll();
	}
	@RequestMapping(method = RequestMethod.GET, value = "typeVM")
	@Profile("test")
	public @ResponseBody List<TypeVM> getTypeVM(){
		return typeVMRepository.findAll();
	}
	
}
