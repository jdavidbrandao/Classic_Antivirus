package core;

public abstract class Antivirus {
	protected String path;
	
	public Antivirus(String path) {
		this.path = path;
	}
	
	public abstract byte[] getFileContent();
}
