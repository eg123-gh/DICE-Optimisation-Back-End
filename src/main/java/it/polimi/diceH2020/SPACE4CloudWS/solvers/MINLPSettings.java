package it.polimi.diceH2020.SPACE4CloudWS.solvers;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "minlp")
public class MINLPSettings implements ConnectionSettings {
	private String address;

	private String password;

	private int port = 22;

	private String username;

	private String remoteWorkDir;

	private String amplDirectory;

	private String solverPath;

	private String pubKeyFile;

	private String knownHosts;

	private boolean forceClean;

	public String getAddress() {
		return address;
	}

	public String getPassword() {
		return password;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	public String getUsername() {
		return username;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	public void setUsername(String username) {
		this.username = username;
	}


	/**
	 * @return the amplDirectory
	 */
	public String getAmplDirectory() {
		return amplDirectory;
	}

	/**
	 * @param amplDirectory
	 *            the amplDirectory to set
	 */
	public void setAmplDirectory(String amplDirectory) {
		this.amplDirectory = amplDirectory;
	}

	/**
	 * @return the solverPath
	 */
	public String getSolverPath() {
		return solverPath;
	}

	/**
	 * @param solverPath
	 *            the solverPath to set
	 */
	public void setSolverPath(String solverPath) {
		this.solverPath = solverPath;
	}

	/**
	 * @return the remoteWorkDir
	 */
	public String getRemoteWorkDir() {
		return remoteWorkDir;
	}

	/**
	 * @param remoteWorkDir
	 *            the remoteWorkDir to set
	 */
	public void setRemoteWorkDir(String remoteWorkDir) {
		this.remoteWorkDir = remoteWorkDir;
	}

	public String getPubKeyFile() {
		return pubKeyFile;
	}

	public void setPubKeyFile(String pubKeyFile) {
		this.pubKeyFile = pubKeyFile;
	}

	public String getKnownHosts() {
		return knownHosts;
	}

	public void setKnownHosts(String knownHosts) {
		this.knownHosts = knownHosts;
	}

	public boolean isForceClean() {
		return forceClean;
	}

	public void setForceClean(boolean forceClean) {
		this.forceClean = forceClean;
	}

}
